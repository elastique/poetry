package nl.elastique.poetry.data.json;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.elastique.poetry.core.annotations.Nullable;
import nl.elastique.poetry.core.lang.callbacks.FailureCallback;
import nl.elastique.poetry.web.http.HttpRequestHandler;
import nl.elastique.poetry.core.lang.Callback;

/**
 * This HttpRequestJsonPersister starts an HTTP request, parses the result as JSON and then processes all mappings.
 */
public class HttpRequestJsonPersister
{
    private final MappedJsonPersister mMappedJsonPersister;

    private final List<Callback<Object[]>> mMappingCallbacks = new ArrayList<>();

    private State mState = State.IDLE;

    private enum State
    {
        IDLE,
        PERSISTING,
        ERROR
    }

    /**
     * @param writableDatabase
     * @param options JsonPersisterImpl.Options value compound (use bitwise OR operator)
     */
    public HttpRequestJsonPersister(SQLiteDatabase writableDatabase, int options)
    {
        if (Looper.myLooper() != Looper.getMainLooper())
        {
            throw new RuntimeException("must create RestToSqlitePersister on the main thread");
        }

        mMappedJsonPersister = new MappedJsonPersister(writableDatabase, options);
    }

    public HttpRequestJsonPersister(SQLiteDatabase writableDatabase)
    {
        this(writableDatabase, 0);
    }

    public HttpRequestJsonPersister addMapping(String path, Class<?> type)
    {
        Callback<Object[]> callback = new FailureCallback<Object[]>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                mState = State.ERROR;
            }
        };

        mMappingCallbacks.add(callback);
        mMappedJsonPersister.addMapping(path, type, callback);

        return this;
    }

    /**
     *
     * @param path
     * @param type
     * @param callback onFailure receives: JsonPathException, org.json.JSONException
     * @return
     */
    public HttpRequestJsonPersister addMapping(String path, Class<?> type, final Callback<Object[]> callback)
    {
        Callback<Object[]> proxy_callback = new Callback<Object[]>()
        {
            @Override
            public void onSuccess(Object[] object)
            {
                callback.onSuccess(object);
            }

            @Override
            public void onFailure(Throwable caught)
            {
                callback.onFailure(caught);

                mState = State.ERROR;
            }
        };

        mMappingCallbacks.add(proxy_callback);
        mMappedJsonPersister.addMapping(path, type, proxy_callback);

        return this;
    }

    public void persist(Context context, HttpRequestHandler httpRequestHandler, Callback<JSONObject> jsonFallback)
    {
        httpRequestHandler.execute(context, new HttpResponseCallback(jsonFallback));
    }

    public void persist(Context context, HttpRequestHandler httpRequestHandler)
    {
        httpRequestHandler.execute(context, new HttpResponseCallback());
    }

    private class HttpResponseCallback implements Callback<HttpResponse>
    {
        private @Nullable final Callback<JSONObject> mJsonCallback;

        public HttpResponseCallback(Callback<JSONObject> callback)
        {
            mJsonCallback = callback;
        }

        public HttpResponseCallback()
        {
            mJsonCallback = null;
        }

        @Override
        public void onSuccess(final HttpResponse response)
        {
            try
            {
                mState = State.PERSISTING;

                final String data = EntityUtils.toString(response.getEntity());

                JSONTokener tokener = new JSONTokener(data);
                final JSONObject json_result = new JSONObject(tokener);

                mMappedJsonPersister.persist(json_result);

                if (mJsonCallback != null)
                {
                    if (State.ERROR.equals(mState))
                    {
                        mJsonCallback.onFailure(new Exception("one or more mappings failed"));
                    }
                    else
                    {
                        mJsonCallback.onSuccess(json_result);
                    }
                }
            }
            catch (IOException | JSONException e)
            {
                processFailure(e);
            }

            mState = State.IDLE;
        }

        @Override
        public void onFailure(Throwable caught)
        {
            processFailure(caught);
        }

        private void processFailure(Throwable caught)
        {
            for (Callback<Object[]> callback : mMappingCallbacks)
            {
                callback.onFailure(caught);
            }

            if (mJsonCallback != null)
            {
                mJsonCallback.onFailure(caught);
            }
        }
    }
}

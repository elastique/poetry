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

import nl.elastique.poetry.web.http.HttpRequestHandler;
import nl.elastique.poetry.core.interfaces.Callback;

/**
 * This HttpRequestJsonPersister starts an HTTP request, parses the result as JSON and then processes all mappings.
 */
public class HttpRequestJsonPersister
{
    private final MappedJsonPersister mMappedJsonPersister;

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

    public HttpRequestJsonPersister addMapping(String path, Class<?> type)
    {
        mMappedJsonPersister.addMapping(path, type);
        return this;
    }

    /**
     *
     * @param path
     * @param type
     * @param callback onFailure receives: JsonPathException, org.json.JSONException
     * @return
     */
    public HttpRequestJsonPersister addMapping(String path, Class<?> type, Callback<Object[]> callback)
    {
        mMappedJsonPersister.addMapping(path, type, callback);
        return this;
    }

    public void persist(Context context, HttpRequestHandler httpRequest, Callback<JSONObject> callback)
    {
        httpRequest.execute(context, new HttpResponseCallback(callback));
    }

    private class HttpResponseCallback implements Callback<HttpResponse>
    {
        private final Callback<JSONObject> mCallback;

        public HttpResponseCallback(Callback<JSONObject> callback)
        {
            mCallback = callback;
        }

        @Override
        public void onSuccess(final HttpResponse response)
        {
            try
            {
                final String data = EntityUtils.toString(response.getEntity());

                JSONTokener tokener = new JSONTokener(data);
                final JSONObject json_result = new JSONObject(tokener);

                mMappedJsonPersister.persist(json_result);

                mCallback.onSuccess(json_result);
            }
            catch (IOException e)
            {
                mCallback.onFailure(e);
            }
            catch (JSONException e)
            {
                mCallback.onFailure(e);
            }
        }

        @Override
        public void onFailure(Throwable caught)
        {
            mCallback.onFailure(caught);
        }
    }
}

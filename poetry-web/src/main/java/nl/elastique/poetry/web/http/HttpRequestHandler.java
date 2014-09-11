package nl.elastique.poetry.web.http;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import nl.elastique.poetry.core.annotations.Nullable;
import nl.elastique.poetry.web.http.exceptions.HttpStatusException;
import nl.elastique.poetry.core.lang.Callback;

/**
 * Wraps a request so it:
 *  - broadcasts event on begin, end, success and failure
 *  - executes in a background thread
 *  - provides a single success/failure callback in the foreground thread
 */
public class HttpRequestHandler
{
    public static class Broadcasts
    {
        // Broadcast that is sent when a request begins
        public static final String ACTION_BEGIN = HttpRequestHandler.class.getName() + ".BEGIN";
        // Broadcast that is sent when a request ends
        public static final String ACTION_END = HttpRequestHandler.class.getName() + ".END";
        // Broadcast that is sent when a request succeeds
        public static final String ACTION_SUCCESS = HttpRequestHandler.class.getName() + ".SUCCESS";
        // Broadcast that is sent when a request fails (and a HttpRequest is placed under the "error" intent data extra)
        public static final String ACTION_FAILURE = HttpRequestHandler.class.getName() + ".FAILURE";
    }

    private static final Logger sLogger = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final HttpClient mHttpClient;

    private final HttpUriRequest mHttpRequest;

    public HttpRequestHandler(HttpUriRequest httpUriRequest)
    {
        this(new DefaultHttpClient(), httpUriRequest);
    }

    public HttpRequestHandler(HttpClient httpClient, HttpUriRequest httpRequest)
    {
        mHttpClient = httpClient;
        mHttpRequest = httpRequest;
    }

    /**
     *
     * @param context
     * @param callback optional callback
     */
    private void executeSynchronous(Context context, Callback<HttpResponse> callback)
    {
        HttpResponse response = null;

        try
        {
            broadcast(context, Broadcasts.ACTION_BEGIN);

            if (sLogger.isDebugEnabled())
            {
                sLogger.debug(String.format("started %s %s", mHttpRequest.getMethod(), mHttpRequest.getRequestLine().getUri().toString()));
            }

            response = mHttpClient.execute(mHttpRequest);

            if (response == null)
            {
                throw new IOException("no response from HttpRequest");
            }

            if (sLogger.isDebugEnabled())
            {
                sLogger.debug(String.format("finished %s %s (status code %d)", mHttpRequest.getMethod(), mHttpRequest.getRequestLine().getUri().toString(), response.getStatusLine().getStatusCode()));
            }

            int status_code = response.getStatusLine().getStatusCode();

            if (status_code < 200 || status_code >= 300)
            {
                throw new HttpStatusException(response, status_code);
            }

            handleSuccess(context, response, callback);
        }
        catch (IOException e)
        {
            handleFailure(context, callback, e);
        }
        catch (HttpStatusException e)
        {
            handleFailure(context, callback, e);
        }
        finally
        {
            broadcast(context, Broadcasts.ACTION_END);
        }
    }

    /**
     * Asynchronous execution.
     * @param context
     * @param listener will be called from a background thread
     */
    public void execute(final Context context, final Callback<HttpResponse> listener)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                executeSynchronous(context, listener);
            }
        }).start();
    }

    protected void handleFailure(final Context context, @Nullable final Callback<HttpResponse> callback, final Throwable throwable)
    {
        if (sLogger.isDebugEnabled())
        {
            String error_message = throwable != null ? throwable.getMessage() : "unknown error";
            sLogger.warn(String.format("failed: %s %s (%s)", mHttpRequest.getMethod(), mHttpRequest.getRequestLine().getUri().toString(), error_message));
        }

        if (callback != null)
        {
            callback.onFailure(throwable);
        }

        broadcast(context, Broadcasts.ACTION_FAILURE);
    }

    protected void handleSuccess(final Context context, @Nullable final HttpResponse response, @Nullable final Callback<HttpResponse> listener)
    {
        if (listener != null)
        {
            listener.onSuccess(response);
        }

        broadcast(context, Broadcasts.ACTION_SUCCESS);
    }

    private static void broadcast(Context context, String action)
    {
        Intent intent = (new Intent()).setAction(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

package nl.elastique.poetry.web.http;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Pair;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import nl.elastique.poetry.web.http.exceptions.HttpStatusException;

public class DefaultRequestHandler implements RequestHandler
{
    private static final Logger sLogger = LoggerFactory.getLogger(RequestHandler.class);

    private static final int sConnectionTimeout = 10000;
    private static final int sSocketTimeout = 10000; // read operation timeout, non-zero value, -1 means infinite timeout

    public static class Broadcasts
    {
        public static final String sException = "nl.elastique.poetry.web.http.DefaultRequestHandler.sException";
    }

    private Pair<String, String> mBasicAuthentication = null;

    /**
     * Processes Exception and optionally translate it into a new one
     */
    protected Exception translateException(Context context, Exception exception)
    {
        return exception;
    }

    protected DefaultHttpClient createClient()
    {
        DefaultHttpClient client = new DefaultHttpClient();

        HttpParams http_params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(http_params, sConnectionTimeout);
        HttpConnectionParams.setSoTimeout(http_params, sSocketTimeout);

        return client;
    }

    @Override
    public final HttpResponse execute(final Context context, HttpUriRequest request) throws Exception
    {
        HttpResponse response = null;

        try
        {
            if (sLogger.isDebugEnabled())
            {
                sLogger.debug("start {} {}", request.getMethod(), request.getRequestLine().getUri());
            }

            DefaultHttpClient client = createClient();

            if (mBasicAuthentication != null)
            {
                String credentials = mBasicAuthentication.first + ":" + mBasicAuthentication.second;
                String basic_auth_header = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
                request.setHeader("Authorization", basic_auth_header);
            }

            response = client.execute(request);

            if (response == null)
            {
                throw new IOException("HttpResponse is null");
            }

            if (sLogger.isDebugEnabled())
            {
                sLogger.debug("finished {} {} (status code {})", request.getMethod(), request.getRequestLine().getUri(), response.getStatusLine().getStatusCode());
            }

            int status_code = response.getStatusLine().getStatusCode();

            if (status_code < 200 || status_code >= 300)
            {
                throw new HttpStatusException(response, status_code);
            }

            return response;
        }
        catch (Exception e)
        {
            if (sLogger.isWarnEnabled())
            {
                int status_code = (response != null) ? response.getStatusLine().getStatusCode() : -1;
                sLogger.debug("failed {} {} (status code {})", request.getMethod(), request.getRequestLine().getUri(), status_code);
            }

            Exception translated_exception = translateException(context, e); // interprets error response and re-throws error
            broadcast(context, translated_exception);
            throw translated_exception;
        }
    }

    public void setBasicAuthentication(String user, String secret)
    {
        mBasicAuthentication = new Pair<>(user, secret);
    }


    // Broadcast


    protected static void broadcast(Context context, Throwable caught)
    {
        Intent intent = new Intent();
        intent.putExtra("message", caught.getMessage());
        intent.putExtra("class", caught.getClass());
        intent.setAction(Broadcasts.sException);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

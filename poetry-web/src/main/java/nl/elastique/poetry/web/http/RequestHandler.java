package nl.elastique.poetry.web.http;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public interface RequestHandler
{
    public HttpResponse execute(final Context context, HttpUriRequest request) throws Exception;
}

package nl.elastique.poetry.web.http.exceptions;

import org.apache.http.HttpResponse;

public class HttpResponseException extends Exception
{
    private HttpResponse mHttpResponse;

    public HttpResponseException(HttpResponse httpResponse)
    {
        mHttpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse()
    {
        return mHttpResponse;
    }
}

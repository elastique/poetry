package nl.elastique.poetry.web.http.exceptions;

import org.apache.http.HttpResponse;

public class HttpStatusException extends HttpResponseException
{
    private final int mStatusCode;

    public HttpStatusException(HttpResponse httpResponse, int statusCode)
    {
        super(httpResponse);

        mStatusCode = statusCode;
    }

    public int getStatusCode()
    {
        return mStatusCode;
    }
}

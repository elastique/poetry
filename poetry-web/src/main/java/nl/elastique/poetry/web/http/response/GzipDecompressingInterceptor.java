package nl.elastique.poetry.web.http.response;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

import nl.elastique.poetry.web.http.entities.GzipDecompressingEntity;

public class GzipDecompressingInterceptor implements HttpResponseInterceptor
{
    private final boolean mForce;

    public GzipDecompressingInterceptor()
    {
        mForce = false;
    }

    public GzipDecompressingInterceptor(boolean force)
    {
        mForce = force;
    }

    @Override
    public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException
    {
        if (mustCreateWrappingEntity(httpResponse))
        {
            httpResponse.setEntity(new GzipDecompressingEntity(httpResponse.getEntity()));
        }
    }

    private boolean mustCreateWrappingEntity(HttpResponse httpResponse)
    {
        if (httpResponse.getEntity() == null)
        {
            return false;
        }

        if (mForce)
        {
            return true;
        }

        Header content_encoding_header = httpResponse.getEntity().getContentEncoding();

        if (content_encoding_header != null)
        {
            for (HeaderElement header_element : content_encoding_header.getElements())
            {
                if ("gzip".equalsIgnoreCase(header_element.getName()))
                {
                    return true;
                }
            }
        }

        return false;
    }
}

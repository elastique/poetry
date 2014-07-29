package nl.elastique.poetry.web.utils;

import android.util.Base64;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class HttpUtils
{
    public static Header getBasicAuthHeader(String username, String password)
    {
        String secret = username + ":" + password;
        String encoded_secret = Base64.encodeToString(secret.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        String value = String.format("Basic %s", encoded_secret);
        return new BasicHeader("Authorization", value);
    }
}

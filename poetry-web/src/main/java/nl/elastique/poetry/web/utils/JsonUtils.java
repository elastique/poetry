package nl.elastique.poetry.web.utils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtils
{
    public static JSONObject createObject(final HttpResponse response) throws Exception
    {
        String data = EntityUtils.toString(response.getEntity());
        JSONTokener tokener = new JSONTokener(data);
        return new JSONObject(tokener);
    }

    public static JSONArray createArray(final HttpResponse response) throws Exception
    {
        String data = EntityUtils.toString(response.getEntity());
        JSONTokener tokener = new JSONTokener(data);
        return new JSONArray(tokener);
    }
}

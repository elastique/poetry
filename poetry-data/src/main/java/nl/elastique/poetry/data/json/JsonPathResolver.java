package nl.elastique.poetry.data.json;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Given a JSON Object { 'root' : { 'key' : 'value' }}
 * It can resolve the String "value" when given the path "root/key"
 */
public class JsonPathResolver
{
    private final String mPath;

    public JsonPathResolver(String path)
    {
        mPath = path;
    }

    /**
     * @param object must be a JSONObject or JSONArray
     * @throws JsonPathException
     */
    public Object resolvePath(Object object) throws JsonPathException
    {
        boolean is_json_object = JSONObject.class.isAssignableFrom(object.getClass());
        boolean is_json_array = JSONArray.class.isAssignableFrom(object.getClass());

        if (!is_json_object && !is_json_array)
        {
            throw new IllegalArgumentException("object must be JSONObject or JSONArray");
        }

        boolean is_root_path = mPath == null || mPath.length() == 0;

        if (is_json_array && !is_root_path)
        {
            throw new IllegalArgumentException("JSONArray can only be parsed at root");
        }

        if (is_root_path)
        {
            return object;
        }

        String[] elements = mPath.split("\\.");

        JSONObject current = (JSONObject)object;

        int last_index = elements.length - 1;

        for (int i = 0; i < elements.length; ++i)
        {
            Object child_object;

            try
            {
                child_object = current.get(elements[i]);
            }
            catch (Exception e)
            {
                throw new JsonPathException(String.format("failed to fetch element \"%s\"", elements[i]));
            }

            if (JSONArray.class.isAssignableFrom(child_object.getClass()))
            {
                if (i == last_index)
                {
                    return child_object;
                }
                else
                {
                    throw new JsonPathException("array element for " + elements[i] + " is not the last element on the path " + mPath);
                }
            }
            else if (JSONObject.class.isAssignableFrom(child_object.getClass()))
            {
                current = (JSONObject)child_object;
            }
            else
            {
                throw new JsonPathException("can't parse element for " + elements[i]
                        + " on path " + mPath
                        + " because the type " + child_object.getClass().getName() + " is not supported");
            }
        }

        return current;
    }
}

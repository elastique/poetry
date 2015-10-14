package nl.elastique.poetry.json;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JsonUtils
{
    public static Object getValue(JSONObject jsonObject, String jsonKey, Class<?> type) throws JSONException
    {
        if (!jsonObject.has(jsonKey))
        {
            return null;
        }

        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type))
        {
            return jsonObject.getInt(jsonKey);
        }
        else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type))
        {
            return jsonObject.getLong(jsonKey);
        }
        else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))
        {
            return jsonObject.getBoolean(jsonKey);
        }
        else if (String.class.isAssignableFrom(type))
        {
            return jsonObject.getString(jsonKey);
        }
        else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type))
        {
            return jsonObject.getDouble(jsonKey);
        }
        else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type))
        {
            return ((Double)jsonObject.getDouble(jsonKey)).floatValue();
        }
        else
        {
            throw new RuntimeException("unsupported type: " + type.getName() + " (only Integer, Long, Boolean, String, Double and Float are supported)");
        }
    }

    public static boolean copyContentValue(JSONObject jsonObject, String jsonKey, ContentValues values, String key) throws JSONException
    {
        if (!jsonObject.has(jsonKey))
        {
            return false;
        }

        Object value = jsonObject.get(jsonKey);

        return copyValue(value, key, values);
    }

    public static boolean copyValue(Object value, String key, ContentValues values)
    {
        Class<?> value_class = value.getClass();

        if (Integer.class == value_class || int.class == value_class)
        {
            values.put(key, (Integer)value);
        }
        else if (Long.class == value_class || long.class == value_class)
        {
            values.put(key, (Long)value);
        }
        else if (Short.class == value_class || short.class == value_class)
        {
            values.put(key, (Short)value);
        }
        else if (Byte.class == value_class || byte.class == value_class)
        {
            values.put(key, (Byte)value);
        }
        else if (Boolean.class == value_class || boolean.class == value_class)
        {
            values.put(key, (Boolean)value);
        }
        else if (Float.class == value_class || float.class == value_class)
        {
            values.put(key, (Float)value);
        }
        else if (Double.class == value_class || double.class == value_class)
        {
            values.put(key, (Double)value);
        }
        else if (CharSequence.class.isAssignableFrom(value_class) || Date.class.isAssignableFrom(value_class))
        {
            values.put(key, value.toString());
        }
        else
        {
            return false;
        }

        return true;
    }
}
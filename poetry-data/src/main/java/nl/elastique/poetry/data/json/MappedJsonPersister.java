package nl.elastique.poetry.data.json;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import nl.elastique.poetry.core.lang.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Persists a JSONObject or a JSONArray given a path in the JSON tree.
 */
public class MappedJsonPersister
{
    private static final Logger sLogger = LoggerFactory.getLogger(MappedJsonPersister.class);

    private final List<PathMapping> mMappings = new ArrayList<PathMapping>();

    private final SQLiteDatabase mDatabase;

    private final int mOptions;

    public class PathMapping extends JsonPathResolver
    {
        private final Class<?> mType;

        private final Callback<Object[]> mCallback;

        public PathMapping(String path, Class<?> type)
        {
            this(path, type, null);
        }

        public PathMapping(String path, Class<?> type, Callback<Object[]> callback)
        {
            super(path);

            mType = type;
            mCallback = callback;
        }

        public Class<?> getType()
        {
            return mType;
        }

        protected void processFailure(Throwable caught)
        {
            if (mCallback != null)
            {
                mCallback.onFailure(caught);
            }
        }

        protected void processSuccess(Object[] updatedIds)
        {
            if (mCallback != null)
            {
                mCallback.onSuccess(updatedIds);
            }
        }
    }

    /**
     * @param writableDatabase
     * @param options JsonSqlitePersister.Options value compound (use bitwise OR operator)
     */
    public MappedJsonPersister(SQLiteDatabase writableDatabase, int options)
    {
        mDatabase = writableDatabase;
        mOptions = options;
    }

    public MappedJsonPersister(SQLiteDatabase writableDatabase)
    {
        this(writableDatabase, 0);
    }

    public MappedJsonPersister addMapping(String path, Class<?> type)
    {
        mMappings.add(new PathMapping(path, type));
        return this;
    }

    /**
     *
     * @param path
     * @param type
     * @param callback onFailure receives: JsonPathException, org.json.JSONException
     * @return
     */
    public MappedJsonPersister addMapping(String path, Class<?> type, Callback<Object[]> callback)
    {
        mMappings.add(new PathMapping(path, type, callback));
        return this;
    }

    public void persist(final JSONObject object)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            sLogger.warn("please call persist() on a background thread");
        }

        for (PathMapping mapper : mMappings)
        {
            persist(mapper, object);
        }
    }

    private void persist(final PathMapping mapper, JSONObject jsonObject)
    {
        Object resolved_data;

        try
        {
            resolved_data = mapper.resolvePath(jsonObject);
        }
        catch (JsonPathException e)
        {
            mapper.processFailure(e);
            return;
        }

        final Object[] updated_ids;

        if (JSONArray.class.isAssignableFrom(resolved_data.getClass()))
        {
            try
            {
                JSONArray json_array = (JSONArray)resolved_data;
                JsonPersister persister = new JsonPersister(mDatabase, mOptions);
                List<Object> id_list = persister.persistArray(mapper.getType(), json_array);
                updated_ids = id_list.toArray(new Object[id_list.size()]);
            }
            catch (JSONException e)
            {
                sLogger.error(String.format("JSON data type processing of %s failed: %s", mapper.getType().getName(), e.getMessage()));
                mapper.processFailure(e);
                return;
            }
        }
        else if (JSONObject.class.isAssignableFrom(resolved_data.getClass()))
        {
            try
            {
                JSONObject json_object = (JSONObject)resolved_data;
                JsonPersister persister = new JsonPersister(mDatabase, mOptions);
                final Object object_id = persister.persistObject(mapper.getType(), json_object);
                if (object_id != null)
                {
                    updated_ids = new Object[1];
                    updated_ids[0] = object_id;
                }
                else
                {
                    updated_ids = null;
                }
            }
            catch (Exception e)
            {
                sLogger.error(String.format("JSON data type processing of %s failed: %s", mapper.getType().getClass().getName(), e.getMessage()));
                mapper.processFailure(e);
                return;
            }
        }
        else
        {
            sLogger.error(String.format("Data type %s while processing %s is not supported", resolved_data.getClass().getName(), mapper.getType().getClass().getName()));
            mapper.processFailure(new RuntimeException("unsupported type: " + resolved_data.getClass().getName()));
            return;
        }

        mapper.processSuccess(updated_ids);
    }
}

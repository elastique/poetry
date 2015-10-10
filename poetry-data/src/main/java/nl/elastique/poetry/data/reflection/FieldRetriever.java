package nl.elastique.poetry.data.reflection;

import java.lang.reflect.Field;
import java.util.HashMap;

import nl.elastique.poetry.core.annotations.Nullable;

/**
 * FieldRetriever caches {@link Field} objects to improve performance.
 *
 * In a test with hundreds of objects, OrmliteReflection.findField() took over 50% CPU time.
 * When looking at the Android source code it shows that this is a fairly heavy method.
 * Considering that Poetry uses only a certain amount of model classes and fields, it
 * makes sense to cache this in memory.
 *
 * Reference: http://grepcode.com/file/repo1.maven.org/maven2/org.robolectric/android-all/4.4_r1-robolectric-1/libcore/reflect/AnnotationAccess.java#AnnotationAccess.getDeclaredAnnotation%28java.lang.reflect.AnnotatedElement%2Cjava.lang.Class%29
 */
public class FieldRetriever
{
    private final HashMap<Class<?>, HashMap<String, Field>> mCache = new HashMap<>();

    /**
     * Retrieve a {@link Field} for a model.
     */
    public @Nullable Field getField(Class<?> modelClass, String jsonKey)
    {
        // Try to retrieve it from cache
        Field field = getCachedField(modelClass, jsonKey);

        // If not cached, try reflection
        if (field == null)
        {
            field = OrmliteReflection.findField(modelClass, jsonKey);

            // Null values are also cached because it will make the next failure quicker
            setCachedField(modelClass, jsonKey, field);
        }

        return field;
    }

    private @Nullable Field getCachedField(Class<?> classObject, String fieldName)
    {
        HashMap<String, Field> field_map = mCache.get(classObject);

        return (field_map != null) ? field_map.get(fieldName) : null;
    }

    private void setCachedField(Class<?> classObject, String fieldName, Field field)
    {
        HashMap<String, Field> field_map = mCache.get(classObject);

        if (field_map == null)
        {
            field_map = new HashMap<>();
            mCache.put(classObject, field_map);
        }

        field_map.put(fieldName, field);
    }
}

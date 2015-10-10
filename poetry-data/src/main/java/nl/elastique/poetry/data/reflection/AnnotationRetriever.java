package nl.elastique.poetry.data.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import nl.elastique.poetry.core.annotations.Nullable;

/**
 * AnnotationRetriever caches annotations to improve performance.
 *
 * In a test with hundreds of objects, Field.getAnnotation() took over 50% CPU time.
 * When looking at the Android source code it shows that this is a fairly heavy method.
 * Considering that Poetry uses only a certain amount of model classes and fields, it
 * makes sense to cache this in memory.
 *
 * Reference: http://grepcode.com/file/repo1.maven.org/maven2/org.robolectric/android-all/4.4_r1-robolectric-1/libcore/reflect/AnnotationAccess.java#AnnotationAccess.getDeclaredAnnotation%28java.lang.reflect.AnnotatedElement%2Cjava.lang.Class%29
 */
public class AnnotationRetriever
{
    private final HashMap<Field, HashMap<Class<? extends Annotation>, Annotation> > mCache = new HashMap<>();

    /**
     * Retrieve a {@link Field} for a model.
     */
    public @Nullable <AnnotationType extends Annotation> AnnotationType getAnnotation(Field field, Class<AnnotationType> annotationClass)
    {
        // Try to retrieve it from cache
        Annotation annotation = getCachedAnnotation(field, annotationClass);

        // If not cached, try reflection
        if (annotation == null)
        {
            annotation = field.getAnnotation(annotationClass);

            // Null values are also cached because it will make the next failure quicker
            setCachedAnnotation(field, annotationClass, annotation);
        }

        return (AnnotationType)annotation;
    }

    private @Nullable Annotation getCachedAnnotation(Field field, Class<? extends Annotation> annotationClass)
    {
        HashMap<Class<? extends Annotation>, Annotation> annotation_map = mCache.get(field);

        return (annotation_map != null) ? annotation_map.get(annotationClass) : null;
    }

    private void setCachedAnnotation(Field field, Class<? extends Annotation> annotationClass, Annotation annotation)
    {
        HashMap<Class<? extends Annotation>, Annotation> annotation_map = mCache.get(field);

        if (annotation_map == null)
        {
            annotation_map = new HashMap<>();
            mCache.put(field, annotation_map);
        }

        annotation_map.put(annotationClass, annotation);
    }
}

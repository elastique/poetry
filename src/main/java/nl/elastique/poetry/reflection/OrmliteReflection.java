package nl.elastique.poetry.reflection;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import nl.elastique.poetry.annotations.Nullable;

/**
 * A set of reflection utilities for OrmLite to process and retrieve fields and annotations.
 *
 * @author Ken Van Hoeylandt
 */
// TODO: most of these calls should be cached as is done in AnnotationRetriever/FieldRetriever
public class OrmliteReflection
{
    // Reference: http://sourceforge.net/p/ormlite/code/HEAD/tree/ormlite-core/trunk/src/main/java/com/j256/ormlite/field/FieldType.java
    private static final String sForeignIdFieldSuffix = "_id";

    /**
     * Get the SQLite table name for an OrmLite model.
     * @param modelClass an OrmLite model class annotated with {@link DatabaseTable}
     * @return the SQLite table name
     */
    public static String getTableName(AnnotationRetriever annotationRetriever, Class<?> modelClass)
    {
        DatabaseTable table_annotation = annotationRetriever.getAnnotation(modelClass, DatabaseTable.class);

        if (table_annotation == null)
        {
            throw new RuntimeException("DatabaseTable annotation not found for " + modelClass.getName());
        }

        return getTableName(modelClass, table_annotation);
    }

    /**
     * Get the SQLite table name for an OrmLite model.
     * @param modelClass an OrmLite model class annotated with {@link DatabaseTable}
     * @param tableAnnotation the annotation to process
     * @return the SQLite table name
     */
    public static String getTableName(Class<?> modelClass, DatabaseTable tableAnnotation)
    {
        return !tableAnnotation.tableName().isEmpty() ? tableAnnotation.tableName() : modelClass.getSimpleName();
    }

    /**
     * Get SQLite column name for a given Field
     * @param field the model's field
     * @return the SQLite column name
     */
    public static String getFieldName(AnnotationRetriever annotationRetriever, Field field)
    {
        DatabaseField database_field = annotationRetriever.getAnnotation(field, DatabaseField.class);

        if (database_field == null)
        {
            throw new RuntimeException("DatabaseField annotation not found in " + field.getDeclaringClass().getName() + " for " + field.getName());
        }

        return getFieldName(field, database_field);
    }

    /**
     * Get SQLite column name for a given Field
     * @param field the model's field
     * @param databaseField the DatabaseField annotation for the specified Field
     * @return the SQLite column name
     */
    public static String getFieldName(Field field, DatabaseField databaseField)
    {
		if (!databaseField.columnName().isEmpty())
		{
			return databaseField.columnName();
		}
		else if (OrmliteReflection.isForeign(databaseField))
		{
			return field.getName() + sForeignIdFieldSuffix;
		}
		else
		{
			return field.getName();
		}
    }

    /**
     * @param databaseField the annotation to check
     * @return true if foreign() is true, foreignAutoRefresh() is true or foreignColumnName() is set to a non-empty string
     */
    public static boolean isForeign(DatabaseField databaseField)
    {
        return databaseField.foreign() || databaseField.foreignAutoRefresh() || !databaseField.foreignColumnName().isEmpty();
    }

    /**
     * @param databaseField the annotation to check
     * @return true if id() or generatedId() are true
     */
    public static boolean isId(DatabaseField databaseField)
    {
        return databaseField.id() || databaseField.generatedId();
    }

    /**
     * Retrieves the generic type argument: the type that is held by the specified ForeignCollection Field
     * @param field a {@link Field} that holds the type {@link com.j256.ormlite.dao.ForeignCollection}
     * @throws RuntimeException when the Field is not a ForeignCollection
     * @return the class
     */
    public static Class<?> getForeignCollectionParameterType(Field field)
    {
        if (!field.getType().equals(ForeignCollection.class))
        {
            throw new RuntimeException(field.getDeclaringClass().getName() + " declares the field \"" + field.getName() + "\" which is not a ForeignCollection but is annotated by ForeignCollectionField");
        }

        Type type = field.getGenericType();
        ParameterizedType parameterized_type = (ParameterizedType)type;
        Type child_type = parameterized_type.getActualTypeArguments()[0];

        return (Class<?>)child_type; // TODO: check conversion?
    }

    /**
     * Find a Field with a DatabaseField annotation that defines it as being an id column.
     * @return the Field or null
     */
    public static @Nullable Field findIdField(AnnotationRetriever annotationRetriever, Class<?> modelClass)
    {
        for (Field field : modelClass.getDeclaredFields())
        {
            DatabaseField database_field = annotationRetriever.getAnnotation(field, DatabaseField.class);

            if (database_field == null)
            {
                continue;
            }

            if (database_field.generatedId() || database_field.id())
            {
                return field;
            }
        }

        if (modelClass.getSuperclass() != null)
        {
            // Recursively check superclass
            return findIdField(annotationRetriever, modelClass.getSuperclass());
        }
        else
        {
            return null;
        }
    }

    /**
     * Find a Field with a DatabaseField annotation that defines it as foreign.
     * @return a Field or null
     */
    public static @Nullable Field findForeignField(AnnotationRetriever annotationRetriever, Class<?> parentClass, Class<?> findClass)
    {
        for (Field field : parentClass.getDeclaredFields())
        {
            DatabaseField database_field = annotationRetriever.getAnnotation(field, DatabaseField.class);

            if (database_field != null && isForeign(database_field) && findClass.isAssignableFrom(field.getType()))
            {
                return field;
            }
        }

        if (parentClass.getSuperclass() != null)
        {
            // Recursively check superclass
            return findForeignField(annotationRetriever, parentClass.getSuperclass(), findClass);
        }
        else
        {
            return null;
        }
    }
}

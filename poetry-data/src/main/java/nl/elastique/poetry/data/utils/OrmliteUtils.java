package nl.elastique.poetry.data.utils;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import nl.elastique.poetry.data.json.annotations.MapFrom;

/**
 * A set of utilities for OrmLite.
 *
 * @author Ken Van Hoeylandt
 */
public class OrmliteUtils
{
    // Reference: http://sourceforge.net/p/ormlite/code/HEAD/tree/ormlite-core/trunk/src/main/java/com/j256/ormlite/field/FieldType.java
    private static final String sForeignIdFieldSuffix = "_id";

    public static String getTableName(Class<?> c)
    {
        DatabaseTable table_annotation = c.getAnnotation(DatabaseTable.class);

        if (table_annotation == null)
        {
            throw new RuntimeException("DatabaseTable annotation not found for " + c.getName());
        }

        return !table_annotation.tableName().isEmpty() ? table_annotation.tableName() : c.getSimpleName();
    }

    public static String getTableName(Class<?> c, DatabaseTable tableAnnotation)
    {
        return !tableAnnotation.tableName().isEmpty() ? tableAnnotation.tableName() : c.getSimpleName();
    }

    public static String getFieldName(Field field)
    {
        DatabaseField database_field = field.getAnnotation(DatabaseField.class);

        if (database_field == null)
        {
            throw new RuntimeException("DatabaseField annotation not found in " + field.getDeclaringClass().getName() + " for " + field.getName());
        }

        return !database_field.columnName().isEmpty() ? database_field.columnName() : field.getName();
    }

    public static String getFieldName(Field field, DatabaseField databaseField)
    {
        return !databaseField.columnName().isEmpty() ? databaseField.columnName() : field.getName();
    }

    public static String getForeignFieldName(Field field, DatabaseField databaseField)
    {
        if (databaseField.foreignColumnName().isEmpty())
        {
            return field.getName() + sForeignIdFieldSuffix;
        }
        else
        {
            return field.getName() + "_" + databaseField.foreignColumnName();
        }
    }

    public static boolean isForeign(DatabaseField databaseField)
    {
        return databaseField.foreign() || databaseField.foreignAutoRefresh() || !databaseField.foreignColumnName().isEmpty();
    }

    public static boolean isId(DatabaseField databaseField)
    {
        return databaseField.id() || databaseField.generatedId();
    }

    public static Field findField(Class<?> modelClass, String name)
    {
        for (Field field : modelClass.getDeclaredFields())
        {
			// Direct match?
            if (field.getName().equals(name))
            {
                return field;
            }

			// MapFrom-annotated match?
			MapFrom map_from = field.getAnnotation(MapFrom.class);

			if (map_from != null && name.equals(map_from.value()))
			{
				return field;
			}
        }

        if (modelClass.getSuperclass() != null)
        {
            return findField(modelClass.getSuperclass(), name);
        }
        else
        {
            return null;
        }
    }

    /**
     * Finds a field of a certain type in a given table type
     */
    public static Field findFirstField(Class<?> tableType, Class<?> fieldType)
    {
        for (Field field : tableType.getDeclaredFields())
        {
            if (field.getType().equals(fieldType))
            {
                return field;
            }
        }

        if (tableType.getSuperclass() != null)
        {
            return findFirstField(tableType.getSuperclass(), fieldType);
        }
        else
        {
            return null;
        }
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

        return (Class<?>)child_type; // TODO: check conversion
    }

    /**
     * Find a Field with a DatabaseField annotation that defines it as being an id column.
     * @return the Field or null
     */
    public static Field findIdField(Class<?> modelClass)
    {
        for (Field field : modelClass.getDeclaredFields())
        {
            DatabaseField database_field = field.getAnnotation(DatabaseField.class);

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
            return findIdField(modelClass.getSuperclass());
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
    public static Field findForeignField(Class<?> parentClass, Class<?> findClass)
    {
        for (Field field : parentClass.getDeclaredFields())
        {
            DatabaseField database_field = field.getAnnotation(DatabaseField.class);

            if (database_field != null && isForeign(database_field) && findClass.isAssignableFrom(field.getType()))
            {
                return field;
            }
        }

        if (parentClass.getSuperclass() != null)
        {
            return findForeignField(parentClass.getSuperclass(), findClass);
        }
        else
        {
            return null;
        }
    }
}

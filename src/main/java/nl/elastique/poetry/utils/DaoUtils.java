package nl.elastique.poetry.utils;

import com.j256.ormlite.dao.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.elastique.poetry.reflection.AnnotationRetriever;
import nl.elastique.poetry.reflection.OrmliteReflection;

/**
 * A set of utilities for Ormlite Dao querying.
 */
public class DaoUtils
{
    static private final Logger sLogger = LoggerFactory.getLogger(DaoUtils.class);

    /**
     * Docs: http://www.sqlite.org/datatype3.html
     */
    public enum ColumnType
    {
        INTEGER, // for int and boolean
        REAL, // float,  double, etc.
        TEXT, // String, etc.
        BLOB,
        NUMERIC
    }

    /**
     * Execute a raw query.
     * It exists to provide logging of all DaoUtils queries.
     *
     * @param dao the Dao to execute the query for
     * @param query the raw query to execute
     * @throws java.sql.SQLException when the query fails to run
     */
    private static void executeQuery(Dao<?, ?> dao, String query) throws java.sql.SQLException
    {
        sLogger.debug("query: {}", query);
        dao.executeRawNoArgs(query);
    }

    /**
     * Add a column to a table.
     *
     * @param dao the Dao to execute the query for
     * @param columnName the column to add
     * @param columnType the type of column to add
     * @throws java.sql.SQLException when the query fails to run
     */
    public static void addColumn(Dao<?, ?> dao, String columnName, ColumnType columnType) throws java.sql.SQLException
    {
        String query = String.format("ALTER TABLE %s ADD COLUMN %s %s",
            OrmliteReflection.getTableName(new AnnotationRetriever(), dao.getDataClass()),
            columnName,
            columnType.toString());

        executeQuery(dao, query);
    }

    /**
     * Add a column to a table with default value for column inserts without value.
     *
     * @param dao the Dao to execute the query for
     * @param columnName the column to add
     * @param columnType the type of column to add
     * @param defaultValue the default value for newly inserted rows that don't have a value specified for this column
     * @throws java.sql.SQLException when the query fails to run
     */
    public static void addColumn(Dao<?, ?> dao, String columnName, ColumnType columnType, String defaultValue) throws java.sql.SQLException
    {
        String query = String.format("ALTER TABLE %s ADD COLUMN %s %s DEFAULT %s",
            OrmliteReflection.getTableName(new AnnotationRetriever(), dao.getDataClass()),
            columnName,
            columnType.toString(),
            defaultValue);

        executeQuery(dao, query);
    }

    /**
     * Copy values from an existing column to another existing column.
     *
     * @param dao the Dao to execute the query for
     * @param fromName the column to copy from
     * @param toName the column to copy to
     * @throws java.sql.SQLException when the query fails to run
     */
    public static void copyColumn(Dao<?, ?> dao, String fromName, String toName) throws java.sql.SQLException
    {
        String query = String.format("UPDATE %s SET %s = %s",
            OrmliteReflection.getTableName(new AnnotationRetriever(), dao.getDataClass()),
            toName,
            fromName);

        executeQuery(dao, query);
    }

    /**
     * Create an index for a specific column.
     *
     * @param dao the Dao to execute the query for
     * @param columnName the column to create the index for
     * @param indexName the name of the index to create
     * @throws java.sql.SQLException when the query fails to run
     */
    public static void createIndex(Dao<?, ?> dao, String columnName, String indexName) throws java.sql.SQLException
    {
        String query = String.format("CREATE INDEX %s ON %s (%s)",
            indexName,
            OrmliteReflection.getTableName(new AnnotationRetriever(), dao.getDataClass()),
            columnName);

        executeQuery(dao, query);
    }

    /**
     * Create an index for a specific column.
     * The name of the index will be columnName_index.
     *
     * @param dao the Dao to execute the query for
     * @param columnName the column to create the index for
     * @throws java.sql.SQLException when the query fails to run
     */
    public static void createIndex(Dao<?, ?> dao, String columnName) throws java.sql.SQLException
    {
        createIndex(dao, columnName, String.format("%s_index", columnName));
    }
}

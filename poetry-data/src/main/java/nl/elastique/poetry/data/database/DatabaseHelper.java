package nl.elastique.poetry.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final Logger sLogger = LoggerFactory.getLogger(DatabaseHelper.class);

    private static DatabaseConfiguration sConfiguration;

    protected static final HashMap<Class<?>, Dao<?, ?>> sCachedDaos = new HashMap<Class<?>, Dao<?,?>>();

    public DatabaseHelper(Context context)
    {
        super(context, sConfiguration.getDatabaseName(), null, sConfiguration.getModelVersion());
    }

    public DatabaseHelper(Context context, DatabaseConfiguration configuration)
    {
        super(context, configuration.getDatabaseName(), null, configuration.getModelVersion());

        sConfiguration = configuration;
    }

    public static void setConfiguration(DatabaseConfiguration configuration)
    {
        sConfiguration = configuration;
    }

    public static DatabaseHelper getHelper(Context context)
    {
        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static DatabaseHelper getHelper(Context context, Class<? extends DatabaseHelper> classObject)
    {
        return OpenHelperManager.getHelper(context, classObject);
    }

    public static void releaseHelper()
    {
        OpenHelperManager.releaseHelper();
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
    {
        createDatabase();
    }

    public void createTable(Class<?> classObject)
    {
        try
        {
            TableUtils.createTable(getConnectionSource(), classObject);
        }
        catch (SQLException e)
        {
            sLogger.error(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        recreateDatabase();
    }

    public <T> void dropTable(Class<T> classObject)
    {
        try
        {
            TableUtils.dropTable(getConnectionSource(), classObject, true);

            if (sCachedDaos.containsKey(classObject))
            {
                sCachedDaos.remove(classObject);
            }
        }
        catch (SQLException e)
        {
            sLogger.error("can't drop table", e);
        }
    }

    public void recreateDatabase()
    {
        dropDatabase();
        createDatabase();
    }

    /**
     * Drops all tables.
     */
    public void dropDatabase()
    {
        for (Class<?> classObject : sConfiguration.getModelClasses())
        {
            dropTable(classObject);
        }
    }

    private void createDatabase()
    {
        for (Class<?> classObject : sConfiguration.getModelClasses())
        {
            createTable(classObject);
        }
    }

    @Override
    public <D extends com.j256.ormlite.dao.Dao<T,?>, T>  D getDao(java.lang.Class<T> clazz) throws java.sql.SQLException
    {
        @SuppressWarnings("unchecked")
        D dao = (D)sCachedDaos.get(clazz);

        if(dao == null)
        {
            dao = super.getDao(clazz);
            sCachedDaos.put(clazz, dao);
        }
        return dao;
    }
}

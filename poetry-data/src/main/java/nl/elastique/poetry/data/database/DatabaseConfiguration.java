package nl.elastique.poetry.data.database;

public class DatabaseConfiguration
{
    public static final String sDefaultName = "database";

    private final Class<?>[] mModelClasses;

    private final String mDatabaseName;

    private final int mModelVersion;

    public DatabaseConfiguration(int modelVersion, Class<?>[] modelClasses, String databaseName)
    {
        mModelVersion = modelVersion;
        mModelClasses = modelClasses;
        mDatabaseName = databaseName;
    }

    public DatabaseConfiguration(int modelVersion, Class<?>[] modelClasses)
    {
        this(modelVersion, modelClasses, sDefaultName);
    }


    public int getModelVersion()
    {
        return mModelVersion;
    }

    public Class<?>[] getModelClasses()
    {
        return mModelClasses;
    }

    public String getDatabaseName()
    {
        return mDatabaseName;
    }
}


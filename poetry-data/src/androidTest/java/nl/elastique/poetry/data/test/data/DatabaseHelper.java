package nl.elastique.poetry.data.test.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;

import nl.elastique.poetry.data.database.DatabaseConfiguration;
import nl.elastique.poetry.data.test.data.models.Group;
import nl.elastique.poetry.data.test.data.models.User;
import nl.elastique.poetry.data.test.data.models.UserGroup;
import nl.elastique.poetry.data.test.data.models.UserTag;

public class DatabaseHelper extends nl.elastique.poetry.data.database.DatabaseHelper
{
    public final static DatabaseConfiguration sConfiguration = new DatabaseConfiguration(6, new Class<?>[]
    {
        User.class,
        Group.class,
        UserTag.class,
        UserGroup.class
    });

    public DatabaseHelper(Context context)
    {
        super(context, sConfiguration);
    }

    public static DatabaseHelper getHelper(Context context)
    {
        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        super.onUpgrade(db, connectionSource, oldVersion, newVersion);

        // When calling the parent class, the whole database is deleted and re-created.
        // Custom upgrade code goes here to override that behavior.
    }
}
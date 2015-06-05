package nl.elastique.poetry.data.test.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Maps a User onto a Group
 */
@DatabaseTable
public class UserGroup
{
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(foreign = true)
    public User user;

    @DatabaseField(foreign = true)
    public Group group;
}

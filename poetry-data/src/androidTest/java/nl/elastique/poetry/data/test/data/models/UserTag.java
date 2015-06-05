package nl.elastique.poetry.data.test.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class UserTag
{
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(foreign = true)
    public User user;

    @DatabaseField
    public String value;
}

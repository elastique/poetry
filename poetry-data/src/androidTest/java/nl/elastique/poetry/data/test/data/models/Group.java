package nl.elastique.poetry.data.test.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Group
{
    @DatabaseField(id = true)
    public int id;

    @DatabaseField
    public String name;
}

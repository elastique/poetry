package nl.elastique.poetry.data.test.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.elastique.poetry.json.annotations.MapFrom;

@DatabaseTable
public class Group
{
    @DatabaseField(id = true, columnName = "id")
	@MapFrom("id")
    private int mId;

    @DatabaseField(columnName = "name")
	@MapFrom("name")
    private String mName;

	public int getId()
	{
		return mId;
	}

	public String getName()
	{
		return mName;
	}
}

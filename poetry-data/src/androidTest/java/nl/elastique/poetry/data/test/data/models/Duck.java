package nl.elastique.poetry.data.test.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.elastique.poetry.data.json.annotations.MapFrom;

@DatabaseTable
public class Duck
{
	@DatabaseField(id = true)
	@MapFrom("id")
	private int mId;

	@DatabaseField
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

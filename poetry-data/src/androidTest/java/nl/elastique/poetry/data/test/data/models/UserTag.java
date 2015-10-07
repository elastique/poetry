package nl.elastique.poetry.data.test.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.elastique.poetry.data.json.annotations.MapFrom;

@DatabaseTable
public class UserTag
{
    @DatabaseField(generatedId = true, columnName = "id")
    private int mId;

    @DatabaseField(foreign = true, columnName = "user_id")
    private User mUser;

    @DatabaseField(columnName = "value")
	private String mValue;
}

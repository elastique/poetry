package nl.elastique.poetry.data.test.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import nl.elastique.poetry.json.annotations.MapFrom;

/**
 * Maps a User onto a Group
 */
@DatabaseTable
public class UserGroup
{
    @DatabaseField(generatedId = true)
	@MapFrom("id")
    private int mId;

    @DatabaseField(foreign = true, columnName = "user_id")
	@MapFrom("user")
	private User mUser;

    @DatabaseField(foreign = true, columnName = "group_id")
	@MapFrom("group")
    private Group mGroup;
}

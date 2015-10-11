package nl.elastique.poetry.data.test.data.models;

import android.content.Context;
import android.nfc.Tag;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

import nl.elastique.poetry.data.json.annotations.ForeignCollectionFieldSingleTarget;
import nl.elastique.poetry.data.json.annotations.ManyToManyField;
import nl.elastique.poetry.data.json.annotations.MapFrom;

@DatabaseTable
public class User
{
    @DatabaseField(id = true, columnName = "id")
	@MapFrom("id")
    private int mId;

    @DatabaseField(columnName = "name")
	@MapFrom("name")
    private String mName;

    /**
     * Many-to-many relationships.
     *
     * OrmLite requires a ForeignCollectionField with the helper-type UserGroup to assist in the database relational mapping.
     * JSON-to-SQLite persistence also requires the additional annotation "ManyToManyField"
     */
    @ForeignCollectionField(eager = true)
    @ManyToManyField(targetType = Group.class)
	@MapFrom("groups")
    private ForeignCollection<UserGroup> mGroups;

    /**
     * One-to-many relationships on simple types (arrays of strings/integers/etc.)
     *
     * OrmLite requries a ForeignCollectionField with the helper-type UserTag.
     * JSON-to-SQLite persistence also requires the additional annotation "ForeignCollectionFieldSingleTarget" to
     * specify in which field of the UserTag table the simple type is stored. In this case the column name is "value":
     */
    @ForeignCollectionField
    @ForeignCollectionFieldSingleTarget(targetField = "value")
	@MapFrom("tags")
    private ForeignCollection<UserTag> mTags;

	public int getId()
	{
		return mId;
	}

	public String getName()
	{
		return mName;
	}
}

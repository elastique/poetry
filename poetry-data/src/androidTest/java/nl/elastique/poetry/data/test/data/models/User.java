package nl.elastique.poetry.data.test.data.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import nl.elastique.poetry.data.json.annotations.ForeignCollectionFieldSingleTarget;
import nl.elastique.poetry.data.json.annotations.ManyToManyField;

@DatabaseTable
public class User
{
    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private String name;

    /**
     * Many-to-many relationships.
     *
     * OrmLite requires a ForeignCollectionField with the helper-type UserGroup to assist in the database relational mapping.
     * JSON-to-SQLite persistence also requires the additional annotation "ManyToManyField"
     */
    @ForeignCollectionField(eager = true)
    @ManyToManyField(targetType = Group.class)
    private ForeignCollection<UserGroup> groups;

    /**
     * One-to-many relationships on simple types (arrays of strings/integers/etc.)
     *
     * OrmLite requries a ForeignCollectionField with the helper-type UserTag.
     * JSON-to-SQLite persistence also requires the additional annotation "ForeignCollectionFieldSingleTarget" to
     * specify in which field of the UserTag table the simple type is stored. In this case the column name is "value":
     */
    @ForeignCollectionField
    @ForeignCollectionFieldSingleTarget(targetField = "value")
    public ForeignCollection<UserTag> tags;
}

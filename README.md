Poetry
======

[![License][license-svg]][license-link]

Poetry is an Android persistence library that allows you to persist a JSON object tree directly into an SQLite database through [OrmLite].
Poetry enables you to write less code and persist data much faster.

Consider this JSON:
```json
{
	"id" : 1,
	"name" : "John Doe"
}
```
And this Java model:
```java
@DatabaseTable
class User
{
	@DatabaseField(id = true)
	public int id;

	@DatabaseField)
	public String name;
}
```
They can be stored into the database like this:
```java
JSONObject json_object = ...; // processed JSON tree
DatabaseHelper helper = ...; // OrmLite databasehelper;
JsonPersister persister = new JsonPersister(helper.getWritableDatabase());
persister.persistObject(User.class, json_object);
```
## Features ##

* Annotation-based model configuration
* Advanced `DatabaseHelper` with easy `DatabaseConfiguration`
* Support for relationships:
	* One-to-one
	* Many-to-one
	* One-to-many
	* Many-to-many
* Support for persisting arrays of base types (e.g. JSON String array persisted to separate table)

## Requirements ##

- Android 2.3.3 (API level 10) or higher

## Usage ##

**build.gradle**

```groovy
repositories {
    mavenCentral()
    maven {
        url "https://dl.bintray.com/elastique/poetry"
    }
}
```

```groovy
dependencies {
    compile (
        [group: 'nl.elastique.poetry', name: 'poetry', version: '3.0.1']
    )
}
```

## Demo ##

A [Demo] application is available on GitHub


## Tutorial ##

### Creating a DatabaseHelper ###

The Poetry `DatabaseHelper` allows you to easily configure your database.
In the example below, you can see a custom `DatabaseHelper` with a `DatabaseConfiguration` that holds the model version and model classes.

**MyDatabaseHelper.java**
```java
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;
import nl.elastique.poetry.database.DatabaseConfiguration;

public class MyDatabaseHelper extends nl.elastique.poetry.database.DatabaseHelper
{
    public final static DatabaseConfiguration sConfiguration = new DatabaseConfiguration(1, new Class<?>[]
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
}
```

### Mapping custom JSON properties ###

With the `@MapFrom` annotation, you can specify the json key name.

**User.java**
```java
@DatabaseTable
class User
{
	@DatabaseField(id = true, columnName = "id")
	@MapFrom("id")
    private int mId;

	@DatabaseField(columnName = "name")
	@MapFrom("name")
    private String mName;
}
```

### One-to-many relationships ###

In this example, a `Game` object holds a list of `Player` objects. 

**game.json**
```json
{
    "id": 1,
    "players" : [
        {
	        "id": 1,
	        "name": "John"
        },
        {
	        "id": 2,
	        "name": "Jane"
        }
    ]
}
```
**Game.java**
```java
@DatabaseTable
public class Game
{
    @DatabaseField(id = true)
    public int id;
    
	@ForeignCollectionField(eager = true)
	public ForeignCollection<Player> players;
}
```
**Player.java**
```java
@DatabaseTable
public class UserTag
{
    @DatabaseField(id = true)
    public int id;

    @DatabaseField
    public String name;
}
```

### Many-to-many relationships ###

In this example, a `User` can have 0 or more `Groups`, and a `Group` can have 0 or more `Users`.

When an object occurs several times throughout the same JSON data, its data is just updated while it is being imported.

**users.json**
```json
[
	{
	    "id" : 1,
	    "name" : "John",
	    "groups" : [
		    {
			    "id" : 1,
			    "name" : "Group 1"
		    },
		    {
			    "id" : 2,
			    "name" : "Group 2"
		    }
	    ]
	},
	{
	    "id" : 2,
	    "name" : "Jane",
	    "groups" : [
		    {
			    "id" : 1,
			    "name" : "Group 1"
		    }
	    ]
	}
]
```

**User.java**
```java
@DatabaseTable
public class User
{
    @DatabaseField(id = true)
    public int id;

    @DatabaseField
    public String name;
    
    /**
     * Many-to-many relationships.
     *
     * OrmLite requires a ForeignCollectionField with the helper-type UserGroup to assist in the database relational mapping.
     * JSON-to-SQLite persistence also requires the additional annotation "ManyToManyField"
     */
    @ForeignCollectionField(eager = true)
    @ManyToManyField(targetType = Group.class)
	public ForeignCollection<UserGroup> groups;
}
```
**UserGroup.java**
```java
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
```

**Group.java**
```java
@DatabaseTable
public class Group
{
    @DatabaseField(id = true)
    public int id;

    @DatabaseField
    public String name;
}
```

### One-to-one relationships ###

In this example, a `User` can have 0 or 1 `Friend` user.

**users.json**
```json
[
	{
	    "id": 1,
	    "name" : "John",
	    "friend" : { "id" : 2 }
	},
	{
	    "id": 2,
	    "name" : "Jane",
	    "friend" : { "id" : 1 }
	}
]
```

The following alternative JSON is also valid:

**users.json**
```json
[
	{
	    "id": 1,
	    "name" : "John",
	    "friend" : 2
	},
	{
	    "id": 2,
	    "name" : "Jane",
	    "friend" : 1
	}
]
```

**User.java**
```java
@DatabaseTable
public class User
{
    @DatabaseField(id = true)
    public int id;

    @DatabaseField
    public String name;
    
    @DatabaseField(foreign = true)
    public User friend;
}
```

### Arrays of base types ###

Arrays of base types work the same as one-to-many relationships. The only difference is that you have to define a model that holds the base types and use the `@ForeignCollectionFieldSingleTarget` annotation to specify the database column name that holds the value.

**user.json**
```json
{
    "id": 1,
    "tags" : [
        "tag1",
        "tag2"
    ]
}
```
**User.java**
```java
@DatabaseTable
public class User
{
    @DatabaseField(id = true)
    public int id;
    
	// The targetField refers to the table's column name
	@ForeignCollectionField(eager = true)
	@ForeignCollectionFieldSingleTarget(targetField = "value")
	@MapFrom("tags")
	public ForeignCollection<UserTag> userTags;
}
```
**UserTag.java**
```java
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
```

 [license-svg]: https://img.shields.io/badge/license-Apache%202.0-lightgrey.svg?style=flat
 [license-link]: https://github.com/elastique/poetry/blob/master/LICENSE
 [OrmLite]: http://ormlite.com
[JSON]: http://json.org/java/
[Demo]: https://github.com/elastique/poetry-demo

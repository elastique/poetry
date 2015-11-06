Poetry
======

Poetry is an Android persistence library that allows you to persist a JSON object tree directly into an SQLite database (through [OrmLite]).
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
	@DatabaseField(id = true, columnName = "id")
	@MapFrom("id")
    private int mId;

	@DatabaseField(columnName = "name")
	@MapFrom("name")
    private String mName;
}
```
They can be stored into the database like this:
```java
JSONObject json_object = ...; // processed JSON tree
DatabaseHelper helper = ...; // OrmLite databasehelper;
JsonPersister persister = new JsonPersister(helper.getWritableDatabase());
persister.persistObject(User.class, json_object);
```
Features
----
* Annotation-based model configuration
* Support for relationships:
	* One-to-one
	* Many-to-one
	* One-to-many
	* Many-to-many
* Arrays of base types (e.g. JSON String array persisted to separate table)

Requirements
----

- Android 2.3.3 (API level 10) or higher

License
----

Apache License, Version 2.0

Usage
----

<strong>build.gradle</strong>

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
        [group: 'nl.elastique.poetry', name: 'poetry', version: '3.0.0']
    )
}
```

Demo
----

A [Demo] application is available on GitHub

[OrmLite]:http://ormlite.com
[JSON]:http://json.org/java/
[Demo]:https://github.com/elastique/poetry-demo

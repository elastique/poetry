Poetry
======

Poetry is a persistence library that allows you to persist a JSON object tree directly into an SQLite database (through [OrmLite]).
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

Library components
----
* <strong>poetry-core</strong>: core Java and Android utilities
* <strong>poetry-data</strong>: persistence and json processing

License
----

Apache License, Version 2.0

Usage
----

<strong>build.gradle</strong>

```
repositories {
    mavenCentral()
    maven {
        url "https://dl.bintray.com/elastique/poetry"
    }
}
```

```
dependencies {
    compile (
        [group: 'nl.elastique.poetry', name: 'poetry-core', version: '2.1.0'],
        [group: 'nl.elastique.poetry', name: 'poetry-data', version: '2.1.0']
    )
}
```

Demo
----

A [Demo] application is available on GitHub

[OrmLite]:http://ormlite.com
[JSON]:http://json.org/java/
[Demo]:https://github.com/elastique/poetry-demo

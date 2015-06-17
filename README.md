Poetry
======

Poetry is a persistence library that allows you to persist a JSON object tree (through [Jackson]) directly into an SQLite database (through [OrmLite]).
Poetry enables you to write less code and persist data much faster.

Consider this JSON:
```
{
	"id" : 1,
	"name" : "John Doe"
}
```
And this Java model:
```
@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable
class User
{
	@DatabaseField(id = true)
    public int id;

	@DatabaseField
    public String name;
}
```
They can be stored into the database like this:
```
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
* <strong>poetry-web</strong>: http request processing utilities

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
        [group: 'nl.elastique.poetry', name: 'poetry-core', version: '1.1+'],
        [group: 'nl.elastique.poetry', name: 'poetry-web', version: '1.1+'],
        [group: 'nl.elastique.poetry', name: 'poetry-data', version: '1.1+']
    )
}
```

[OrmLite]:http://ormlite.com
[JSON]:http://json.org/java/
[Jackson]:https://github.com/FasterXML/jackson

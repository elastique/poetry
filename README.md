Poetry
======

Poetry is Elastique's open source toolset to make Android development easier and more productive.

Poetry is divided into 3 parts:

* poetry-core
* poetry-web
* poetry-data

This library and its documentation is still in development. Please come back later for more information.

Poetry Core
----
Basic language features, algorithms and interfaces.

Poetry Web
----
Web development features including a threaded HttpRequestHandler that combines HttpClient and HttpRequest, executes it in the background and provides success/failure callbacks in the background. It also broadcasts its internal events.

Poetry Data
----
Provides ORM functionality by combining [OrmLite] and [JSON] combined with [Jackson]:

JSON input can be directly injected into an SQLite database.

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

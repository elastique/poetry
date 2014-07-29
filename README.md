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

<strong>settings.gradle</strong>

```
include ':poetry-core'
include ':poetry-web'
include ':poetry-data'
project(':poetry-core').projectDir = new File('ElastiquePoetry/poetry-core')
project(':poetry-web').projectDir = new File('ElastiquePoetry/poetry-web')
project(':poetry-data').projectDir = new File('ElastiquePoetry/poetry-data')
```

<strong>build.gradle</strong>

```
dependencies {
    compile project(':poetry-core')
    compile project(':poetry-web')
    compile project(':poetry-data')
}
```

[OrmLite]:http://ormlite.com
[JSON]:http://json.org/java/
[Jackson]:https://github.com/FasterXML/jackson

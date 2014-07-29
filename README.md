Poetry
======

Poetry is Elastique's open source toolset to make Android development easier and more productive.

Poetry is divided into 3 parts:

* poetry-core
* poetry-web
* poetry-data

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
This library and its documentation is still in development. Please come back later for more information.

[OrmLite]:http://ormlite.com
[JSON]:http://json.org/java/
[Jackson]:https://github.com/FasterXML/jackson

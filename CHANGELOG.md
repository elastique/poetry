## 2.3.0 (2015-10-10)

Features:
 - performance improvements for big datasets with JsonPersister

Deprecations:
 - pre-Android 4.1 support is dropped 

## 2.2.0 (2015-10-07)

Bugfixes:
 - fix for foreign relationships
 - updated tests

## 2.1.0 (2015-09-25)

Features:
 - added MapFrom annotation
 
## 2.0.0 (2015-09-15)

Deprecations:
 - removed poetry-web because Android's Apache HttpClient is deprecated

## 1.2.0 (2015-09-02)

Features:
 - added ability to persist foreign relationships when only an ID is provided in JSON
   (rather than an object with an ID embedded in it)

## 1.1.0 (2015-06-05)

Deprecations:
  - removed HttpRequestJsonPersister
  - removed MappedJsonPersister

Bugfixes:
  - fix for reserved table names (e.g. "Group")

Features:
  - added first test
  - created demo project

## 1.0.0

Initial release
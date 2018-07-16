

Run all locally:
---------------- 

```
mvn lagom:runAll
```

hello service
-------------

Get / update users welcome messages:
```
curl -H "Content-Type: application/json" -X POST -d '{"message": "Hi"}' http://localhost:9000/api/hello/Alice
{ "done" : true }✔

curl http://localhost:9000/api/hello/Alice
Hi, Alice!✔

```


readside module
---------------

This service consumes events from a Kafka topic produced by the Hello module and persists in Couchbase

```
curl http://localhost:9000/api/readside/hello/Alice
```

CRUD example
------------

For demonstration purposes readside module also implements a direct CRUD-like access to Couchbase.

> THIS approach is NOT recommended for implementing flexible and scalable Microservices.

> ES/CQRS approach provides means to build flexible and scalable microservices promoting asynchronous
> communications between services.

```

curl -H "Content-Type: application/json" -X POST -d '{"message": "Hi"}' http://localhost:9000/crud-api/hello/Alice
{ "done" : true }✔

curl http://localhost:9000/crud-api/hello/Alice
Hello, Alice!✔

```

Couchbase
---------

[https://hub.docker.com/r/couchbase/server/]

```
docker pull couchbase/server

docker run -d --name db -p 8091-8094:8091-8094 -p 11210:11210 couchbase
```

open [http://localhost:8091]

Cassandra
---------

```
22:13 $ ~/local/apps/apache-cassandra-3.11.2/bin/cqlsh 127.0.0.1 4000
Connected to Test Cluster at 127.0.0.1:4000.
[cqlsh 5.0.1 | Cassandra 3.11.2 | CQL spec 3.4.4 | Native protocol v4]
Use HELP for help.
cqlsh> show databases

describe keyspaces;

use <keyspace>;

describe tables;

SELECT * FROM system_schema.views;
```


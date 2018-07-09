

Run all locally:
---------------- 

```
mvn lagom:runAll
```

hello service
-------------

Get / update users welcome messages:
```
curl http://localhost:9000/api/hello/World
Hello, World!✔

curl http://localhost:9000/api/hello/Alice
Hello, Alice!✔

curl -H "Content-Type: application/json" -X POST -d '{"message": "Hi"}' http://localhost:9000/api/hello/Alice

curl http://localhost:9000/api/hello/Alice
Hi, Alice!✔

curl http://localhost:9000/api/hello/World
Hello, World!✔
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

curl -H "Content-Type: application/json" -X POST -d '{"message": "Hi"}' http://localhost:9000/api/readside/hello/Alice
{ "done" : true }✔

curl http://localhost:9000/api/readside/hello/Alice
Hi, Alice!✔
```

Couchbase
---------

[https://hub.docker.com/r/couchbase/server/]

```
docker pull couchbase/server

docker run -d --name db -p 8091-8094:8091-8094 -p 11210:11210 couchbase
```

open [http://localhost:8091]


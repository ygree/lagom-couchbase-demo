
Run Locally: 

```
mvn lagom:runAll
```

Test:
----------
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


Read-Side:
----------
```
curl http://localhost:9000/api/readside/hello/Alice
```

Couchbase:
----------

[https://hub.docker.com/r/couchbase/server/]

```
docker pull couchbase/server

docker run -d --name db -p 8091-8094:8091-8094 -p 11210:11210 couchbase

open http://localhost:8091
```




Run Locally: 

```
mvn lagom:runAll
```

Test:

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


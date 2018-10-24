# RSO: Orders microservice

## Prerequisites

```bash
docker run -d --name pg-rents -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=rent -p 5433:5432 postgres:latest
```
Local run (warning: debugger needs to be attached):
```
java -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y -jar api/target/rents-api-1.0.0-SNAPSHOT.jar
```

```
docker build -t rents:1.0 .
docker run -p 8081:8081 rents:1.0
to change network host: docker run -p 8081:8081 --net=host rents:1.0
```

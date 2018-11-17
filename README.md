# RSO: Rents microservice

## Prerequisites

```bash
docker run -d --name pg-rents -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=rent -p 5433:5432 postgres:latest
```
Local run (warning: debugger needs to be attached):
```
java -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y -jar api/target/rents-api-1.0.0-SNAPSHOT.jar
```

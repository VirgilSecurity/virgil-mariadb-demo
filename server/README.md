# Prerequisites

- Java 11+
- Docker

# Run with Docker

## Create configuration files

Copy `env.template` file to a new `.env` file and define configuration parameters.

## Build Java application

```
mvn clean package
```

## Build Docker image

```
docker build -t virgil/mariadb-demo-server .
```

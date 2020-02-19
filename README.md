# virgil-mariadb-demo

# Prerequisites

- Java 11+
- Docker
- yarn

# Run with Docker Compose

## Create configuration files

Copy `env.template` file to a new `.env` file and define configuration parameters.

## Build Java application

Execute next command in `server` directory
```
mvn clean package
```

## Build Docker image

Execute next command in `server` directory
```
docker build -t virgil/mariadb-demo-server .
```

## Build client application

Execute next command in `client` directory
```
yarn install
```

## Build Docker image

Execute next command in `client` directory
```
docker build -t virgil/mariadb-demo-client .
```

## Run Docker container

```
docker-compose up
```

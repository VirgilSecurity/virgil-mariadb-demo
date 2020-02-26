# virgil-mariadb-demo kubernetes deployment

# Prerequisites

- Kubernetes cluster
- kubectl
- generated environment variables (.env)

## Deployment

1. Import .env variables
```
while read line; do export $line; done <<< $(grep -v '^#' server/.env)
```

2. Create namespace & mariadb deployment
```
kubectl create namespace demo
kubectl apply -f k8s-deployment-mariadb.yml -n demo
```

3. Create deployment of demo server and client
```
cat k8s-deployment.yml | envsubst | kubectl -n demo apply -f -
```


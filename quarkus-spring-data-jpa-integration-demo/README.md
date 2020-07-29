# quarkus-spring-data-jpa-integration-demo project

This project uses Quarkus and Quarkus Spring Data Jpa Extension (no api / no Panache).

It's a demo to perform load tests.

## Compile the application

```
/mvn clean package -Dquarkus.package.uber-jar=true
```

## Docker

```
/docker build . -t quarkus-spring-data-jpa-integration-demo:1.0.0-SNAPSHOT
/docker push quarkus-spring-data-jpa-integration-demo:1.0.0-SNAPSHOT
```

## Kubernetes deploy

```
/kubectl apply -f deployment.yaml
```
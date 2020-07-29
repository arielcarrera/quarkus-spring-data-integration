# spring-data-jpa-service project

This project uses Spring and Spring Data Jpa Extension.

It's a demo to perform load tests.

## Compile the application

```
/mvn clean package spring-boot:repackage
```

## Docker

```
/mvn spring-boot:build-image
```

## Kubernetes deploy

```
/kubectl apply -f deployment.yaml
```
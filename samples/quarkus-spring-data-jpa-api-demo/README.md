# quarkus-spring-data-jpa-api-demo project

This project uses Quarkus and Quarkus Spring Data Jpa Api extension (default/Panache).

It's a demo to perform load tests.

## Compatibility problems due to Quarkus's unsupported Spring Data Jpa features

- Query By Example usage (removed)
- @Async and Future usage (removed)
- Streamable usage (removed)
- Attribute name of @Query is currently not supported (removed)
- Attribute nativeQuery of @Query is currently not supported (removed)
- spEL expressions are not currently supported

## Compile the application

```
/mvn clean package -Dquarkus.package.uber-jar=true
```

## Docker

```
/docker build . -t quarkus-spring-data-jpa-api-demo:1.0.0-SNAPSHOT
/docker push quarkus-spring-data-jpa-api-demo:1.0.0-SNAPSHOT
```

## Kubernetes deploy

```
/kubectl apply -f deployment.yaml
```
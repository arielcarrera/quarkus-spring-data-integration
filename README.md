# quarkus-spring-data-integration extension

Quarkus extension that integrates Spring Data Jpa and provides all the features of this famous framework (No API / No Panache)! No more limits! Easy to migrate and run!
Migrate to Quarkus and enjoy a noticeable improvement in performance and memory consumption without losing any functionality!


## What is *Quarkus Spring Data Jpa Integration* ?

This is an extension that integrate Spring Data Jpa in Quarkus. It's intended to be a first step to migrate large projects or projects that uses features that are not supported by the current Panche Spring Data Jpa Api implementation.

The mission of this project is to allow not only an easy migration of projects based on Spring Boot / Spring Data Jpa, also allow to incorporate in this way the rest of the Spring Data stack projects in less time and in a simpler way to maintain than the current solution adopted.
Te


## Supported features

- [x] Support for default Spring data Repository interfaces:
  - [X] CrudRepository
  - [X] PagingAndSortingRepository
  - [X] JpaRepository
  - [X] JpaRepositoryImplementation
  - [X] SimpleJpaRepository
- [X] Query Methods
  - [X] Derived count query
  - [X] Derived delete query
  - [X] Derived update query
- [X] Custom Repository Interfaces
- [X] @Transactional support
- [X] Enhanced lookup of repository fragments hierarchy
- [X] @NoRepositoryBean support
- [X] Multi-Configuration
- [X] Property Expressions (transversal property)
- [X] Special Parameter Handling (Pageabla, Sort)
- [X] Limiting Query Results
- [X] Returning types (Collections / Iterables)
- [X] Returning type: Streamable
- [X] Optional (null handling)
- [X] Async query result
- [X] Custom Configuration with MicroProfile Config (quarkus application.properties)
- [X] Custom Configuration with a given Cdi Repository Configuration Bean
- [X] Repository Custom Implementation (Impl)
- [X] Custom Base Repository
- [X] Projections (dtos)
- [X] Specifications
- [X] Persistable.isNew or EntityInformation.

## Not tested features 

- [ ] Spring Data Extensions
  - [ ] QueryDSL Extension
  - [ ] Repository Populator Extension

- [ ] Support for other Spring data Repository interfaces (less common): 
  - [ ] QuerydslJpaRepository (not tested. Deprecated in Spring Data Jpa)
  - [ ] ReactiveCrudRepository (not tested yet)
  - [ ] RevisionRepository (not tested yet)
  - [ ] RxJava2CrudRepository (not tested yet)
  - [ ] RxJava2SortingRepository (not tested yet)

## Known limitations and advantages

It is known that the way to generate code for the implementation of Panache (Spring Data Jpa API), surely is the optimal solution, however, when we are trying to migrate from a project with Spring Data Jpa to Quarkus, many of us have had to rewrite lot of code for some unsupported features or with slight differences to how the original solution works.

So this extension aims to allow you to take the first steps... to be able to jump to Quarkus in a simple way! It is literally possible to do it in a few minutes now!

While the performance tests are preliminary ... it's amazing! an improvement of around 30% in throughput and a reduction of around 14% of memory consumption are shown with a default configuration and under the same load conditions.

## features supported by "Quarkus Spring Data Jpa Integration" that are not supported in the Quarkus API implementation (Panache / Spring Data Jpa Api)

- [X] QueryByExampleExecutor
- [ ] QueryDSL support
- [X] Custom base Repository
- [X] java.util.concurrent.Future as return types
- [X] Native queries with @Query
- [X] Named queries with @Query
- [X] Persistable.isNew or EntityInformation.
- [X] Slice triggers a count query by every slice ([issue](https://github.com/quarkusio/quarkus/issues/9357))


## Source code

In the root of the source code you can see the following projects: 

 - quarkus-spring-data-jpa-extension-parent
 - quarkus-spring-data-jpa-integration-demo
 - spring-data-jpa-service
 
### quarkus-spring-data-jpa-extension-parent

The source of the extension.
In the test folder of the deployment module you can find a complete TestSuite that cover more than 300 tests.

### spring-data-jpa-service

SpringBoot project with rest services using Spring Data Jpa.

### quarkus-spring-data-jpa-integration-demo

Quarkus project with the new *Spring Data Jpa Integration* extension which allows to migrate all the services implemented in the SpringBoot project with almost no code changes! (only configuration related changes)!

## Related projects

[spring-data-commons for cdi](https://github.com/arielcarrera/spring-data-commons-cdi)
[spring-data-jpa for cdi](https://github.com/arielcarrera/spring-data-jpa-cdi)

## Maven Repositories

```
    <repositories>
        <repository>
            <id>github</id>
            <name>GitHub Ariel Carrera - Spring Data Commons Packages</name>
            <url>https://maven.pkg.github.com/arielcarrera/spring-data-commons-cdi</url>
        </repository>
        <repository>
            <id>github2</id>
            <name>GitHub Ariel Carrera - Spring Data Jpa Packages</name>
            <url>https://maven.pkg.github.com/arielcarrera/spring-data-jpa-cdi</url>
        </repository>
        <repository>
          <id>github3</id>
          <name>GitHub Ariel Carrera - Quarkus Spring Data Integration Packages</name>
          <url>https://maven.pkg.github.com/arielcarrera/quarkus-spring-data-integration</url>
        </repository>
    </repositories>
```

### Video presentation

[click here (youtube)](https://www.youtube.com/watch?v=GY-4_kBU1AE)

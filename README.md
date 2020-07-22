# quarkus-spring-data-integration extension

Quarkus extension that integrates Spring Data Jpa and provides all the features of this famous framework (No API / No Panache)! No more limits! Easy to migrate and run!

## What is it?

This is an extension that integrate Spring Data Jpa in Quarkus. It's intended to be a first step to migrate large projects or projects that uses features that are not supported by the current Panche Spring Data Jpa Api implementation.

The mission of this project is to allow not only an easy migration of projects based on Spring Boot / Spring Data Jpa, also allow to incorporate in this way the rest of the Spring Data stack projects in less time and in a simpler way to maintain than the current solution adopted.

## Known limitations

It is known that the way to generate code for the implementation of Panache (Spring Data Jpa API), surely is the optimal solution, however, when we are trying to migrate from a project with Spring Data Jpa to Quarkus, many of us have had to rewrite lot of code for some unsupported features or with slight differences to how the original solution works.
So this extension aims to allow you to take the first steps... to be able to jump to Quarkus in a simple way! It is literally possible to do it in a few minutes now!

## Supported features

[x] Support for default Spring data Repository interfaces:
- [X] CrudRepository
- [X] PagingAndSortingRepository
- [X] JpaRepository
- [X] JpaRepositoryImplementation
- [X] SimpleJpaRepository
[X] Query Methods
- [X] Derived count query
- [X] Derived delete query
- [X] Derived update query
[X] Custom Repository Interfaces
[X] @Transactional support
[X] Enhanced lookup of repository fragments hierarchy
[X] @NoRepositoryBean support
[X] Multi-Configuration
[X] Property Expressions (transversal property)
[X] Special Parameter Handling (Pageabla, Sort)
[X] Limiting Query Results
[X] Returning types (Collections / Iterables)
[X] Returning type: Streamable
[X] Optional (null handling)
[X] Async query result
[X] Custom Configuration with MicroProfile Config (quarkus application.properties)
[X] Custom Configuration with a given Cdi Repository Configuration Bean
[X] Repository Custom Implementation (Impl)
[X] Custom Base Repository
[X] Projections (dtos)
[X] Specifications


## Not tested features 

[ ] Spring Data Extensions
- [ ] QueryDSL Extension
- [ ] Repository Populator Extension

[ ] Support for default Spring data Repository interfaces (less common):
- [ ] QuerydslJpaRepository (not tested. Deprecated in Spring Data Jpa)
- [ ] ReactiveCrudRepository (not tested yet)
- [ ] RevisionRepository (not tested yet)
- [ ] RxJava2CrudRepository (not tested yet)
- [ ] RxJava2SortingRepository (not tested yet)





### Video presentation

[click here (youtube)](https://www.youtube.com/watch?v=GY-4_kBU1AE)

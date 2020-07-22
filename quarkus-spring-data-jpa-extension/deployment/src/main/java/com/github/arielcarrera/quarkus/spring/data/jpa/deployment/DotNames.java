package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.Vetoed;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.jboss.jandex.DotName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config.DefaultCdiRepositoryConfig;

import io.quarkus.arc.DefaultBean;

/**
 * Helper class with {@link DotName}'s constants
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public final class DotNames {
	
	public static final DotName STEREOTYPE = DotName.createSimple(Stereotype.class.getName());
	public static final DotName QUALIFIER = DotName.createSimple(Qualifier.class.getName());
	public static final DotName QUALIFIER_DEFAULT = DotName.createSimple(Default.class.getName());
	public static final DotName QUALIFIER_ANY = DotName.createSimple(Any.class.getName());
	public static final DotName VETOED = DotName.createSimple(Vetoed.class.getName());
	public static final DotName DEFAULT_BEAN = DotName.createSimple(DefaultBean.class.getName());
	
	public static final DotName APPLICATION_SCOPED = DotName.createSimple(ApplicationScoped.class.getName());
	public static final DotName SESSION_SCOPED = DotName.createSimple(SessionScoped.class.getName());
	public static final DotName REQUEST_SCOPED = DotName.createSimple(RequestScoped.class.getName());
	public static final DotName DEPENDENT = DotName.createSimple(Dependent.class.getName());
	public static final DotName SINGLETON  = DotName.createSimple(Singleton.class.getName());
	
	public static final DotName PRODUCES = DotName.createSimple(Produces.class.getName());
	public static final DotName ENTITY_MANAGER = DotName.createSimple(EntityManager.class.getName());
	
	public static final DotName SPRING_DATA_SIMPLE_JPA_REPOSITORY = DotName.createSimple(SimpleJpaRepository.class.getName());
	public static final DotName SPRING_DATA_CDI_REPOSITORY_CONFIGURATION = DotName.createSimple(CdiRepositoryConfiguration.class.getName());
	public static final DotName SPRING_DATA_DEFAULT_CDI_REPOSITORY_CONFIGURATION = DotName.createSimple(DefaultCdiRepositoryConfig.class.getName());
	
	
	public static final DotName SPRING_DATA_REPOSITORY_DEFINITION = DotName.createSimple(RepositoryDefinition.class.getName());

	public static final DotName SPRING_DATA_REPOSITORY = DotName.createSimple(Repository.class.getName());
	public static final DotName SPRING_DATA_CRUD_REPOSITORY = DotName.createSimple(CrudRepository.class.getName());
	public static final DotName SPRING_DATA_PAGING_REPOSITORY = DotName
			.createSimple(PagingAndSortingRepository.class.getName());
	public static final DotName SPRING_DATA_JPA_REPOSITORY = DotName.createSimple(JpaRepository.class.getName());
	public static final DotName SPRING_DATA_QUERY_EXAMPLE_EXECUTOR = DotName
			.createSimple(QueryByExampleExecutor.class.getName());

	public static final Set<DotName> SUPPORTED_REPOSITORIES = new HashSet<>(Arrays.asList(SPRING_DATA_JPA_REPOSITORY,
			SPRING_DATA_PAGING_REPOSITORY, SPRING_DATA_CRUD_REPOSITORY, SPRING_DATA_REPOSITORY));
	
	public static final Set<DotName> SPRING_DATA_REPOSITORY_CLASSES = new HashSet<>(Arrays.asList(SPRING_DATA_JPA_REPOSITORY,
			SPRING_DATA_PAGING_REPOSITORY, SPRING_DATA_CRUD_REPOSITORY, SPRING_DATA_REPOSITORY, SPRING_DATA_SIMPLE_JPA_REPOSITORY));

	public static final DotName SPRING_DATA_NO_REPOSITORY_BEAN = DotName.createSimple(NoRepositoryBean.class.getName());
	public static final DotName SPRING_DATA_PAGEABLE = DotName.createSimple(Pageable.class.getName());
	public static final DotName SPRING_DATA_PAGE_REQUEST = DotName.createSimple(PageRequest.class.getName());
	public static final DotName SPRING_DATA_SORT = DotName.createSimple(Sort.class.getName());
	public static final DotName SPRING_DATA_PAGE = DotName.createSimple(Page.class.getName());
	public static final DotName SPRING_DATA_SLICE = DotName.createSimple(Slice.class.getName());
	public static final DotName SPRING_DATA_QUERY = DotName.createSimple(Query.class.getName());
	public static final DotName SPRING_DATA_PARAM = DotName.createSimple(Param.class.getName());
	public static final DotName SPRING_DATA_MODIFYING = DotName.createSimple(Modifying.class.getName());

	public static final DotName JPA_ID = DotName.createSimple(Id.class.getName());
	public static final DotName VERSION = DotName.createSimple(Version.class.getName());
	public static final DotName JPA_MAPPED_SUPERCLASS = DotName.createSimple(MappedSuperclass.class.getName());
	public static final DotName VOID = DotName.createSimple(void.class.getName());
	public static final DotName LONG = DotName.createSimple(Long.class.getName());
	public static final DotName PRIMITIVE_LONG = DotName.createSimple(long.class.getName());
	public static final DotName INTEGER = DotName.createSimple(Integer.class.getName());
	public static final DotName PRIMITIVE_INTEGER = DotName.createSimple(int.class.getName());
	public static final DotName BOOLEAN = DotName.createSimple(Boolean.class.getName());
	public static final DotName PRIMITIVE_BOOLEAN = DotName.createSimple(boolean.class.getName());
	public static final DotName STRING = DotName.createSimple(String.class.getName());
	public static final DotName ITERATOR = DotName.createSimple(Iterator.class.getName());
	public static final DotName COLLECTION = DotName.createSimple(Collection.class.getName());
	public static final DotName LIST = DotName.createSimple(List.class.getName());
	public static final DotName STREAM = DotName.createSimple(Stream.class.getName());
	public static final DotName OPTIONAL = DotName.createSimple(Optional.class.getName());
	public static final DotName OBJECT = DotName.createSimple(Object.class.getName());

	private DotNames() {
	}
}

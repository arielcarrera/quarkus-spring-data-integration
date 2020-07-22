package com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.spi.Converter;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.DataExceptionPostProcessor;
import com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.TransactionalPostProcessor;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConvertWith;

/**
 * Data - Jpa - Repository Configuration
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ConfigGroup
public class DataJpaRepositoryConfig {

    /**
     * The Query Lookup Strategy key ({@link QueryLookupStrategy.Key}) to lookup queries
     */
    @ConfigItem(defaultValue = "CREATE_IF_NOT_FOUND")
    public Optional<QueryLookupStrategy.Key> queryLookupStrategy = Optional.empty();
    
    /**
     * The base Repository class to use
     */
    @ConvertWith(ClassConverter.class)
    @ConfigItem
    public Optional<Class<?>> baseClass = Optional.empty();

    /**
     * The configured postfix to be used for lookup implementation classes (custom impl).
     */
    @ConfigItem(defaultValue = "Impl") 
    public String implementationPostfix = "Impl";

    /**
     * list of Repository Proxy PostProcessor to be used during proxy creation
     */
    @ConvertWith(ClassConverter.class)
    @ConfigItem(defaultValue = "com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.DataExceptionPostProcessor,com.github.arielcarrera.quarkus.spring.data.jpa.runtime.processors.TransactionalPostProcessor")
    public Optional<List<Class<?>>> postProcessors = Optional.of(Arrays.asList(DataExceptionPostProcessor.class, TransactionalPostProcessor.class));

    
	/**
	 * The Query Method Evaluation ContextProvider ({@link QueryMethodEvaluationContextProvider}) to use 
	 */
    @ConvertWith(ClassConverter.class)
    @ConfigItem
    public Optional<Class<?>> queryMethodEvaluationContextProvider = Optional.empty();
    
    /**
     * list of Query Creation Listeners to be used during proxy creation
     */
    @ConvertWith(ClassConverter.class)
    @ConfigItem
    public Optional<List<Class<?>>> queryCreationListeners = Optional.empty();
    
    
    /**
     * Converter from String to Class
     * @author Ariel Carrera <carreraariel@gmail.com>
     *
     */
    public static class ClassConverter implements Converter<Class<?>> {

		private static final long serialVersionUID = 1L;

		public ClassConverter() {}

        @Override
        public Class<?> convert(String s) {
            if (s == null || s.isEmpty()) {
                return null;
            }
            try {
				return this.getClass().getClassLoader().loadClass(s);
			} catch (ClassNotFoundException ignored) {
			}

            throw new IllegalArgumentException("Convertion error. Class not found: " + s);
        }
    }

}
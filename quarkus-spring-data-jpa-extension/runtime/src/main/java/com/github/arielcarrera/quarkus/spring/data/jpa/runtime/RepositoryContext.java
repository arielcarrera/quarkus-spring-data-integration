package com.github.arielcarrera.quarkus.spring.data.jpa.runtime;

import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.inject.UnsatisfiedResolutionException;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
import org.springframework.data.repository.config.FragmentMetadata;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.ImplementationLookupConfiguration;
import org.springframework.data.repository.config.RepositoryFragmentConfiguration;
import org.springframework.data.util.Optionals;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Context for Spring Data Jpa Repositories
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public class RepositoryContext {

	private final MetadataReaderFactory metadataReaderFactory;
	private final FragmentMetadata metdata;
	private final CustomRepositoryImplementationDetector detector;

	/**
	 * Constructor of the Repository Context
	 */
	public RepositoryContext() {
		this(new CustomRepositoryImplementationDetector(new StandardEnvironment(), new PathMatchingResourcePatternResolver()));
	}

	/**
	 * Create a new {@link RepositoryContext} given {@link ClassLoader} and
	 * {@link CustomRepositoryImplementationDetector}.
	 * 
	 * @param classLoader must not be {@literal null}.
	 * @param detector must not be {@literal null}.
	 */
	public RepositoryContext(CustomRepositoryImplementationDetector detector) {

		Assert.notNull(detector, "CustomRepositoryImplementationDetector must not be null!");

		ResourceLoader resourceLoader = new PathMatchingResourcePatternResolver();
		this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
		this.metdata = new FragmentMetadata(metadataReaderFactory);
		this.detector = detector;
	}

	/**
	 * Get the custom repository implementation detector
	 * @return
	 */
	public CustomRepositoryImplementationDetector getCustomImplementationDetector() {
		return detector;
	}

	/**
	 * Get the {@link RepositoryFragmentConfiguration} for a repository class
	 * 
	 * @param config
	 * @param repositoryInterface class
	 * @return {@link Stream} of {@link RepositoryFragmentConfiguration} fragment configurations.
	 */
	public Stream<RepositoryFragmentConfiguration> findRepositoryFragmentsConfigs(CdiRepositoryConfiguration config,
			Class<?> repositoryInterface) {

		DetectionConfiguration detectionConfiguration = new DetectionConfiguration(config,
				metadataReaderFactory);

		return metdata.getFragmentInterfaces(repositoryInterface.getName())
				.map(it -> getRepositoryFragmentConfiguration(it, detectionConfiguration))
				.flatMap(Optionals::toStream);
	}

	/**
	 * Get a custom implementation class for a given repository class
	 * @param repositoryType
	 * @param cdiRepositoryConfiguration
	 * @return {@link Optional} of the interface class
	 */
	public Optional<Class<?>> getCustomImplementationClass(Class<?> repositoryType,
			CdiRepositoryConfiguration cdiRepositoryConfiguration) {

		ImplementationDetectionConfiguration configuration = new DetectionConfiguration(
				cdiRepositoryConfiguration, metadataReaderFactory);
		ImplementationLookupConfiguration lookup = configuration.forFragment(repositoryType.getName());

		Optional<AbstractBeanDefinition> beanDefinition = detector.detectCustomImplementation(lookup);
		return beanDefinition.map(bd -> loadClass(bd.getBeanClassName()));
	}

	/**
	 * Get class by name
	 * @param name
	 * @return
	 */
	public Class<?> loadClass(String name) {
		try {
			return ClassUtils.forName(name, this.getClass().getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new UnsatisfiedResolutionException(String.format("Unable to resolve class for name '%s'", name), e);
		}
	}

	/**
	 * Method to get a repository fragment configuration
	 * @param fragmentInterfaceName
	 * @param detectionConfiguration
	 * @return {@link Optional} of {@link RepositoryFragmentConfiguration}
	 */
	private Optional<RepositoryFragmentConfiguration> getRepositoryFragmentConfiguration(String fragmentInterfaceName,
			DetectionConfiguration detectionConfiguration) {
		
		ImplementationLookupConfiguration lookup = detectionConfiguration.forFragment(fragmentInterfaceName);
		Optional<AbstractBeanDefinition> beanDefinition = detector.detectCustomImplementation(lookup);
		
		return beanDefinition.map(bd -> new RepositoryFragmentConfiguration(fragmentInterfaceName, bd));
	}

}
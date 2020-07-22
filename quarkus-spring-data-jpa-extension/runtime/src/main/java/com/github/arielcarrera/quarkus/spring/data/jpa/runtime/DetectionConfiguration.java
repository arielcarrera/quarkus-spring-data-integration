package com.github.arielcarrera.quarkus.spring.data.jpa.runtime;

import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.util.Streamable;

/**
 * Default Repository Implementation detection configuration
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public class DetectionConfiguration implements ImplementationDetectionConfiguration {

	private final MetadataReaderFactory metadataReaderFactory;
	private final CdiRepositoryConfiguration config;
	

	public DetectionConfiguration(CdiRepositoryConfiguration configuration, MetadataReaderFactory metadataReaderFactory) {
		super();
		this.config = configuration;
		this.metadataReaderFactory = metadataReaderFactory;
	}
	
	public MetadataReaderFactory getMetadataReaderFactory() {
		return metadataReaderFactory;
	}

	@Override
	public String getImplementationPostfix() {
		return config.getRepositoryImplementationPostfix();
	}

	@Override
	public Streamable<String> getBasePackages() {
		return Streamable.empty();
	}

	@Override
	public Streamable<TypeFilter> getExcludeFilters() {
		return Streamable.empty();
	}
}
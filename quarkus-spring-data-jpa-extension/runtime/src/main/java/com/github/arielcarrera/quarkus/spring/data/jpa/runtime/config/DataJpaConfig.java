package com.github.arielcarrera.quarkus.spring.data.jpa.runtime.config;
import java.util.Collections;
import java.util.Map;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Data - Jpa configuration
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@ConfigRoot(name = "data-jpa", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class DataJpaConfig {

	/**
	 * Repository configuration
	 */
    @ConfigItem
    public DataJpaRepositoryConfig repository;
    
    /**
     * The Named Queries to use
     */
    @ConfigDocSection
    @ConfigDocMapKey("named-queries")
    public Map<String, String> namedQueries = Collections.emptyMap();

}
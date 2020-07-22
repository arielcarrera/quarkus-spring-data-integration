package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import java.util.List;
import java.util.Map;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Build item with discovered repository classes
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public final class RepositoryTypesBuildItem extends SimpleBuildItem {

	private final Map<ClassInfo, List<AnnotationInstance>> repositoryTypes;

    public RepositoryTypesBuildItem(Map<ClassInfo, List<AnnotationInstance>> repoTypes) {
        this.repositoryTypes = repoTypes;
    }

    public Map<ClassInfo, List<AnnotationInstance>> getRepositoryTypes() {
        return repositoryTypes;
    }

}
package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import java.util.List;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Build item with discovered Repository Configuration
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public final class RepositoryConfigurationBuildItem extends SimpleBuildItem {

	private final List<CdiConfigurationItem> cdiRepositoryConfigurationQualifiers;

    public RepositoryConfigurationBuildItem(List<CdiConfigurationItem> cdiRepositoryConfigurationQualifiers) {
        this.cdiRepositoryConfigurationQualifiers = cdiRepositoryConfigurationQualifiers;
    }

    public List<CdiConfigurationItem> getCdiRepositoryConfigurationQualifiers() {
        return cdiRepositoryConfigurationQualifiers;
    }
    
    public static class CdiConfigurationItem {
    	
    	private ClassInfo classInfo;
    	private List<AnnotationInstance> annotationsInstance;
    	
		public CdiConfigurationItem(ClassInfo classInfo, List<AnnotationInstance> annotationsInstance) {
			super();
			this.classInfo = classInfo;
			this.annotationsInstance = annotationsInstance;
		}
		
		public ClassInfo getClassInfo() {
			return classInfo;
		}
		public void setClassInfo(ClassInfo classInfo) {
			this.classInfo = classInfo;
		}
		public List<AnnotationInstance> getAnnotationsInstance() {
			return annotationsInstance;
		}
		public void setAnnotationInstance(List<AnnotationInstance> annotationsInstance) {
			this.annotationsInstance = annotationsInstance;
		}
    	
    	
    }

}
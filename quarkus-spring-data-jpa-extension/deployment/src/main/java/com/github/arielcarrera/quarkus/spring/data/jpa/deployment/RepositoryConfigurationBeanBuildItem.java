package com.github.arielcarrera.quarkus.spring.data.jpa.deployment;

import java.util.List;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;

import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Build item with aggregates to the repo configurations data
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
public final class RepositoryConfigurationBeanBuildItem extends SimpleBuildItem {

	private final List<CdiConfigurationBeanItem> configBeanInfoList;

	public RepositoryConfigurationBeanBuildItem(
			List<CdiConfigurationBeanItem> configBeanInfoList) {
		this.configBeanInfoList = configBeanInfoList;
	}

	public List<CdiConfigurationBeanItem> getCdiRepositoryConfigurationQualifiers() {
		return configBeanInfoList;
	}

	public static class CdiConfigurationBeanItem {

		private BeanInfo beanInfo;
		private ClassInfo classInfo;
		private List<AnnotationInstance> annotationsInstance;

		public CdiConfigurationBeanItem(BeanInfo beanInfo, ClassInfo classInfo, List<AnnotationInstance> annotationsInstance) {
			super();
			this.beanInfo = beanInfo;
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

		public BeanInfo getBeanInfo() {
			return beanInfo;
		}

		public void setBeanInfo(BeanInfo beanInfo) {
			this.beanInfo = beanInfo;
		}

	}

}
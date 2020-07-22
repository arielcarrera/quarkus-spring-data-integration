package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom;

import org.springframework.data.repository.core.support.QueryCreationListener;
import org.springframework.data.repository.query.RepositoryQuery;

public class CustomQueryCreationListener<T extends RepositoryQuery> 
implements QueryCreationListener<T> {

	public void onCreation(T query) {
		//do something
	}
}
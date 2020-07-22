package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.resources;

import javax.enterprise.inject.Any;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.ItemNoRepositoryBeanRepository;
import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.repositories.NoRepositoryBeanRepository;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InjectableInstance;

public class NoRepositoryBeanResourceImpl implements NoRepositoryBeanResource {

	public NoRepositoryBeanResourceImpl() {
		super();
	}
	
	@Override
    public boolean checkNoRepositoryBeanInjection() {
		InjectableInstance<NoRepositoryBeanRepository> instance = Arc.container().select(NoRepositoryBeanRepository.class, Any.Literal.INSTANCE);
        return instance.isResolvable();
    }

	@Override
	public boolean checkItemNoRepositoryBeanInjection() {
		InjectableInstance<ItemNoRepositoryBeanRepository> instance = Arc.container().select(ItemNoRepositoryBeanRepository.class, Any.Literal.INSTANCE);
        return instance.isResolvable();
	}
}

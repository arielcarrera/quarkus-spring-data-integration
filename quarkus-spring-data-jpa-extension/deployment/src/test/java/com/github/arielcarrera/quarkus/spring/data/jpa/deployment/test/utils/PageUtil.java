package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.utils;

import com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page;

public class PageUtil {
	
	public static <T> com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page<T> 
		of(org.springframework.data.domain.Page<T> page){
		
		return new Page<T>(page.getNumber(),page.getSize(),page.getTotalElements(),
				page.getTotalPages(), page.getContent());
	}
	
	public static <T> com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model.Page<T> 
	of(org.springframework.data.domain.Slice<T> slice){
	
	return new Page<T>(slice.getNumber(),slice.getSize(), null,
			null, slice.getContent());
}
	
}

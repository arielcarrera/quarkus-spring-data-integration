package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Page<T> {
	
	Long totalElements;
	
	Integer totalPages;
	
	Integer numberOfElements;
	
	Integer number;
	
	Integer size;
	
	List<T> content = new ArrayList<>();

	public Page() {
		super();
	}
	
	public Page(Integer number, Integer size, Long totalElements, Integer totalPages, List<T> content) {
		super();
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.number = number;
		this.size = size;
		if (content != null) {
			this.content.addAll(content);
		}
		this.numberOfElements = content.size();
	}

	public Long getTotalElements() {
		return totalElements;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public Integer getNumber() {
		return number;
	}

	public Integer getSize() {
		return size;
	}

	public List<T> getContent() {
		return content;
	}

	public Integer getNumberOfElements() {
		return numberOfElements;
	}
	
}

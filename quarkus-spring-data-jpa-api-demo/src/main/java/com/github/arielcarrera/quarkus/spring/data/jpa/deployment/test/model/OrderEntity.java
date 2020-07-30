package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

@Entity
public class OrderEntity implements Persistable<Integer> {

	@Id
	private Integer id;

	private Integer number;

	private String productId;

	private String user;

	private Double total;

	@Transient
	private boolean isNew = true;

	public OrderEntity() {
		super();
	}

	public OrderEntity(Integer id, Integer number, String productId, String user, Double total) {
		super();
		this.id = id;
		this.number = number;
		this.productId = productId;
		this.user = user;
		this.total = total;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	@PrePersist
	@PostLoad
	void markNotNew() {
		this.isNew = false;
	}

}
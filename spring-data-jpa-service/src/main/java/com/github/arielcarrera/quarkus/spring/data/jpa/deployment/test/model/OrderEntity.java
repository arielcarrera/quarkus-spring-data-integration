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

	private Integer orderNumber;

	private String productId;

	private String username;

	private Double total;

	@Transient
	private boolean isNew = true;

	public OrderEntity() {
		super();
	}

	public OrderEntity(Integer id, Integer number, String productId, String user, Double total) {
		super();
		this.id = id;
		this.orderNumber = number;
		this.productId = productId;
		this.username = user;
		this.total = total;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer number) {
		this.orderNumber = number;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String user) {
		this.username = user;
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

package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model;

public class OrderDto  {

	private Integer id;

	private Integer orderNumber;

	private String productId;
	
	private Double total;

	public OrderDto() {
		super();
	}

	public OrderDto(Integer id, Integer number, String productId) {
		super();
		this.id = id;
		this.orderNumber = number;
		this.productId = productId;
	}
	
	public OrderDto(Integer id, Integer number, String productId, Double total) {
		super();
		this.id = id;
		this.orderNumber = number;
		this.productId = productId;
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

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

}

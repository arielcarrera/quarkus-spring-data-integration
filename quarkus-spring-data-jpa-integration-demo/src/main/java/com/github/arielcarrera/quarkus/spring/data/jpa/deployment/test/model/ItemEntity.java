package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@NamedQuery(name = "ItemEntity.findByUniqueValue", query = "select i from ItemEntity i where i.uniqueValue = ?1")
@NamedQuery(name = "ItemEntity.findByUniqueValue.count", query = "select count(i) from ItemEntity i where i.uniqueValue = ?1")
@Entity
public class ItemEntity implements Identifiable<Integer>, Codifiable<Integer> {
	
	public static final int STATUS_ACTIVE = 0;
	public static final int STATUS_DELETED = -1;
	
	@Id
	private Integer id;

	private Integer value;
	
	private int status;
	
	private Integer code;
	
	@Column(nullable=true,unique=true)
	private Integer uniqueValue;
	
	@OneToOne(fetch=FetchType.LAZY,cascade=CascadeType.PERSIST)
	private LazyEntity lazy;
	
	@OneToOne(fetch=FetchType.EAGER,cascade=CascadeType.PERSIST)
	private ChildEntity child;

	public ItemEntity() {
		super();
	}

	public ItemEntity(Integer id, Integer value) {
		super();
		this.id = id;
		this.value = value;
	}
	
	public ItemEntity(Integer id, Integer value, Integer uniqueValue) {
		super();
		this.id = id;
		this.value = value;
		this.uniqueValue = uniqueValue;
	}
	
	public ItemEntity(Integer id, Integer value, Integer uniqueValue, int status) {
		super();
		this.id = id;
		this.value = value;
		this.uniqueValue = uniqueValue;
		this.status = status;
	}
	
	public ItemEntity(Integer id, Integer value, Integer uniqueValue, int status, Integer codigo) {
		super();
		this.id = id;
		this.value = value;
		this.uniqueValue = uniqueValue;
		this.status = status;
		this.code = codigo;
	}
	
	public ItemEntity(Integer id, Integer value, Integer uniqueValue, int status, ChildEntity child) {
		super();
		this.id = id;
		this.value = value;
		this.uniqueValue = uniqueValue;
		this.status = status;
		this.child = child;
	}
	
	public ItemEntity(Integer id, Integer value, Integer uniqueValue, int status, LazyEntity lazy) {
		super();
		this.id = id;
		this.value = value;
		this.uniqueValue = uniqueValue;
		this.status = status;
		this.lazy = lazy;
	}
	
	public ItemEntity(Integer id, Integer value, Integer uniqueValue, int status, LazyEntity lazy, Integer codigo) {
		super();
		this.id = id;
		this.value = value;
		this.uniqueValue = uniqueValue;
		this.status = status;
		this.lazy = lazy;
		this.code = codigo;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getUniqueValue() {
		return uniqueValue;
	}

	public void setUniqueValue(Integer uniqueValue) {
		this.uniqueValue = uniqueValue;
	}

	public LazyEntity getLazy() {
		return lazy;
	}

	public void setLazy(LazyEntity lazy) {
		this.lazy = lazy;
	}
	
	public ChildEntity getChild() {
		return child;
	}

	public void setChild(ChildEntity child) {
		this.child = child;
	}

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public void setCode(Integer code) {
		this.code = code;
	}
	
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}

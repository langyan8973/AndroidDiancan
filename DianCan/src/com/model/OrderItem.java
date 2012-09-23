package com.model;


// Generated 2012-2-28 10:39:34 by Hibernate Tools 3.4.0.CR1


/**
 * OrderItem generated by hbm2java
 */

public class OrderItem implements java.io.Serializable {
	private Integer id;
	private Recipe recipe;
	private Order order;
	private Integer count;

	public OrderItem() {
	}

	public OrderItem(Recipe recipe, Order order, Integer count) {
		this.recipe = recipe;
		this.order = order;
		this.count = count;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Recipe getRecipe() {
		return this.recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}

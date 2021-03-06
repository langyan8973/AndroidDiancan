package com.diancan.model;


// Generated 2012-2-28 10:39:34 by Hibernate Tools 3.4.0.CR1


/**
 * OrderItem generated by hbm2java
 */

public class OrderItem implements java.io.Serializable {
	private Integer id;
	private Recipe recipe;
	private Integer countNew;
	private Integer countDeposit;
	private Integer countConfirm;
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public OrderItem() {
	}

	public OrderItem(Recipe recipe){
		this.recipe = recipe;
		countConfirm = 0;
		countDeposit = 0;
		countNew = 0;
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

	public Integer getCountNew() {
		return countNew;
	}

	public void setCountNew(Integer countNew) {
		this.countNew = countNew;
	}

	public Integer getCountDeposit() {
		return countDeposit;
	}

	public void setCountDeposit(Integer countDeposit) {
		this.countDeposit = countDeposit;
	}

	public Integer getCountConfirm() {
		return countConfirm;
	}

	public void setCountConfirm(Integer countConfirm) {
		this.countConfirm = countConfirm;
	}
	
	public int GetCount(){
		return countNew+countDeposit+countConfirm;
	}

}

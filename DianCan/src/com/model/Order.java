package com.model;

// Generated 2012-2-28 10:39:34 by Hibernate Tools 3.4.0.CR1

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Order generated by hbm2java
 */

public class Order implements java.io.Serializable {
	private Integer id;
	private Desk desk;
	private Integer number;
	private Date starttime;
	private Date ordertime;
	private Date enttime;
	private Integer status;
	private String code;
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	public Order() {
	}

	public Order(Desk desk) {
		this.desk = desk;
	}

	public Order(Desk desk, Integer number, Date starttime, Date ordertime,
			Date enttime, Integer status, List<OrderItem> orderItems) {
		this.desk = desk;
		this.number = number;
		this.starttime = starttime;
		this.ordertime = ordertime;
		this.enttime = enttime;
		this.status = status;
		this.orderItems = orderItems;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Desk getDesk() {
		return this.desk;
	}

	public void setDesk(Desk desk) {
		this.desk = desk;
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getStarttime() {
		return this.starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getOrdertime() {
		return this.ordertime;
	}

	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}

	public Date getEnttime() {
		return this.enttime;
	}

	public void setEnttime(Date enttime) {
		this.enttime = enttime;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	
}

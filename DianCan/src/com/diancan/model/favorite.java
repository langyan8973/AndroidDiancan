package com.diancan.model;

import java.io.Serializable;
import java.util.Date;

public class favorite {
	int id;
	Date time;
	String restaurantName;
	int rid;
	String restaurantImage;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getRestaurantName() {
		return restaurantName;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public String getRestaurantImage() {
		return restaurantImage;
	}
	public void setRestaurantImage(String restaurantImage) {
		this.restaurantImage = restaurantImage;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return restaurantName;
	}
	
	
}

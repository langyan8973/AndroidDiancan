package com.diancan.model;

import java.util.Date;

import android.R.integer;
import android.text.format.Time;

public class HisRestaurant {
	private int rid;
	private String rname;
	private Date time;
	private String image;
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public String getRname() {
		return rname;
	}
	public void setRname(String rname) {
		this.rname = rname;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
}

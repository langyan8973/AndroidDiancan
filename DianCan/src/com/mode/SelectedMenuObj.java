package com.mode;

import java.io.Serializable;

import com.model.Recipe;

import android.R.integer;

public class SelectedMenuObj extends MenuObj implements Serializable {
	
	public int count;
	public double totalPrice;
	public boolean isCommit;
	public int lockCount;
	public SelectedMenuObj()
	{
		
	}
	public SelectedMenuObj(Recipe menu)
	{
		cid=menu.getCid().toString();
		id=menu.getId().toString();
		count=0;
		description=menu.getDescription();
		image=menu.getImage();
		name=menu.getName();
		price=menu.getPrice();
		totalPrice=menu.getPrice()*count;
		lockCount=0;
		isCommit=false;
	}
	public SelectedMenuObj(MenuObj menu)
	{
		cid=menu.getCid();
		id=menu.getId();
		count=0;
		description=menu.getDescription();
		image=menu.getImage();
		name=menu.getName();
		price=menu.getPrice();
		totalPrice=menu.getPrice()*count;
		lockCount=0;
		isCommit=false;
	}
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public double getPrice() {
		// TODO Auto-generated method stub
		return super.getPrice();
	}
	@Override
	public void setPrice(double price) {
		// TODO Auto-generated method stub
		super.setPrice(price);
	}
	@Override
	public String getCid() {
		// TODO Auto-generated method stub
		return super.getCid();
	}
	@Override
	public void setCid(String cid) {
		// TODO Auto-generated method stub
		super.setCid(cid);
	}
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return super.getId();
	}
	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		super.setId(id);
	}
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return super.getDescription();
	}
	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub
		super.setDescription(description);
	}
	@Override
	public String getImage() {
		// TODO Auto-generated method stub
		return super.getImage();
	}
	@Override
	public void setImage(String image) {
		// TODO Auto-generated method stub
		super.setImage(image);
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		super.setName(name);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public boolean isCommit() {
		return isCommit;
	}
	public void setCommit(boolean isCommit) {
		this.isCommit = isCommit;
	}
	public int getLockCount() {
		return lockCount;
	}
	public void setLockCount(int lockCount) {
		this.lockCount = lockCount;
	}
}

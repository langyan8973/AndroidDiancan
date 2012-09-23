package com.mode;

import java.io.Serializable;
import java.util.ArrayList;

import com.model.Desk;

import android.R.integer;

public class DeskObj implements Serializable {
	
	int id;
	String name;
	int capacity;
	String description;
	Integer isSelect;
	public SelectedProduct selectedProduct;
	
	public DeskObj()
	{
		
	}
	public DeskObj(int tid,String tname,int tcapacity,String tdescription)
	{
		id=tid;
		name=tname;
		capacity=tcapacity;
		description=tdescription;
		selectedProduct=null;
		isSelect=0;
	}
	public DeskObj(Desk desk)
	{
		id=desk.getId();
		name=desk.getName();
		description=desk.getDescription();
		selectedProduct=new SelectedProduct();
		isSelect=desk.getStatus();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public SelectedProduct getSelectedProduct() {
		return selectedProduct;
	}
	public void setSelectedProduct(SelectedProduct selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	
	public Integer getIsSelect() {
		return isSelect;
	}
	public void setIsSelect(Integer isSelect) {
		this.isSelect = isSelect;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}

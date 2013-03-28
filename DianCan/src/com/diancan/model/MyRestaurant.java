package com.diancan.model;

import java.util.List;

import android.R.integer;
import android.util.SparseArray;
import android.util.SparseIntArray;

public class MyRestaurant{
	int id;
	String name;
	String image;
	SparseArray<Category> categoryDic;
	
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


	public SparseArray<Category> getCategoryDic() {
		return categoryDic;
	}

	public void setCategoryDic(SparseArray<Category> categoryDic) {
		this.categoryDic = categoryDic;
	}

	public MyRestaurant(){
		
	}
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public MyRestaurant(Restaurant restaurant){
		setId(restaurant.getId());
		setName(restaurant.getName());
		setImage(restaurant.getImage());
	}

	
}

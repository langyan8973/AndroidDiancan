package com.mode;

import java.util.ArrayList;
import java.util.List;

import com.model.Category;

public class CategoryObj {
	
	int id;
	String name;
	String description;
	String image;
	List<SelectedMenuObj> selectedMenuObjs;
	public CategoryObj()
	{
		
	}
	public CategoryObj(Category category)
	{
		id=category.getId();
		name=category.getName();
		description=category.getDescription();
		image=category.getImage();
		selectedMenuObjs=new ArrayList<SelectedMenuObj>();
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public List<SelectedMenuObj> getSelectedMenuObjs() {
		return selectedMenuObjs;
	}
	public void setSelectedMenuObjs(List<SelectedMenuObj> selectedMenuObjs) {
		this.selectedMenuObjs = selectedMenuObjs;
	}
}

package com.diancan.model;

import java.util.Date;
import java.util.List;

public class AllDomain implements java.io.Serializable {
	private List<Category> addCategories;
	private List<Category> updateCategories;
	private List<Integer> deleteCategories;
	private List<Recipe> addRecipes;
	private List<Recipe> updateRecipes;
	private List<Integer> deleteRecipes;
	private List<Desk> addDesks;
	private List<Desk> updateDesks;
	private List<Integer> deleteDesks;
	private Date date;

	public List<Category> getAddCategories() {
		return addCategories;
	}

	public void setAddCategories(List<Category> addCategories) {
		this.addCategories = addCategories;
	}

	public List<Category> getUpdateCategories() {
		return updateCategories;
	}

	public void setUpdateCategories(List<Category> updateCategories) {
		this.updateCategories = updateCategories;
	}

	public List<Integer> getDeleteCategories() {
		return deleteCategories;
	}

	public void setDeleteCategories(List<Integer> deleteCategories) {
		this.deleteCategories = deleteCategories;
	}

	public List<Recipe> getAddRcipes() {
		return addRecipes;
	}

	public void setAddRcipes(List<Recipe> addrRcipes) {
		this.addRecipes = addrRcipes;
	}

	public List<Recipe> getUpdateRecipes() {
		return updateRecipes;
	}

	public void setUpdateRecipes(List<Recipe> updateRecipes) {
		this.updateRecipes = updateRecipes;
	}

	public List<Integer> getDeleteRecipes() {
		return deleteRecipes;
	}

	public void setDeleteRecipes(List<Integer> deleteRecipes) {
		this.deleteRecipes = deleteRecipes;
	}

	public List<Desk> getAddDesks() {
		return addDesks;
	}

	public void setAddDesks(List<Desk> addDesks) {
		this.addDesks = addDesks;
	}

	public List<Desk> getUpdateDesks() {
		return updateDesks;
	}

	public void setUpdateDesks(List<Desk> updateDesks) {
		this.updateDesks = updateDesks;
	}

	public List<Integer> getDeleteDesks() {
		return deleteDesks;
	}

	public void setDeleteDesks(List<Integer> deleteDesks) {
		this.deleteDesks = deleteDesks;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public AllDomain(List<Category> addCategories,
			List<Category> updateCategories, List<Integer> deleteCategories,
			List<Recipe> addrRcipes, List<Recipe> updateRecipes,
			List<Integer> deleteRecipes, List<Desk> addDesks,
			List<Desk> updateDesks, List<Integer> deleteDesks, Date date) {
		super();
		this.addCategories = addCategories;
		this.updateCategories = updateCategories;
		this.deleteCategories = deleteCategories;
		this.addRecipes = addrRcipes;
		this.updateRecipes = updateRecipes;
		this.deleteRecipes = deleteRecipes;
		this.addDesks = addDesks;
		this.updateDesks = updateDesks;
		this.deleteDesks = deleteDesks;
		this.date = date;
	}

	public AllDomain() {
	}
}

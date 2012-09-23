package com.mode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.model.Order;

public class History implements Serializable {
	public List<SelectedProduct> hisSelectedProducts=new ArrayList<SelectedProduct>();
	public History()
	{
		
	}
	public List<SelectedProduct> getHisSelectedProducts() {
		return hisSelectedProducts;
	}

	public void setHisSelectedProducts(
			List<SelectedProduct> hisSelectedProducts) {
		this.hisSelectedProducts = hisSelectedProducts;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}

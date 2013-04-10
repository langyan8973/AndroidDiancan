package com.diancan.Helper;

import java.util.Iterator;

import android.R.integer;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.diancan.diancanapp.AppDiancan;
import com.diancan.model.Category;
import com.diancan.model.MyRestaurant;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.Recipe;

public class OrderHelper {
	Order mOrder;
	SparseArray<Category> categoryDic;
	SparseArray<OrderItem> orderItemDic;
	String portionString;
	int totalCount;
	int countNew;
	
	public OrderHelper(Order order,String strpart){
		SetOrderAndItemDic(order);
	}
	public SparseArray<Category> getCategoryDic() {
		return categoryDic;
	}

	public Order getOrder() {
		return mOrder;
	}

	public void setOrder(Order order) {
		this.mOrder = order;
	}
	public void setCategoryDic(SparseArray<Category> categoryDic) {
		this.categoryDic = categoryDic;
	}

	public SparseArray<OrderItem> getOrderItemDic() {
		return orderItemDic;
	}

	public void setOrderItemDic(SparseArray<OrderItem> orderItemDic) {
		this.orderItemDic = orderItemDic;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getCountNew() {
		return countNew;
	}
	public void setCountNew(int countNew) {
		this.countNew = countNew;
	}
	/**
	 * 新加入订单设置MyReataurant对象
	 * @param order
	 */
	public void SetOrderAndItemDic(Order order){
		mOrder = order;
		if(orderItemDic==null){
			orderItemDic = new SparseArray<OrderItem>();
		}
		totalCount = 0;
		countNew = 0;
		orderItemDic.clear();
		if(order.getClientItems()!=null&&order.getClientItems().size()>0){
			Iterator<OrderItem> iterator;
			for(iterator=order.getClientItems().iterator();iterator.hasNext();){
				OrderItem orderItem=iterator.next();
				orderItemDic.put(orderItem.getRecipe().getId(), orderItem);
				totalCount+=orderItem.GetCount();
				countNew+=orderItem.getCountNew();
			}
		}
	}
	
	public OrderItem SetRecipeCount(Recipe recipe,int deltacount){
		if(orderItemDic.indexOfKey(recipe.getId())>-1){
			OrderItem orderItem = orderItemDic.get(recipe.getId());
			if(deltacount>0){
				orderItem.setCountNew(orderItem.getCountNew()+deltacount);
			}
			else{
				if((orderItem.getCountNew()+deltacount)>=0){
					orderItem.setCountNew(orderItem.getCountNew()+deltacount);
				}else{
					orderItem.setCountNew(0);
				}
			}
			return orderItem;
		}
		else{
			OrderItem nItem =new OrderItem();
			nItem.setRecipe(recipe);
			nItem.setCountNew(deltacount);
			orderItemDic.put(recipe.getId(), nItem);
			return nItem;
		}
	}
	
	public String GetCommitOrderString(){
		String contentString = "";
		StringBuilder stringBuilder = new StringBuilder(contentString);
		Iterator<OrderItem> iterator;
        for(iterator = mOrder.getClientItems().iterator();iterator.hasNext();){
        	OrderItem orderItem = iterator.next();
        	if(orderItem.getCountNew()!=0){
        		stringBuilder.append("\n");
        		stringBuilder.append(orderItem.getRecipe().getName());
        		stringBuilder.append("-------");
        		stringBuilder.append(orderItem.getCountNew()+portionString);
        	}
        }
        contentString = stringBuilder.toString();
        if(contentString.indexOf("\n")==0){
        	contentString = contentString.substring(1);
        }
        return contentString;
	}
	
	public String GetCheckOrderString(){
		String contentString = "";
		StringBuilder stringBuilder = new StringBuilder(contentString);
		Iterator<OrderItem> iterator;
        for(iterator = mOrder.getClientItems().iterator();iterator.hasNext();){
        	OrderItem orderItem = iterator.next();
        	if(orderItem.getCountDeposit()!=0||orderItem.getCountConfirm()!=0){
        		stringBuilder.append("\n");
        		stringBuilder.append(orderItem.getRecipe().getName());
        		stringBuilder.append("-------");
        		stringBuilder.append((orderItem.getCountDeposit()+orderItem.getCountConfirm())+portionString);
        	}
        }
        contentString = stringBuilder.toString();
        if(contentString.indexOf("\n")==0){
        	contentString = contentString.substring(1);
        }
        return contentString;
	}
}

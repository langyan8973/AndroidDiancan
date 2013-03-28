package com.diancan.Utils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import com.diancan.model.History;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;
import com.diancan.model.city;
import com.diancan.model.favorite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {
	public static String ConvertHistoryToJson(History history)
	{
		Gson sGson=new Gson();
		String hiString=sGson.toJson(history);
		return hiString;
	}
	public static History ParseJsonToHistory(String jsonStr)
	{
		Type objType=new TypeToken<History>() {
		}.getType();
		Gson sGson=new Gson();
		History history=null;
		try {
			history = sGson.fromJson(jsonStr, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return history;
	}
	public static Order ParseJsonToOrder(String jsonStr)
	{
		Type objType=new TypeToken<Order>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		Order order=null;
		try {
			order = sGson.fromJson(jsonStr, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return order;
	}
	
	public static Set<OrderItem> parseJsonToItems(String jsonString){
		Type objType=new TypeToken<Set<OrderItem>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		Set<OrderItem> orderItems=null;
		try {
			orderItems = sGson.fromJson(jsonString, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orderItems;
	}
	
	public static List<city> parseJsonTocities(String jsonString){
		Type objType=new TypeToken<List<city>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<city> cities=null;
		try {
			cities = sGson.fromJson(jsonString, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cities;
	}
	
	public static favorite parseJsonToFavorite(String jsonString){
		Type objType=new TypeToken<favorite>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		favorite f=null;
		try {
			f = sGson.fromJson(jsonString, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}
}

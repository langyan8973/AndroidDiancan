package com.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ResponseCache;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.Utils.FileUtils;
import com.Utils.MenuUtils;
import com.model.Order;
import com.model.OrderItem;

public class HttpDownloader {
	public static String getString(String urlStr,String udid) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client= new DefaultHttpClient();
		HttpGet get = new HttpGet(urlStr);
		get.addHeader("accept", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
		get.addHeader("X-device",udid);
		try {
			HttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			} else {
				// TODO 返回错误信息
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// TODO 返回协议错误信息
		} catch (IOException e) {
			e.printStackTrace();
			// TODO 返回网络错误
		}
		return null;
	}
	
	
	public static Bitmap getStream(String urlStr ) {		
		try {
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			connection.setUseCaches(true);
//			BitmapFactory.Options options=new BitmapFactory.Options();
//			options.inSampleSize=2;
//			Bitmap bmp=BitmapFactory.decodeStream(connection.getInputStream(),null,options);
			InputStream iStream=connection.getInputStream();
			Bitmap bmp=BitmapFactory.decodeStream(iStream);
			iStream.close();
			return bmp;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	//启用缓存
	public static void enableHttpResponseCache() {
	  try {
	    long httpCacheSize = 50 * 1024 * 1024; // 10 MiB
	    File httpCacheDir = new File(FileUtils.cacheDir.getAbsolutePath(), "http");
	    Class.forName("android.net.http.HttpResponseCache")
	         .getMethod("install", File.class, long.class)
	         .invoke(null, httpCacheDir, httpCacheSize);
	  } catch (Exception httpResponseCacheNotAvailable) {
		  ResponseCache.setDefault(new MyResponseCache2());
	  }
	}

	public static String RegisterUdid(String id,String strurl) throws ClientProtocolException, IOException{
		  DefaultHttpClient client= new DefaultHttpClient();
		  HttpPost httppost = new HttpPost(strurl); 
		  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1); 
		   nameValuePairs.add(new BasicNameValuePair("udid", id+""));
		   nameValuePairs.add(new BasicNameValuePair("ptype", "1"));
		   httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

		   HttpResponse response; 
		   response=client.execute(httppost); 
		   HttpEntity responseEntity = response.getEntity();
		   String jsonString=parseContent(responseEntity.getContent());
			return jsonString;
	}
	public static String OrderForm(String rootUrl,Order order,String udid) throws ClientProtocolException, IOException,
		JSONException {
		
		DefaultHttpClient client;
		client = new DefaultHttpClient();
		
		System.out.println("提交订单:");
		HttpPost post = new HttpPost(rootUrl + "orders");
		post.addHeader("X-device",udid);
		
		JSONArray list = new JSONArray();
		Iterator<OrderItem> iterator;
		for(iterator=order.getOrderItems().iterator();iterator.hasNext();)
		{
			OrderItem orderItem=iterator.next();
			JSONObject obj = new JSONObject();
			obj.put("rid", orderItem.getRecipe().getId());
			obj.put("count",orderItem.getCount());
			list.put(obj);
		}
		
		JSONObject object = new JSONObject();
		object.put("tid",order.getDesk().getId());
		object.put("number", order.getNumber());
		object.put("recipes", list);
		System.out.println(object.toString());
		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		
		HttpClientParams.setRedirecting(post.getParams(), false);
		
		HttpResponse response = client.execute(post);
		junit.framework.Assert.assertEquals(201, response.getStatusLine().getStatusCode());
		
		String location = response.getLastHeader("Location").getValue();
		System.out.println("创建成功：" + location);
		return location;
	}
	
	public static String GetOrderForm(String reqString,String udid) throws ClientProtocolException, IOException,
	JSONException {
		System.out.println("获取单个订单:");
		DefaultHttpClient client;
		client = new DefaultHttpClient();
		
		HttpGet get = new HttpGet(reqString);
		get.addHeader("accept", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
		get.addHeader("X-device",udid);
		HttpResponse response = client.execute(get);
				
		HttpEntity entity = response.getEntity();
		System.out.println(entity.getContentType().getValue());
		String jsonString=parseContent(entity.getContent());
		System.out.println(jsonString);
		return jsonString;
	}
	
	//开台
	public static String submitOrder(String rootUrl,int tid,int number,String udid) throws Throwable {
		JSONObject object = new JSONObject();
		object.put("tid", tid);
		object.put("number", number);
		
		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		
		HttpPost post = new HttpPost(rootUrl + "orders");
		post.addHeader("X-device",udid);
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		
		HttpClientParams.setRedirecting(post.getParams(), false);
		DefaultHttpClient client=new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		HttpEntity responseEntity = response.getEntity();
		String jsonString=parseContent(responseEntity.getContent());
		System.out.println(jsonString);
		
		System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 201) {
			return jsonString;
		}
		else {
			throw new Exception(jsonString);
		}
	}
	
	/**
	 * 请求结账
	 * @param rootUrl
	 * @param idString
	 * @return
	 * @throws Throwable 
	 */
	public static String RequestFinally(String urlString,String udid) throws Throwable
	{
		
		HttpPut put=new HttpPut(urlString);
		put.addHeader("X-device",udid);
		DefaultHttpClient client=new DefaultHttpClient();
		HttpResponse response=client.execute(put);
		HttpEntity responseEntity = response.getEntity();
		String jsonString=parseContent(responseEntity.getContent());
		System.out.println(jsonString);
		if (response.getStatusLine().getStatusCode() == 200) {
			return jsonString;
		}
		else {
			throw new Exception(jsonString);
		}
	}
	
	/**
	 * 叫服务
	 * @param rootUrl
	 * @param strContent
	 * @return
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String RequestServices(String rootUrl,String strContent,String orderid,String udid) throws JSONException, ClientProtocolException, IOException
	{
		int statuscode;
		if(strContent==MenuUtils.Service_2)
		{
			statuscode=21;
		}
		else if(strContent==MenuUtils.Service_1)
		{
			statuscode=22;
		}
		else {
			statuscode=23;
		}
		
		JSONObject object = new JSONObject();
		object.put("type", statuscode);
		
		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		
		HttpPost post = new HttpPost(rootUrl + "orders/"+orderid+"/assistent");
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		post.addHeader("X-device",udid);
		
		HttpClientParams.setRedirecting(post.getParams(), false);
		DefaultHttpClient client=new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		int code = response.getStatusLine().getStatusCode();
		return null;
	}
	
	/***
	 * 根据邀请码获取订单
	 * @param rootUrl
	 * @param codeString
	 * @return
	 * @throws Throwable
	 */
	public static String RequestOrderByCode(String rootUrl,String codeString,String udid) throws Throwable {
		
		HttpPut put=new HttpPut(rootUrl+"orders/desk/"+codeString);	
		put.addHeader("X-device",udid);
		DefaultHttpClient client=new DefaultHttpClient();
		HttpResponse response = client.execute(put);
		HttpEntity responseEntity = response.getEntity();
		String jsonString=parseContent(responseEntity.getContent());
		System.out.println(jsonString);
		
		System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 201) {
			return jsonString;
		}
		else {
			throw new Exception(jsonString);
		}
	}
	
	//加减菜
	public static String alterRecipeCount(String rootUrl, int oid,int rid,JSONObject object,String udid) throws Throwable
	{
		String urlString=rootUrl + "restaurants/"+rid+"/orders/" + oid;
		HttpPost post = new HttpPost(urlString);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		nameValuePairs.add(new BasicNameValuePair("rid", object.get("rid").toString()));
		nameValuePairs.add(new BasicNameValuePair("count", object.get("count").toString()));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		post.addHeader("X-device",udid);
		
		DefaultHttpClient client=new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		HttpEntity responseEntity = response.getEntity();
		String jsonString=parseContent(responseEntity.getContent());
		
		if (response.getStatusLine().getStatusCode() == 200) {
			return jsonString;
		}
		else {
			throw new Exception(jsonString);
		}
		
	}
	
	private static String parseContent(InputStream stream) throws IOException {
		StringBuilder sb = new StringBuilder();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		String line = reader.readLine();
		while (line != null) {
			sb.append(line);
			line = reader.readLine();
		}
		reader.close();
		return sb.toString();
	}
	
}

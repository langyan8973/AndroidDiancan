package com.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.Utils.FileUtils;
import com.Utils.MenuUtils;
import com.mode.DeskObj;
import com.mode.SelectedMenuObj;
import com.mode.SelectedProduct;
import com.model.Order;
import com.model.OrderItem;
import com.model.Recipe;

public class HttpDownloader {
	static HttpClient client;
	public static String getString(String urlStr) {
		StringBuffer sb = new StringBuffer();
		if(client==null)
		{
			client= new DefaultHttpClient();
		}
		HttpGet get = new HttpGet(urlStr);
		get.addHeader("accept", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
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

	
	public static String OrderForm(String rootUrl,Order order) throws ClientProtocolException, IOException,
		JSONException {
		
		DefaultHttpClient client;
		client = new DefaultHttpClient();
		
		System.out.println("提交订单:");
		HttpPost post = new HttpPost(rootUrl + "orders");
		
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
	
	public static String GetOrderForm(String reqString) throws ClientProtocolException, IOException,
	JSONException {
		System.out.println("获取单个订单:");
		DefaultHttpClient client;
		client = new DefaultHttpClient();
		
		HttpGet get = new HttpGet(reqString);
		get.addHeader("accept", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
		HttpResponse response = client.execute(get);
				
		HttpEntity entity = response.getEntity();
		System.out.println(entity.getContentType().getValue());
		String jsonString=parseContent(entity.getContent());
		System.out.println(jsonString);
		return jsonString;
	}
	
	//开台
	public static String submitOrder(String rootUrl,int tid,int number) throws Throwable {
		JSONObject object = new JSONObject();
		object.put("tid", tid);
		object.put("number", number);
		
		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		
		HttpPost post = new HttpPost(rootUrl + "orders");
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		
		HttpClientParams.setRedirecting(post.getParams(), false);
		
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
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public static String RequestFinally(String rootUrl,String idString) throws ClientProtocolException, IOException, JSONException
	{
		JSONObject object = new JSONObject();
		object.put("status", 12);
		
		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		
		HttpPut put=new HttpPut(rootUrl+"orders/"+idString);	
		put.setEntity(entity);
		client.execute(put);
		
		return null;
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
	public static String RequestServices(String rootUrl,String strContent,String orderid) throws JSONException, ClientProtocolException, IOException
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
		
		HttpClientParams.setRedirecting(post.getParams(), false);
		
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
	public static String RequestOrderByCode(String rootUrl,String codeString) throws Throwable {
		
		HttpPut put=new HttpPut(rootUrl+"orders/desk/"+codeString);		
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
	public static String alterRecipeCount(String rootUrl, int oid,JSONObject object) throws Throwable
	{
		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		
		HttpPost post = new HttpPost(rootUrl + "orders/" + oid);
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		
		HttpClientParams.setRedirecting(post.getParams(), true);
		
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
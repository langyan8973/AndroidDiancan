package com.diancan.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterInputStream;
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
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.diancan.Utils.FileUtils;
import com.diancan.Utils.MenuUtils;
import com.diancan.model.Order;
import com.diancan.model.OrderItem;

public class HttpDownloader {
	private static final String LOG_TAG = "HttpDownloader";
	public static ImageFileCache mImageFileCache;
	public static String getString(String urlStr,String udid,String Authorization) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client= new DefaultHttpClient();
		HttpGet get = new HttpGet(urlStr);
		get.addHeader("accept", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
		get.addHeader("X-device",udid);
		get.addHeader("Authorization",Authorization);
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
		
		Bitmap result = null;
		// 文件缓存中获取
        result = mImageFileCache.getImage(urlStr);
		if(result==null){
			result = downloadBitmap(urlStr);
			if(result!=null){
				mImageFileCache.saveBitmap(result, urlStr);
			}
		}
		return result;
		
//		try {
//			URL url = new URL(urlStr);
//			URLConnection connection = url.openConnection();
//			connection.setUseCaches (true);
//			Log.d("HttpDownloader", "开始请求");
//			long time1=System.currentTimeMillis();   
//			
//			InputStream iStream=connection.getInputStream();
//			
//			long time2=System.currentTimeMillis();   
//			System.out.println("下载图片时间： "+(time2-time1)+"ms"); 
//
//			Log.d("HttpDownloader", "得到图片流");
//			
//			Bitmap bmp=BitmapFactory.decodeStream(new FlushedInputStream(iStream),null,null);
//			
//			long time3=System.currentTimeMillis();   
//			System.out.println("解析图片时间： "+(time3-time2)+"ms"); 
//			Log.d("HttpDownloader", "得到Bitmap");
//			iStream.close();
//			return bmp;
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
	}
	
	public static Bitmap downloadBitmap(String url) {
        final HttpClient client = new DefaultHttpClient();
        final HttpGet getRequest = new HttpGet(url);
                                                               
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(LOG_TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }
                                                                   
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    FilterInputStream fit = new FlushedInputStream(inputStream);
                    return BitmapFactory.decodeStream(fit);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            client.getConnectionManager().shutdown();
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
//	public static String OrderForm(String rootUrl,Order order,String udid) throws ClientProtocolException, IOException,
//		JSONException {
//		
//		DefaultHttpClient client;
//		client = new DefaultHttpClient();
//		
//		System.out.println("提交订单:");
//		HttpPost post = new HttpPost(rootUrl + "orders");
//		post.addHeader("X-device",udid);
//		
//		JSONArray list = new JSONArray();
//		Iterator<OrderItem> iterator;
//		for(iterator=order.getOrderItems().iterator();iterator.hasNext();)
//		{
//			OrderItem orderItem=iterator.next();
//			JSONObject obj = new JSONObject();
//			obj.put("rid", orderItem.getRecipe().getId());
//			obj.put("count",orderItem.getCount());
//			list.put(obj);
//		}
//		
//		JSONObject object = new JSONObject();
//		object.put("tid",order.getDesk().getId());
//		object.put("number", order.getNumber());
//		object.put("recipes", list);
//		System.out.println(object.toString());
//		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
//		entity.setContentType("application/json;charset=UTF-8");
//		entity.setContentEncoding("UTF-8");
//		
//		post.setEntity(entity);
//		post.setHeader("Content-Type", "application/json;charset=UTF-8");
//		
//		HttpClientParams.setRedirecting(post.getParams(), false);
//		
//		HttpResponse response = client.execute(post);
//		junit.framework.Assert.assertEquals(201, response.getStatusLine().getStatusCode());
//		
//		String location = response.getLastHeader("Location").getValue();
//		System.out.println("创建成功：" + location);
//		return location;
//	}
	
	public static String GetOrderForm(String reqString,String udid,String Authorization) throws ClientProtocolException, IOException,
	JSONException {
		System.out.println("获取单个订单:");
		DefaultHttpClient client;
		client = new DefaultHttpClient();
		
		HttpGet get = new HttpGet(reqString);
		get.addHeader("accept", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
		get.addHeader("X-device",udid);
		get.addHeader("Authorization",Authorization);
		HttpResponse response = client.execute(get);
				
		HttpEntity entity = response.getEntity();
		System.out.println(entity.getContentType().getValue());
		String jsonString=parseContent(entity.getContent());
		System.out.println(jsonString);
		return jsonString;
	}
	
	/**
	 * 开台
	 * @param rootUrl
	 * @param tid
	 * @param number
	 * @param udid
	 * @return
	 * @throws Throwable
	 */
	public static String submitOrder(String rootUrl,int tid,int number,String udid,String Authorization) throws Throwable {
		JSONObject object = new JSONObject();
		object.put("tid", tid);
		object.put("number", number);
		
		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		
		HttpPost post = new HttpPost(rootUrl + "orders");
		post.addHeader("X-device",udid);
		post.addHeader("Authorization",Authorization);
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
	
	public static String GetOrderByCode(String rootUrl,String udid,String Authorization) throws Exception{
		HttpPost post = new HttpPost(rootUrl);
		post.addHeader("X-device",udid);
		post.addHeader("Authorization",Authorization);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		HttpClientParams.setRedirecting(post.getParams(), false);
		DefaultHttpClient client=new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		HttpEntity responseEntity = response.getEntity();
		String jsonString=parseContent(responseEntity.getContent());
		System.out.println(jsonString);
		
		System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 200) {
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
	public static String RequestFinally(String urlString,String udid,String Authorization) throws Throwable
	{
		
		HttpPut put=new HttpPut(urlString);
		put.addHeader("X-device",udid);
		put.addHeader("Authorization",Authorization);
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
	public static String RequestServices(String rootUrl,String strContent,String orderid,
			String udid,String Authorization) throws JSONException, ClientProtocolException, IOException
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
		post.addHeader("Authorization", Authorization);
		
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
	public static String RequestOrderByCode(String rootUrl,String codeString,String udid,String Authorization) throws Throwable {
		
		HttpPut put=new HttpPut(rootUrl+"orders/desk/"+codeString);	
		put.addHeader("X-device",udid);
		put.addHeader("Authorization", Authorization);
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
	public static String alterRecipeCount(String rootUrl, int oid,int rid,JSONObject object,
			String udid,String Authorization) throws Throwable
	{
		String urlString=rootUrl + "restaurants/"+rid+"/orders/" + oid;
		HttpPost post = new HttpPost(urlString);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		nameValuePairs.add(new BasicNameValuePair("rid", object.get("rid").toString()));
		nameValuePairs.add(new BasicNameValuePair("count", object.get("count").toString()));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		post.addHeader("X-device",udid);
		post.addHeader("Authorization", Authorization);
		
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
	
	/**
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
	
}

package com.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.CacheResponse;
import java.util.List;
import java.util.Map;
 
public class MyCacheResponse extends CacheResponse {
	
	FileInputStream fis;
	Map<String, List<String>> headers;
	File cacheFile;
	public MyCacheResponse(File cacheFile) {
		this.cacheFile = cacheFile;
		
		try {
			 fis = new FileInputStream(cacheFile);
			 ObjectInputStream ois = new ObjectInputStream (fis);
			 HttpHeaderWrapper sc = (HttpHeaderWrapper)ois.readObject();
			 headers = sc.getRspHeaders();
		   } catch (IOException ex) {
			   String dd=ex.getMessage();
			   String ss=dd;
		   } catch (ClassNotFoundException e) {
		}
	}
 
	@Override
	public InputStream getBody() throws FileNotFoundException {
			return fis;
	}
 
	@Override
	public Map<String, List<String>> getHeaders() {
		return headers;
	}
}
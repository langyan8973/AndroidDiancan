package com.download;

import java.io.File;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.Utils.FileUtils;

public class MyResponseCache2 extends ResponseCache {
	String cacheDir = FileUtils.cacheDir.getAbsolutePath();

	@Override
	public CacheResponse get(URI uri, String requestMethod,
			Map<String, List<String>> requestHeaders) {
		File file = new File(cacheDir, escape(uri.getPath()));
		if (file.exists()) {
			CacheResponse resp = new MyCacheResponse(file);
			return resp;
		}
		return null;
	}

	@Override
	public CacheRequest put(URI uri, URLConnection conn) {
		File file = new File(cacheDir, escape(conn.getURL().getPath()));
		CacheRequest req = (CacheRequest) new MyCacheRequest(file,
				conn.getHeaderFields());
		return req;
	}

	private String escape(String url) {
		return url.replace("/", "-").replace(".", "-");
	}
}
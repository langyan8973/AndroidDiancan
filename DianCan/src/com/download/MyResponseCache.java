package com.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.Utils.FileUtils;

public class MyResponseCache extends ResponseCache {
	String cacheDir = FileUtils.cacheDir.getAbsolutePath();
	
	@Override
    public CacheResponse get(URI uri, String s, Map<String, List<String>> headers) throws IOException {
        final File file = new File(cacheDir, escape(uri.getPath()));
        if (!file.exists()) {
        	return null;
        } else {
        	CacheResponse cacheResponse = new CacheResponse() {
                @Override
                public Map<String, List<String>> getHeaders() throws IOException {
                    return null;
                }

                @Override
                public InputStream getBody() throws IOException {
                    return new FileInputStream(file);
                }
            };
        	return cacheResponse;
        }
    }

    @Override
    public CacheRequest put(URI uri, URLConnection urlConnection) throws IOException {
        final File file = new File(cacheDir, escape(urlConnection.getURL().getPath()));
        return new CacheRequest() {
            @Override
            public OutputStream getBody() throws IOException {
                return new FileOutputStream(file);
            }

            @Override
            public void abort() {
                file.delete();
            }
        };
    }

    private String escape(String url) {
       return url.replace("/", "-").replace(".", "-");
    }
}

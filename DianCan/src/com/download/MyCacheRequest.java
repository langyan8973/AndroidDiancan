package com.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.util.List;
import java.util.Map;
 
public class MyCacheRequest extends CacheRequest  {
 
	FileOutputStream fos;

	File cacheFile;
	Map<String, List<String>> rspHeaders;
	public MyCacheRequest(File cacheFile,Map<String, List<String>> rspHeaders) {
		this.cacheFile = cacheFile;
		this.rspHeaders = rspHeaders;
		
		try {
			fos = new FileOutputStream(cacheFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			HttpHeaderWrapper sc = new HttpHeaderWrapper();
			sc.setRspHeaders(rspHeaders);
			oos.writeObject(sc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@Override
    public OutputStream getBody() throws IOException {
        return fos;
    }

    @Override
    public void abort() {
    	cacheFile.delete();
    }
}

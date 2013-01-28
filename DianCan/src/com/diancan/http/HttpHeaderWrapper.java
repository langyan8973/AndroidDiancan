package com.diancan.http;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class HttpHeaderWrapper  implements Serializable{
	private Map<String, List<String>> rspHeaders;

    public HttpHeaderWrapper() {
    	
    }

	public Map<String, List<String>> getRspHeaders() {
		return rspHeaders;
	}

	public void setRspHeaders(Map<String, List<String>> rspHeaders) {
		this.rspHeaders = rspHeaders;
	}

}

package com.mode;

public class ServiceMess {
	String id;
	String deskId;
	String sText;
	String messReturned;
	boolean isComplete;
	public String getDeskId() {
		return deskId;
	}
	public void setDeskId(String deskId) {
		this.deskId = deskId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getsText() {
		return sText;
	}
	public void setsText(String sText) {
		this.sText = sText;
	}
	public String getMessReturned() {
		return messReturned;
	}
	public void setMessReturned(String messReturned) {
		this.messReturned = messReturned;
	}
	public boolean isComplete() {
		return isComplete;
	}
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}
	
}

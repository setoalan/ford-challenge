package com.logicdrop.fordchallenge.api.services;

public class IncidentData {

	private String type;
	private String date;
	private String location;
	private String area;
	private String latlng;
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getArea() {
		return this.area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}
	
	public String getLatlng() {
		return this.latlng;
	}
	
	public void setLatlng(String latlng) {
		this.latlng = latlng;
	}
}

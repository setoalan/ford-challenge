package com.logicdrop.fordchallenge.api.services;


public interface IterisInterface {

	TrafficResponse getIncidentReport(final String operation, final String types, 
			final String regionIds, final String licenseKey, final String url) throws TrafficServiceException;
	
}

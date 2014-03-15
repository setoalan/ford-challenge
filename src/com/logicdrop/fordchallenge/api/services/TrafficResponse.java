package com.logicdrop.fordchallenge.api.services;

import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.logicdrop.fordchallenge.incidents.IncidentBean;

/**
 * Complete response from the traffic service.
 * <p>
 * Contains incident data if available.
 * 
 * @author kjq
 * 
 */
@Root(strict = false)
public class TrafficResponse
{
	@Attribute(name = "data_timestamp", required = false)
	private String dataTimestamp;

	@Attribute(name = "xml_timestamp", required = false)
	private String xmlTimestamp;

	@Attribute(name = "freq", required = false)
	private int freq;

	@ElementList(name = "Incidents", required = false)
	private List<IncidentBean> incidents;

	/**
	 * @return the dataTimestamp
	 */
	public String getDataTimestamp()
	{
		return this.dataTimestamp;
	}

	/**
	 * @return the freq
	 */
	public int getFrequency()
	{
		return this.freq;
	}

	/**
	 * @return the trafficIncidents
	 */
	public List<IncidentBean> getIncidents()
	{
		return this.incidents;
	}

	/**
	 * @return the xmlTimestamp
	 */
	public String getXmlTimestamp()
	{
		return this.xmlTimestamp;
	}
}

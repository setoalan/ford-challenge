package com.logicdrop.fordchallenge.incidents;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.logicdrop.fordchallenge.api.utils.StringHelper;

/**
 * Contains a traffic incident report.
 * 
 * @author kjq
 * 
 */
@Root(name = "Incident", strict = false)
public class IncidentBean
{
	@Attribute(name = "id", required = false)
	private String id;

	@Attribute(name = "timestamp", required = false)
	private String timestamp;

	@Attribute(name = "type", required = false)
	private String type;

	@Attribute(name = "agency", required = false)
	private String agency;

	@Attribute(name = "location", required = false)
	private String location;

	@Attribute(name = "area", required = false)
	private String area;

	@Attribute(name = "latlng", required = false)
	private String latLong;

	@Attribute(name = "feed", required = false)
	private String feed;

	/**
	 * @return the agency
	 */
	public String getAgency()
	{
		return this.agency;
	}

	/**
	 * @return the area
	 */
	public String getArea()
	{
		return this.area;
	}

	/**
	 * @return the feed
	 */
	public String getFeed()
	{
		return this.feed;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @return the latLong
	 */
	public String getLatLong()
	{
		return this.latLong;
	}

	/**
	 * @return the location
	 */
	public String getLocation()
	{
		return this.location;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp()
	{
		return this.timestamp;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * Return '|' delimited string of the values
	 */
	@Override
	public String toString()
	{
		final String[] values = new String[] {
				this.type,
				this.timestamp,
				this.location,
				this.area,
				this.latLong
		};

		return StringHelper.join('|', "???", values);
	}
}

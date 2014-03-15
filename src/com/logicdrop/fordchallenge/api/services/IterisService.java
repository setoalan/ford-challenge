package com.logicdrop.fordchallenge.api.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

import com.logicdrop.fordchallenge.api.services.TrafficLandParser.Entry;
import com.logicdrop.fordchallenge.api.utils.StringHelper;
import com.logicdrop.fordchallenge.incidents.IncidentBean;
import com.logicdrop.fordchallenge.incidents.IncidentsActivity;

public class IterisService extends IntentService implements IterisInterface
{
	/**
	 * Retrieve measurements operation
	 */
	public static final String RETRIEVE_MEASUREMENTS = "retrieveMeasurements";

	/**
	 * Retrieve incidents operation
	 */
	public static final String RETRIEVE_INCIDENTS = "retrieveIncidents";
	
	private static ArrayList<IncidentData> incidentData;
	private static List<Entry> traffic;
	
	public IterisService() {
		super(null);
		incidentData = new ArrayList<IncidentData>();
		
		getTrafficReport webFetch = new getTrafficReport();
		webFetch.execute();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicdrop.trafficsync.api.ITrafficReport
	 * #getTrafficReport(java.lang.String, java.lang.String, java.lang.String, java.lang.String,java.lang.String)
	 * 
	 * Incident URL: http://ims.meridian-enviro.com/demo/riis.pl?op=retrieveIncidents&type=government&rids=41860&key=300D91
	 */
	@Override
	public TrafficResponse getIncidentReport(final String operation, final String types, final String regionIds, 
			final String licenseKey, final String url) throws TrafficServiceException
	{
		// Build the arguments
		final String query = StringHelper.join('&', "", new String[] {
				ParameterType.O + operation,
				ParameterType.T + types,
				ParameterType.R + regionIds,
				ParameterType.K + licenseKey });

		// Build the URL
		final String serviceUrl = url + "?" + query;

		// String of the result to process
		String result = null;
		
		try
		{
			// Prepare the client
			final HttpClient httpclient = new DefaultHttpClient();

			// This is a GET request
			final HttpUriRequest request = new HttpGet(serviceUrl);

			// Execute the request and get the status and response
			final HttpResponse response = httpclient.execute(request);
			final StatusLine status = response.getStatusLine();

			// If status is ok stream in the response.
			if (status.getStatusCode() == HttpStatus.SC_OK)
			{
				final ByteArrayOutputStream out = new ByteArrayOutputStream();

				try
				{
					response.getEntity().writeTo(out);
					result = out.toString();
					//Log.d("String", result);
				}
				finally
				{
					out.close();
				}
			}
			else
			{
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(status.getReasonPhrase());
			}
		}
		catch (final Exception x)
		{
			throw new TrafficServiceException(x);
		}

		// Return the deserialized result
		return deserialize(result);
	}
	
	class getTrafficReport extends AsyncTask<Void, Void, Void>{
		TextView trafficView;
		TrafficResponse incidents;

		@Override
		protected Void doInBackground(Void... arg0)
		{
			try {
				incidents = getIncidentReport("retrieveIncidents", "government", "41860", "300D91", "http://ims.meridian-enviro.com/demo/riis.pl");
			} catch (TrafficServiceException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPreExecute(){}
		
		@Override
		protected void onPostExecute(Void result){
			parseIncidents();
		}
		
		public void parseIncidents(){
			String[] parts;
			for (IncidentBean ib : incidents.getIncidents()) {
				parts = ib.toString().split("\\|");
				IncidentData temp = new IncidentData();
				temp.setType(parts[0]);
				temp.setDate(parts[1]);
				temp.setLocation(parts[2]);
				temp.setArea(parts[3]);
				temp.setLatlng(parts[4]);
				incidentData.add(temp);
			}

	        for (IncidentData td : incidentData) {
	            Map<String, String> map = new HashMap<String, String>();
				List<String> items = Arrays.asList(td.getLatlng().split("\\s*,\\s*"));
				Double distance = distanceFrom(items);
	            map.put("1", String.format("%.1f mi%n", distance));
	            map.put("2", td.getLocation());
	            map.put("3", td.getLatlng());
	            IncidentsActivity.getFillMaps().add(map);
	        }
	        IncidentsActivity.getAdapter().notifyDataSetChanged();
		}
	}
	
	private double distanceFrom(List<String> item) {
		double lat = Double.parseDouble(item.get(0));
		double lng = Double.parseDouble(item.get(1));
		if (IncidentsActivity.getLocation() == null)
			return 0;
		double theta = lng - IncidentsActivity.getLocation().getLongitude();
		double distance = Math.sin(deg2rad(lat)) * Math.sin(deg2rad(IncidentsActivity.getLocation().getLatitude())) 
				 + Math.cos(deg2rad(lat)) * Math.cos(deg2rad(IncidentsActivity.getLocation().getLatitude())) 
				 * Math.cos(deg2rad(theta));
		distance = Math.acos(distance);
		distance = Math.toDegrees(distance);
		distance = distance * 60 * 1.1515;
		return distance;
	}

	private double deg2rad(double deg) {
		  return (deg * Math.PI / 180.0);
	}

	/**
	 * Deserializes the XML response to a bean.
	 * 
	 * @param response XML string to deserialize.
	 * @return a TrafficResponse bean
	 */
	private static TrafficResponse deserialize(final String response)
	{
		TrafficResponse result;

		final Serializer serializer = new Persister();

		try
		{
			result = serializer.read(TrafficResponse.class, response);
		}
		catch (final Exception x)
		{
			// If there is an error return an empty response.
			result = new TrafficResponse();
		}
		return result;
	}

	/**
	 * Parameters used in the query string.
	 */
	static enum ParameterType
	{
		//Incident Parameter Types
		/**
		 * Operation
		 */
		O("op="),
		/**
		 * Type
		 */
		T("type="),
		/**
		 * Regions
		 */
		R("rids="),
		/**
		 * License Key
		 */
		K("key="),
		//Traffic Parameter Types (Uses Key and Type from Incident Parameter Types)
		/**
		 * System 
		 */
		S("system="),
		/**
		 * Version
		 */
		V("version=");

		private final String text;

		private ParameterType(final String text)
		{
			this.text = text;
		}

		@Override
		public String toString()
		{
			return this.text;
		}
	}
	
	public static ArrayList<IncidentData> getIncidentData() {
		return incidentData;
	}
	
	public static List<Entry> getTraffic() {
		return traffic;
	}
}
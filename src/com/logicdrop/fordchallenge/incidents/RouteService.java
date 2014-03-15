package com.logicdrop.fordchallenge.incidents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;

import com.logicdrop.fordchallenge.api.utils.StringHelper;

public class RouteService extends IntentService {

	private String url = "http://maps.googleapis.com/maps/api/directions/json";
	private static JSONObject directions;

	public RouteService(String start, String end, boolean avoidHighways) {
		super(null);
		
		getRouteReport report = new getRouteReport(start, end, avoidHighways);
		report.execute();
	}

	@Override
	protected void onHandleIntent(Intent intent) {}
	
	public class getRouteReport extends AsyncTask<Void, Void, Void>
	{
		String start, end;
		Boolean avoidHighways;
		 
		public getRouteReport(String start, String end, Boolean avoidHighways) {
			this.start = start;
			this.end = end;
			this.avoidHighways = avoidHighways;
		}

		@Override
		protected Void doInBackground(Void... params) {
			String ah = (avoidHighways) ? "highways" : null;
			try {
				directions = getMapReport(start, end, ah);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		 
		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected void onPostExecute(Void result) {
			IncidentsActivity.getMDialog().dismiss();
		}
	}
	
	private JSONObject getMapReport(String startLoc, String endLoc, String avoidHighways) throws JSONException {
		final String query = StringHelper.join('&', "", new String[] {
			ParameterType.O + startLoc,
			ParameterType.D + endLoc,
			ParameterType.R + "",
			ParameterType.S + "",
			ParameterType.H + avoidHighways});
		final String serviceUrl = url + "?" + query;
		String result = null;
		
		try {
			final HttpClient httpclient = new DefaultHttpClient();
			final HttpUriRequest request = new HttpGet(serviceUrl);
			final HttpResponse response = httpclient.execute(request);
			final StatusLine status = response.getStatusLine();
			if (status.getStatusCode() == HttpStatus.SC_OK) {
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				
				try {
					response.getEntity().writeTo(out);
					result = out.toString();
				} finally {
					out.close();
				}
			} else {
				response.getEntity().getContent().close();
				throw new IOException(status.getReasonPhrase());
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
		return new JSONObject(result);
	}
	
	/**
	 * Parameters used in the query string.
	 */
	static enum ParameterType
	{
		/**
		 * Origin
		 */
		O("origin="),
		/**
		 * Destination
		 */
		D("destination="),
		/**
		 * Region
		 */
		R("region=us"),
		/**
		 * Sensor
		 */
		S("sensor=false"),
		/**
		 * Highways
		 */
		H("avoid=");

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
	
	public static JSONObject getDirections() {
		return directions;
	}
	
	public static JSONArray getSteps() throws JSONException {
		return directions.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
	}
}

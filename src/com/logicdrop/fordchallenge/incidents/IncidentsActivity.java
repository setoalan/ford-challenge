package com.logicdrop.fordchallenge.incidents;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.logicdrop.fordchallenge.CreateRouteActivity;
import com.logicdrop.fordchallenge.R;
import com.logicdrop.fordchallenge.api.services.IncidentData;
import com.logicdrop.fordchallenge.api.services.IterisService;
import com.logicdrop.fordchallenge.api.services.TrafficLandParser;
import com.logicdrop.fordchallenge.api.services.TrafficLandParser.Entry;

public class IncidentsActivity extends Activity implements LocationListener {
	
	private ListView incidentList;

	private static SimpleAdapter adapter;
	private static List<Map<String, String>> fillMaps;
	private static String[] from;
    private static int[] to;
    
    private static GoogleMap mMap;
	private LocationManager locationManager;
	private String locationProvider;
	private static Location lastKnownLocation;
	private Polyline polyline;
	
	private View v;
	private ImageView image;
	private InputStream is, is2;
	private TextView title, snippet, name, orientation, routeInfo;
	
	private static ProgressDialog mDialog;
	private List<Entry> entries;
	private TrafficLandParser trafficLandParser;
	private AssetManager am;
	
	private Builder dialog;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		setContentView(R.layout.incidents);
		setUpMapIfNeeded(mMap);
		
		// Check if there is connection to the network
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			incidentList = (ListView)findViewById(R.id.incidents);
	        from = new String[] {"1", "2"};
	        to = new int[] { R.id.item1, R.id.item2 };
	        fillMaps = new ArrayList<Map<String, String>>();
			adapter = new SimpleAdapter(this, fillMaps, R.layout.incidents_list_item, from, to);
			// Setup of listview of traffic incidents drawer.
			incidentList.setAdapter(adapter);
			// Animation of clicking of listview item
			incidentList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int childPosition, long id) {
					String[] separated = fillMaps.get(childPosition).get("3").split(",");
					mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(separated[0]), Double.parseDouble(separated[1])), 13));
				}
			});
		}
		
		// Updating of maps after user has routed a route after dialog.
		mDialog = new ProgressDialog(this);
		mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {		
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					updateMap(mMap, RouteService.getDirections());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
		// Parsing of TrafficLand camera feeds.
		trafficLandParser = new TrafficLandParser();
		am = getAssets();
		try {
			is2 = am.open("trafficLand.xml");
			entries = trafficLandParser.parse(is2);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Displaying of dialog when calling all services
		if (CreateRouteActivity.isLoading()) {
			CreateRouteActivity.setLoading(false);
			mDialog.setMessage("Loading...");
			mDialog.setCancelable(false);
			mDialog.show();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		CreateRouteActivity.setServiceCalled(false);
	}
	
	@Override
	public void onBackPressed() {
		// Keeps user from accidentally clicking back and losing map data instance.
		if (CreateRouteActivity.isServiceCalled()) {
		    new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Closing Ford Challenge")
		        .setMessage("Are you sure you want to exit? Map data will be lost.")
		        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            finish();    
		        }
		    })
		    .setNegativeButton("No", null)
		    .show();
		} else {
			finish();
		}
	}

	private boolean setUpLocationServices() {
		// Determine if location services are enabled.
		boolean gpsEnabled, networkEnabled;
		if (locationManager == null)
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        // If not enabled, display dialog to go to location settings
        if (!gpsEnabled && !networkEnabled) {
        	dialog = new AlertDialog.Builder(this);
        	dialog.setMessage("GPS Network Not Enabled");
        	dialog.setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			});
        	dialog.show();
        	return false;
        }
        // If enabled, get requests for location updates.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 0, this);
        locationProvider = LocationManager.NETWORK_PROVIDER;
        lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        return true;
	}
	
	private void setUpMapIfNeeded(GoogleMap mMap) {
	    if (mMap == null)
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    if (mMap != null) {
	        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        mMap.setTrafficEnabled(true);
	        mMap.setMyLocationEnabled(true);
	        if (setUpLocationServices())
	        	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 12));
	        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
				@Override
				public View getInfoContents(Marker marker) {
					return null;
				}

				// Setup of clicking on camera icons for an imageview of its camera feed
				@Override
				public View getInfoWindow(Marker marker) {
					if (marker.getSnippet() == null) {
						// For camera icons when clicked.
						v = getLayoutInflater().inflate(R.layout.camera_marker, null);
						name = (TextView) v.findViewById(R.id.cameraName);
						image = (ImageView) v.findViewById(R.id.cameraImage);
						orientation = (TextView) v.findViewById(R.id.cameraOrientation);
						StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
						StrictMode.setThreadPolicy(policy);
						try {
							List<String> camInfo = Arrays.asList(TrafficLandParser.getWebIdMap().get(marker.getTitle()).split(","));
							name.setText(camInfo.get(0));
							orientation.setText(camInfo.get(1));
							is = (InputStream) new URL(camInfo.get(2)).getContent();
							image.setImageDrawable(Drawable.createFromStream(is, "src name"));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}	
					} else {
						// For traffic incident icons when clicked.
						v = getLayoutInflater().inflate(R.layout.incident_marker, null);
						title = (TextView) v.findViewById(R.id.markerTitle);
						snippet = (TextView) v.findViewById(R.id.markerSnippet);
						title.setText(marker.getTitle());
						snippet.setText(marker.getSnippet());
					}
					return v;
				}
	        });    
		}
	}

	private void updateMap(GoogleMap mMap, JSONObject jsonObj) throws JSONException {
		// Check if directions are valid
		if (!jsonObj.getString("status").equals("OK")) {
			Toast.makeText(this, "Invalid Route", Toast.LENGTH_LONG).show();
			return;
		}
    	JSONObject route = jsonObj.getJSONArray("routes").getJSONObject(0);
    	JSONObject legs = route.getJSONArray("legs").getJSONObject(0);
    	JSONArray steps = legs.getJSONArray("steps");
	    if (mMap == null)
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    if (mMap != null) {
	    	// If route line is present, remove it.
	    	if (polyline != null && polyline.isVisible())
	    		polyline.remove();
	    	// Display route info on black bar.
	    	routeInfo = (TextView) findViewById(R.id.routeInfo);
    		routeInfo.setText("Est. Time (" + legs.getJSONObject("duration").getString("text") 
    					+ ") Distance (" + legs.getJSONObject("distance").getString("text") + ")");
	    	ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
	    	// Generation of list of directions.
	    	for (int i = 0; i < steps.length(); i++) {
	    		ArrayList<LatLng> arr = decodePoly(steps.getJSONObject(i).getJSONObject("polyline").getString("points"));
	    		for (int j = 0; j < arr.size(); j++)
	    			listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
	    	}
	    	PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.BLUE);
	        for (int i=0; i < listGeopoints.size(); i++)
	        	rectLine.add(listGeopoints.get(i));
	        mMap.clear();
	        // Drawing the route on the map.
	        polyline = mMap.addPolyline(rectLine);
	        for (IncidentData td : IterisService.getIncidentData()) {
	        	// Drawing the traffic incident icons on the map.
	        	String[] separated = td.getLatlng().split(",");
	        	mMap.addMarker(new MarkerOptions()
	           		.position(new LatLng(Double.parseDouble(separated[0]),Double.parseDouble(separated[1])))
	           		.title(td.getType())
	           		.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_warning))
	           		.snippet(td.getLocation()));
	        }
	        for (Entry entry : entries) {
	        	// Drawing the camera icons on the map.
        		if (Double.parseDouble(entry.location[0]) >= route.getJSONObject("bounds").getJSONObject("southwest").getDouble("lat") &&
        			Double.parseDouble(entry.location[0]) <= route.getJSONObject("bounds").getJSONObject("northeast").getDouble("lat") &&
        			Double.parseDouble(entry.location[1]) >= route.getJSONObject("bounds").getJSONObject("southwest").getDouble("lng") &&
        			Double.parseDouble(entry.location[1]) <= route.getJSONObject("bounds").getJSONObject("northeast").getDouble("lng")) {
        			mMap.addMarker(new MarkerOptions()
            			.position(new LatLng(Double.parseDouble(entry.location[0]),Double.parseDouble(entry.location[1])))
            			.title(entry.webid)
            			.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_traffic_cam)));
        		}
        	}
	        // Animate the window to fit the bounds of the route.
	        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
	           		new LatLng(route.getJSONObject("bounds").getJSONObject("southwest").getDouble("lat"), 
	           				   route.getJSONObject("bounds").getJSONObject("southwest").getDouble("lng")),  
	           		new LatLng(route.getJSONObject("bounds").getJSONObject("northeast").getDouble("lat"), 
	                   		   route.getJSONObject("bounds").getJSONObject("northeast").getDouble("lng"))), 30));
	    }
	    return;
	}
	
	private static ArrayList<LatLng> decodePoly(String encoded) {
		// The decoding of the route polylines
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
	    while (index < len) {
	        int i, shift = 0, result = 0;
	        do {
	            i = encoded.charAt(index++) - 63;
	            result |= (i & 0x1f) << shift;
	            shift += 5;
	        } while (i >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;
	        shift = 0;
	        result = 0;
	        do {
	            i = encoded.charAt(index++) - 63;
	            result |= (i & 0x1f) << shift;
	            shift += 5;
	        } while (i >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
	        poly.add(position);
	    }
	    return poly;
	}
	
	public static Location getLocation() {
		return lastKnownLocation;
	}
	
	public static SimpleAdapter getAdapter() {
		return adapter;
	}

	public static List<Map<String, String>> getFillMaps() {
		return fillMaps;
	}
	
	public static ProgressDialog getMDialog() {
		return mDialog;
	}
	
	public static Location getLastKnownLocation() {
		return lastKnownLocation; 
	}

	@Override
	public void onLocationChanged(Location location) {
		lastKnownLocation.setLatitude(location.getLatitude());
		lastKnownLocation.setLongitude(location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {}

	@Override
	public void onProviderEnabled(String arg0) {}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
}

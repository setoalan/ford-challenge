package com.logicdrop.fordchallenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.logicdrop.fordchallenge.api.services.IterisService;
import com.logicdrop.fordchallenge.incidents.IncidentsActivity;
import com.logicdrop.fordchallenge.incidents.RouteService;
import com.logicdrop.fordchallenge.route.MyRoutesActivity;
import com.logicdrop.fordchallenge.route.Route;

public class CreateRouteActivity extends Activity {

	private ImageButton swap;
	private Button saveRoute, routeRoute;
	private AutoCompleteTextView startLoc, endLoc;
	private CheckBox avoidHighways;
	private AlertDialog.Builder alert;
    private AlertDialog alertd;
    private EditText input;
    private String[] myloc;
	private ArrayAdapter<String> adapter;
	
    private GoogleMap mMap;
	
	private static boolean serviceCalled;
	private static boolean loading = false;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_route);
		
		saveRoute = (Button) findViewById(R.id.saveRoute);
		routeRoute = (Button) findViewById(R.id.routeRoute);
		swap = (ImageButton) findViewById(R.id.swap);
		startLoc = (AutoCompleteTextView) findViewById(R.id.startLoc);
		endLoc = (AutoCompleteTextView) findViewById(R.id.endLoc);
		avoidHighways = (CheckBox) findViewById(R.id.avoidHighways);
		
		myloc = getResources().getStringArray(R.array.my_location);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myloc);
		startLoc.setAdapter(adapter);
		endLoc.setAdapter(adapter);
		
		setUpMapIfNeeded(mMap);
		
		// Dialog for saving a route.
		alert = new AlertDialog.Builder(this);
	    alertd = alert.create();
	   	alert.setTitle("Save Route");
	   	alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	   		public void onClick(DialogInterface dialog, int whichButton) {
	   			createNewRoute(input.getText().toString());
	   			MainActivity.tabHost.setCurrentTab(1);
	   			finish();
	   		}
	   	});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				alertd.dismiss();
			}
	  	});
		
		swap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String temp = startLoc.getText().toString();
				startLoc.setText(endLoc.getText());
				endLoc.setText(temp);
			} 
		});
		
		saveRoute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (startLoc.getText().toString().matches("") || endLoc.getText().toString().matches("")) {
					Toast.makeText(CreateRouteActivity.this, "Please enter start & end destination.", Toast.LENGTH_SHORT).show();	
				} else {
					input = new EditText(CreateRouteActivity.this);
					input.setLines(1);
					input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
					alert.setView(input);
					alert.show();
				}
			}
		});
		
		routeRoute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check if inputs are blank, both "my locations", or both the same.
				if ((startLoc.getText().toString().matches("") || endLoc.getText().toString().matches("")) ||
					(startLoc.getText().toString().equalsIgnoreCase("my location") && endLoc.getText().toString().equalsIgnoreCase("my location")) ||
					(startLoc.getText().toString().equalsIgnoreCase(endLoc.getText().toString()))) {
						Toast.makeText(CreateRouteActivity.this, "Please enter start & end destination.", Toast.LENGTH_SHORT).show();	
				} else {
					// Calls for Iteris service if it has not been called yet
					if (!serviceCalled) {
						new IterisService();
						serviceCalled = true;
					}
					// Calls for Google Directions service
					if(startLoc.getText().toString().equalsIgnoreCase("my location")) {
						if (IncidentsActivity.getLocation() != null) {
							new RouteService(IncidentsActivity.getLastKnownLocation().getLatitude() + "," + IncidentsActivity.getLastKnownLocation().getLongitude(), endLoc.getText().toString().replaceAll("\\s",""), avoidHighways.isChecked());
							MainActivity.setStartLoc(IncidentsActivity.getLastKnownLocation().getLatitude() + "," + IncidentsActivity.getLastKnownLocation().getLongitude());
							MainActivity.setEndLoc(endLoc.getText().toString().replaceAll("\\s",""));
						}
					} else if (endLoc.getText().toString().equalsIgnoreCase("my location")) {
						if (IncidentsActivity.getLocation() != null) {
							new RouteService(startLoc.getText().toString().replaceAll("\\s",""), IncidentsActivity.getLastKnownLocation().getLatitude() + "," + IncidentsActivity.getLastKnownLocation().getLongitude(), avoidHighways.isChecked());
							MainActivity.setStartLoc(startLoc.getText().toString().replaceAll("\\s",""));
							MainActivity.setEndLoc(IncidentsActivity.getLastKnownLocation().getLatitude() + "," + IncidentsActivity.getLastKnownLocation().getLongitude());
						}
					} else {
						new RouteService(startLoc.getText().toString().replaceAll("\\s",""), endLoc.getText().toString().replaceAll("\\s",""), avoidHighways.isChecked());
						MainActivity.setStartLoc(startLoc.getText().toString().replaceAll("\\s",""));
						MainActivity.setEndLoc(endLoc.getText().toString().replaceAll("\\s",""));
					}
					loading = true;
					imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					MainActivity.routeFromCreate = true;
					MainActivity.routed = true;
					MainActivity.tabHost.setCurrentTab(0);
					finish();
				}
			}
		});
	}
	
	private void setUpMapIfNeeded(GoogleMap mMap) {
		// Setup of map.
	    if (mMap == null)
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2)).getMap();
	    if (mMap != null) {
	        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        mMap.setTrafficEnabled(true);
	        mMap.setMyLocationEnabled(true);
	        if (IncidentsActivity.getLocation() != null)
	        	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(IncidentsActivity.getLastKnownLocation().getLatitude(), IncidentsActivity.getLastKnownLocation().getLongitude()), 11));
	    }
	}
	
	private void createNewRoute(String value) {
		String start, end;
		start = (startLoc.getText().toString().equalsIgnoreCase("My Location")) ? 
				IncidentsActivity.getLastKnownLocation().getLatitude() + "," + IncidentsActivity.getLastKnownLocation().getLongitude() : 
					startLoc.getText().toString();
		end = (endLoc.getText().toString().equalsIgnoreCase("My Location")) ? 
				IncidentsActivity.getLastKnownLocation().getLatitude() + "," + IncidentsActivity.getLastKnownLocation().getLongitude() : 
					endLoc.getText().toString();
		MainActivity.setStartLoc(start);
		MainActivity.setEndLoc(end);
		// Storing the route into the database.
		Route new_route = new Route(this, value, System.currentTimeMillis(), start, end);
		new_route.serialize();
		MyRoutesActivity.getRoutes().add(new_route);
		MyRoutesActivity.updateRoutes();
	}
	
	public static boolean isServiceCalled() {
		return serviceCalled;
	}

	public static void setServiceCalled(boolean serviceCalled2) {
		serviceCalled = serviceCalled2;
	}

	public static boolean isLoading() {
		return loading;
	}

	public static void setLoading(boolean loading2) {
		loading = loading2;
	}
}

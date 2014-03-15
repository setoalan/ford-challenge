package com.logicdrop.fordchallenge.route;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.logicdrop.fordchallenge.CreateRouteActivity;

public class MyRoutesActivity extends ListActivity {

	private static ArrayList<Route> routes;
	private static MyRoutesAdapter adapter;
	public static Context context;
	private String selectedItem;     
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		routes = Route.getAllRoutes(context);
		Log.d("Routes", "" + routes.size());
		
		adapter = new MyRoutesAdapter(this, routes);
		setListAdapter(adapter);
		
		OnItemLongClickListener itemLongListener = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long rowid) {
				selectedItem = parent.getItemAtPosition(position).toString().replaceAll("[0-9]","");
				AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
				builder.setMessage("Do you want to remove " + selectedItem + "?");
				builder.setCancelable(false);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {     
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Route.db.deleteEntry("Routes", "routeName=" + "\'" + selectedItem + "\'");
						Route.db.deleteEntry("Beans", "routeName=" + "\'" + selectedItem + "\'");
						adapter = null;
						routes = Route.getAllRoutes(context);
						adapter = new MyRoutesAdapter(context, routes);
						setListAdapter(adapter);
						Toast.makeText(getApplicationContext(), selectedItem + " has been removed.", Toast.LENGTH_SHORT).show();
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
	            builder.show();
				return true;
			}
		};
		getListView().setOnItemLongClickListener(itemLongListener);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Route selectedValue = (Route) getListAdapter().getItem(position);
		Intent routeItem = new Intent(this, RouteHistoryActivity.class);
		routeItem.putExtra("route_name", selectedValue.name);
		startActivity(routeItem);
		return;
	}
	
	public static void updateRoutes()
	{
		for (final Route route : routes)
		{
			route.serialize();
		}
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}
	
	public static ArrayList<Route> getRoutes() {
		if (routes == null)
			routes = new ArrayList<Route>();
		return routes;
	}
	
	@Override
	public void onBackPressed() {
		if (CreateRouteActivity.isServiceCalled()) {
		    new AlertDialog.Builder(getParent())
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
}

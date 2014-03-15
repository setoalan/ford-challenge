package com.logicdrop.fordchallenge.route;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.logicdrop.fordchallenge.R;

@SuppressWarnings("deprecation")
public class RouteTabsActivity extends TabActivity {
	
	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_tab);
		
		tabHost = getTabHost();
		
		TabSpec myRouteSpec = tabHost.newTabSpec("My Routes");
		myRouteSpec.setIndicator("My Routes", null);
		myRouteSpec.setContent(new Intent(this, MyRoutesActivity.class));
		
		TabSpec recentRouteSpec = tabHost.newTabSpec("Recent Routes");
		recentRouteSpec.setIndicator("Recent Routes", null);
		recentRouteSpec.setContent(new Intent(this, RecentRoutesActivity.class));
		
		tabHost.addTab(myRouteSpec);
		tabHost.addTab(recentRouteSpec);
	}
}

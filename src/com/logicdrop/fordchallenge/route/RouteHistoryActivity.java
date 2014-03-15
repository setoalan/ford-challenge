 package com.logicdrop.fordchallenge.route;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.logicdrop.fordchallenge.CreateRouteActivity;
import com.logicdrop.fordchallenge.MainActivity;
import com.logicdrop.fordchallenge.OpenXCActivity;
import com.logicdrop.fordchallenge.R;
import com.logicdrop.fordchallenge.api.openxc.OpenXCBean;
import com.logicdrop.fordchallenge.api.services.IterisService;
import com.logicdrop.fordchallenge.api.social.SocialInterface;
import com.logicdrop.fordchallenge.api.social.SocialService;
import com.logicdrop.fordchallenge.incidents.RouteService;
import com.logicdrop.fordchallenge.menu.FacebookActivity;

public class RouteHistoryActivity extends Activity {

    private static ArrayList<OpenXCBean> beanList;
    private ExpandableListView expListView;
    public static RouteHistoryAdapter expListAdapter;
    private static Context context;
    private static String route_name;
    Intent intent;
    private SocialInterface social;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_routes_item);
        context = this;
        social = new SocialService();
        
        intent = getIntent();
        route_name = intent.getStringExtra("route_name");
        beanList = Route.getBeans(this, route_name);
        
        expListView = (ExpandableListView) findViewById(R.id.route_date_list);  
        expListAdapter = new RouteHistoryAdapter(this, beanList);
        expListView.setAdapter(expListAdapter);
    }
 
    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId())
    	{
    		case R.id.openxc:
    			Intent openxc_intent = new Intent(this, OpenXCActivity.class);
    			openxc_intent.putExtra("route_name", intent.getStringExtra("route_name"));
    			startActivity(openxc_intent);
    			break;
    		case R.id.routeRoute:
    			if (!CreateRouteActivity.isServiceCalled()) {
    				new IterisService();
    				CreateRouteActivity.setServiceCalled(true);
    			}
    			MainActivity.routeFromCreate = false;
    			MainActivity.routed = true;
    			for (Route route : Route.getAllRoutes(getBaseContext())) {
    				if (route.name.equals(route_name)) {
    					new RouteService(route.start.replaceAll("\\s",""), route.stop.replaceAll("\\s",""), false);
    					break;
    				}
    			}
				CreateRouteActivity.setLoading(true);
				MainActivity.tabHost.setCurrentTab(0);
				finish();
    			break;
    		case R.id.facebook:
    	        Intent fbIntent = new Intent(this, FacebookActivity.class);
    	        fbIntent.putExtra("Message", "I'm using the Logicdrop Ford Challenge App!");
    	        startActivity(new Intent(fbIntent));
    	        break;
    		case R.id.tweet:
    	        startActivity(social.postToTwitter(this, "I'm using the Logicdrop Ford Challenge App!"));
    	        break;
    		case R.id.email:
    	        startActivity(social.postToEmail("I'm using the Logicdrop Ford Challenge App!"));
    	        break;
    		default:
    	        break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    public static void update()
    {
    		beanList = Route.getBeans(context, route_name);
    		expListAdapter.notifyDataSetChanged();
    }
    
    public static void addBean(OpenXCBean bean) {
		beanList.add(bean);
		expListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.openxc).setVisible(true);
        menu.findItem(R.id.createRoute).setVisible(false);
        menu.findItem(R.id.routeRoute).setVisible(true);
        menu.findItem(R.id.directionListMenu).setVisible(false);
        menu.findItem(R.id.saveRouteMenu).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
}

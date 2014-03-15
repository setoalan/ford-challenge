package com.logicdrop.fordchallenge;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.logicdrop.fordchallenge.api.social.SocialInterface;
import com.logicdrop.fordchallenge.api.social.SocialService;
import com.logicdrop.fordchallenge.incidents.IncidentsActivity;
import com.logicdrop.fordchallenge.incidents.RouteService;
import com.logicdrop.fordchallenge.menu.FacebookActivity;
import com.logicdrop.fordchallenge.menu.WalkthroughActivity;
import com.logicdrop.fordchallenge.openxc.OpenXcEnablerActivity;
import com.logicdrop.fordchallenge.route.MyRoutesActivity;
import com.logicdrop.fordchallenge.route.Route;
import com.logicdrop.fordchallenge.route.RouteTabsActivity;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	
	public static TabHost tabHost;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    public static ActionBarDrawerToggle mDrawerToggle;
    private String[] mMenuTitles;
    
    private SocialInterface social;
    
    public static int count;
    public static Timer mTimer;
    private static TimerTask mTimerTask;
    
    public static boolean routed = false;
    public static boolean routeFromCreate = false;
    private static String startLoc, endLoc;
    
	private AlertDialog.Builder alert;
    private AlertDialog alertd;
    private EditText input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		count = 0;
		
		social = new SocialService();
		
		// Setup of left drawer menu.
        mMenuTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.icon_drawer,
                R.string.drawer_open,
                R.string.drawer_close
                ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // Setup of bottom tabs.
		tabHost = getTabHost();
		
		TabSpec incidentSpec = tabHost.newTabSpec("Incidents");
		incidentSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab1_selector));
		incidentSpec.setContent(new Intent(this, IncidentsActivity.class));
		
		TabSpec routeSpec = tabHost.newTabSpec("Routes");
		routeSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab2_selector));
		routeSpec.setContent(new Intent(this, RouteTabsActivity.class));
		
		TabSpec statsSpec = tabHost.newTabSpec("Statistics");
		statsSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab3_selector));
		statsSpec.setContent(new Intent(this, StatsActivity.class));
		
		TabSpec feedSpec = tabHost.newTabSpec("Feed");
		feedSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab4_selector));
		feedSpec.setContent(new Intent(this, FeedActivity.class));
		
		TabSpec syncSpec = tabHost.newTabSpec("Sync");
		syncSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab5_selector));
		syncSpec.setContent(new Intent(this, SyncActivity.class));
	
		tabHost.addTab(incidentSpec);
		tabHost.addTab(routeSpec);
		tabHost.addTab(statsSpec);
		tabHost.addTab(feedSpec);
		tabHost.addTab(syncSpec);
		
		tabHost.setCurrentTab(0);
		
		// Setup of dialog to save if user has not saved the route yet.
		alert = new AlertDialog.Builder(this);
	    alertd = alert.create();
	   	alert.setTitle("Save Route");
	   	alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	   		public void onClick(DialogInterface dialog, int whichButton) {
	   			Route new_route = new Route(MainActivity.this, input.getText().toString(), System.currentTimeMillis(), startLoc, endLoc);
				new_route.serialize();
				MyRoutesActivity.getRoutes().add(new_route);
				MyRoutesActivity.updateRoutes();
				routeFromCreate = false;
				invalidateOptionsMenu();
				tabHost.setCurrentTab(1);
	   		}
	   	});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				alertd.dismiss();
			}
	  	});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}
	
	// Global timer functions.
	public static void startTimer(){
		mTimer = new Timer();
	  	mTimerTask = new TimerTask() {
	  		@Override 
	  		public void run() { 
	  			do { 
	  				try {
	  					Thread.sleep(1000); 
	  				} catch (InterruptedException e) {
	  					e.printStackTrace();
	  				}    
	  			} while (false);  
	    		count++;   
	  		} 
		}; 
	    if(mTimer != null && mTimerTask != null) 
	    	mTimer.schedule(mTimerTask, 1000, 1000); 
	} 
	
	public static void cancelTimer() {
		if (mTimer != null)
			mTimer.cancel();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.createRoute).setVisible(!drawerOpen);
        menu.findItem(R.id.openxc).setVisible(false);
        menu.findItem(R.id.routeRoute).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        if (routed)
        	menu.findItem(R.id.directionListMenu).setVisible(!drawerOpen);
        else
        	menu.findItem(R.id.directionListMenu).setVisible(false);
        if (routeFromCreate)
        	menu.findItem(R.id.saveRouteMenu).setVisible(!drawerOpen);
        else
        	menu.findItem(R.id.saveRouteMenu).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        switch(item.getItemId()) {
        case R.id.openxc:
        	startActivity(new Intent(this, OpenXCActivity.class));
        	break;
        case R.id.createRoute:
        	startActivity(new Intent(this, CreateRouteActivity.class));
        	break;
        case R.id.directionListMenu:
        	try {
				if (!RouteService.getDirections().getString("status").equals("OK"))
					Toast.makeText(this, "Create a Valid Route First", Toast.LENGTH_SHORT).show();
				else
					startActivity(new Intent(this, DirectionsListActivity.class));
			} catch (JSONException e) {}
        	break;
        case R.id.saveRouteMenu:
        	try {
				if (!RouteService.getDirections().getString("status").equals("OK")) {
					Toast.makeText(this, "Create a Valid Route First", Toast.LENGTH_SHORT).show();
				} else {
					input = new EditText(MainActivity.this);
					input.setLines(1);
					input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
					alert.setView(input);
					alert.show();
				}
			} catch (JSONException e) {}
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

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	mDrawerList.setItemChecked(position, false);
            mDrawerLayout.closeDrawer(mDrawerList);
            switch (position) {
            case 0:
            	startActivity(new Intent(MainActivity.this, WalkthroughActivity.class));
            	return;
            case 1:
            	startActivity(new Intent(MainActivity.this, OpenXcEnablerActivity.class));
            	return;
            }
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	@Override
	public void onBackPressed() {
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
    
    public static void setTab(int tab) {
    	tabHost.setCurrentTab(tab);
    }
    
	public static String getStartLoc() {
		return startLoc;
	}

	public static void setStartLoc(String startLoc2) {
		startLoc = startLoc2;
	}

	public static String getEndLoc() {
		return endLoc;
	}

	public static void setEndLoc(String endLoc2) {
		endLoc = endLoc2;
	}
}
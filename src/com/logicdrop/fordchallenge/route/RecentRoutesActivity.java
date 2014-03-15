package com.logicdrop.fordchallenge.route;
 
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.logicdrop.fordchallenge.CreateRouteActivity;
import com.logicdrop.fordchallenge.R;
import com.logicdrop.fordchallenge.api.openxc.OpenXCBean;
 
public class RecentRoutesActivity extends Activity {
 
    private ArrayList<Route> routeCollection;
    private ExpandableListView expListView;
    RecentRoutesAdapter expListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_routes);
        routeCollection = Route.getRecentRoutes(this);
      	expListView = null;
        expListView = (ExpandableListView) findViewById(R.id.recent_route_list);
        expListAdapter = new RecentRoutesAdapter(
                this, routeCollection);
        expListView.setAdapter(expListAdapter);
        expListView.setOnChildClickListener(new OnChildClickListener() {
 
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                final OpenXCBean selected = (OpenXCBean) expListAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected.getRouteName(), Toast.LENGTH_LONG)
                        .show();
 
                return true;
            }
        });
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		routeCollection = Route.getRecentRoutes(this);
		expListView = null;
	   	expListView = (ExpandableListView) findViewById(R.id.recent_route_list);
	   	expListAdapter = new RecentRoutesAdapter(this, routeCollection);
	 	expListView.setAdapter(expListAdapter);
	 	expListView.setOnChildClickListener(new OnChildClickListener() {
	
	 	public boolean onChildClick(ExpandableListView parent, View v,
	 			int groupPosition, int childPosition, long id) {
	 		final OpenXCBean selected = (OpenXCBean) expListAdapter.getChild(groupPosition, childPosition);
	 		Toast.makeText(getBaseContext(), selected.getRouteName(), Toast.LENGTH_LONG).show();
	 		return true;
	 		}
	 	});
	}
	
    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
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
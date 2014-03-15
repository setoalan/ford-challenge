package com.logicdrop.fordchallenge;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.logicdrop.fordchallenge.api.openxc.OpenXCBean;


public class StatsActivity extends Activity {

	//OpenOpenXCBean();
	private Store openXCdb = null;
	private TextView avgMPGView, avgMPHView, routeMileageView, totalMileageView, totalFuelUsedView;
	private double avgMPG = 0, avgMPH = 0, routeMileage = 0, totalMileage = 0, totalFuelUsed = 0;
	ArrayList<ContentValues> allValues = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);
		if( OpenXCBean.db != null) {
			openXCdb = OpenXCBean.db;
			allValues = openXCdb.allEntries();
			totals();
			setViews();
			}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		avgMPG = avgMPH = routeMileage = totalMileage = totalFuelUsed = 0;
		if( OpenXCBean.db != null) {
			openXCdb = OpenXCBean.db;
			allValues = openXCdb.allEntries();
			totals();
			setViews();
			}
	}
	public void setViews() {
		avgMPGView = (TextView) findViewById(R.id.avg_mpg_val2);
		avgMPGView.setText("" + avgMPG);	    
		avgMPHView = (TextView) findViewById(R.id.avg_mph_val2);
		avgMPHView.setText("" + avgMPH);
		routeMileageView = (TextView) findViewById(R.id.route_mileage_val2);
		routeMileageView.setText("" + routeMileage);
		totalMileageView = (TextView) findViewById(R.id.total_mileage_val2);
		totalMileageView.setText("" + totalMileage);
		totalFuelUsedView = (TextView) findViewById(R.id.total_fuel_used_val2);
		totalFuelUsedView.setText("" + totalFuelUsed);
	}
	
	public void totals() {
		for(int i = 0; i < allValues.size(); i++ )
		{
			avgMPG += allValues.get(i).getAsDouble("avgMPG");
			avgMPH += allValues.get(i).getAsDouble("avgMPH");
			routeMileage += allValues.get(i).getAsDouble("routeMileage");
			totalMileage += allValues.get(i).getAsDouble("totalMileage");
			totalFuelUsed += allValues.get(i).getAsDouble("totalFuelUsed");
		}
		avgMPG /= allValues.size();
		avgMPH /= allValues.size();
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
}

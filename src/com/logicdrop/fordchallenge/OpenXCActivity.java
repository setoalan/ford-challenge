package com.logicdrop.fordchallenge;

import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.logicdrop.fordchallenge.api.openxc.OpenXCBean;
import com.logicdrop.fordchallenge.route.MyRoutesActivity;
import com.logicdrop.fordchallenge.route.RouteHistoryActivity;
import com.openxc.VehicleManager;
import com.openxc.measurements.FuelConsumed;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.Odometer;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.VehicleSpeed;
import com.openxc.remote.VehicleServiceException;


public class OpenXCActivity extends FragmentActivity
{
	private VehicleManager mVehicleManager;
	private TextView avgMPGView, avgMPHView, routeMileageView, totalMileageView, totalFuelUsedView, timeView;
	private Button startTime, stopTime;
	private Timer timer;
	private double avgMPG, avgMPH, routeMileage, totalMileage, totalFuelUsed;
	private long date = System.currentTimeMillis();
	private static boolean timerActive;
	ContentValues map;
	Intent route_intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openxc);
	
		route_intent = getIntent();
	    Intent intent = new Intent(this, VehicleManager.class);
	    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	    
	    avgMPGView = (TextView) findViewById(R.id.avgMPG);
	    avgMPHView = (TextView) findViewById(R.id.avgMPH);
	    // displays mph right now
	    routeMileageView = (TextView) findViewById(R.id.rrTotalMiles);
	    totalMileageView = (TextView) findViewById(R.id.totalMileage);
	    totalFuelUsedView = (TextView) findViewById(R.id.totalFuelUsed);
	    timeView = (TextView) findViewById(R.id.time);
	    startTime = (Button) findViewById(R.id.startTime);
	    stopTime = (Button) findViewById(R.id.stopTime);
	    
	    timer = new Timer();
	    startTime(timer, timeView);	   
	    
	    startTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!timerActive) {
					Toast.makeText(OpenXCActivity.this, "Recording started.", Toast.LENGTH_SHORT).show();
					MainActivity.startTimer();
					timerActive = true;
				} else {
					Toast.makeText(OpenXCActivity.this, "Time is already active.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	    
	    stopTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (timerActive) {
					MainActivity.cancelTimer();
					timeView.setText("0");
					Toast.makeText(OpenXCActivity.this, "Data Recorded", Toast.LENGTH_LONG).show();
					map = null;
					map = getContentValues();
					RouteHistoryActivity.addBean(new OpenXCBean(OpenXCActivity.this, map, true));
					//RouteHistoryActivity.update();
					MyRoutesActivity.updateRoutes();
					MainActivity.count = 0;
					timerActive = false;
					finish();
				} else {
					Toast.makeText(OpenXCActivity.this, "Timer is not active.", Toast.LENGTH_SHORT).show();
				}
			}
		}); 
	}
	
	public ContentValues getContentValues()
	{
		ContentValues temp = new ContentValues();
		
		temp.put("avgMPG", avgMPG);
		temp.put("avgMPH", avgMPH);
		temp.put("routeMileage", routeMileage);
		temp.put("totalMileage", totalMileage);
		temp.put("totalFuelUsed", totalFuelUsed);
		temp.put("elapsedTime", MainActivity.count);
		temp.put("routeName", route_intent.getStringExtra("route_name"));
		temp.put("date", date);
		
		return temp;
	}
	
	public void startTime(Timer timer, final TextView time) {
		timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	            runOnUiThread(new Runnable()
	            {
	                @Override
	                public void run()
	                {
	                    time.setText("" + MainActivity.count);               
	                }
	            });
	        }
	    }, 1000, 1000);
	}
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.createRoute).setVisible(false);
        menu.findItem(R.id.openxc).setVisible(false);
        menu.findItem(R.id.routeRoute).setVisible(false);
        menu.findItem(R.id.facebook).setVisible(false);
        menu.findItem(R.id.tweet).setVisible(false);
        menu.findItem(R.id.email).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.directionListMenu).setVisible(false);
        menu.findItem(R.id.saveRouteMenu).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    Log.i("openxc", "Unbinding from vehicle service");
	    unbindService(mConnection);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    // Called when the connection with the service is established
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        Log.i("openxc", "Bound to VehicleManager");
	        mVehicleManager = ((VehicleManager.VehicleBinder)service).getService();
	        try {
				mVehicleManager.addListener(FuelConsumed.class, mConsumedListener);
				mVehicleManager.addListener(Odometer.class, mOdometer);
				mVehicleManager.addListener(VehicleSpeed.class, mSpeedListener);
			} catch (VehicleServiceException e) {
				e.printStackTrace();
			} catch (UnrecognizedMeasurementTypeException e) {
				e.printStackTrace();
			}
	    }

	    // Called when the connection with the service disconnects unexpectedly
	    public void onServiceDisconnected(ComponentName className) {
	        Log.w("openxc", "VehicleService disconnected unexpectedly");
	        mVehicleManager = null;
	    }
	};
	
	FuelConsumed.Listener mConsumedListener = new FuelConsumed.Listener() {
		@Override
		public void receive(Measurement measurement) {
		    final FuelConsumed fuel = (FuelConsumed) measurement;
		    OpenXCActivity.this.runOnUiThread(new Runnable() {
		        public void run() {
		        	totalFuelUsedView.setText(String.format("%.5f%n", fuel.getValue().doubleValue()*0.264172));
		        	totalFuelUsed = fuel.getValue().doubleValue()*0.264172;
		        }
		    });
		}
	};
	
	Odometer.Listener mOdometer = new Odometer.Listener() {
		@Override
		public void receive(Measurement measurement) {
			final Odometer distance = (Odometer) measurement;
			OpenXCActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					avgMPGView.setText(String.format("%.5f%n", ((distance.getValue().doubleValue()*0.621371)/Double.parseDouble(totalFuelUsedView.getText().toString()))));
					avgMPG = (distance.getValue().doubleValue()*0.621371)/Double.parseDouble(totalFuelUsedView.getText().toString());
					avgMPHView.setText(String.format("%.5f%n", (distance.getValue().doubleValue()*0.621371*3600)/Integer.parseInt(timeView.getText().toString())));
					avgMPH = (distance.getValue().doubleValue()*0.621371*3600)/Integer.parseInt(timeView.getText().toString());
					totalMileageView.setText(String.format("%.5f%n", distance.getValue().doubleValue()*0.621371));
					totalMileage = distance.getValue().doubleValue()*0.621371;
				}
			});
		}
	};
	
    VehicleSpeed.Listener mSpeedListener = new VehicleSpeed.Listener() {
        public void receive(Measurement measurement) {
            final VehicleSpeed speed = (VehicleSpeed) measurement;
            OpenXCActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                	// SHOULD BE ROUTE MILEAGE, NOT MPH
                    routeMileageView.setText(String.format("%.5f%n", speed.getValue().doubleValue()));
                    routeMileage = speed.getValue().doubleValue();
                }
            });
        }
    };
}
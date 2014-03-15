package com.logicdrop.fordchallenge;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.logicdrop.fordchallenge.incidents.RouteService;

public class DirectionsListActivity extends Activity {
	
	private ListView directionList;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> list;
	private JSONArray directions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.direction_list);
		directionList = (ListView) findViewById(R.id.directionList);
		list = new ArrayList<String>();
		try {
			directions = RouteService.getSteps();
			for (int i = 0; i < 10; i++)
				list.add(i + "  " + directions.getJSONObject(i).getString("html_instructions").replaceAll("<(.*?)\\>","") + "\n\t\t"
					+ directions.getJSONObject(i).getJSONObject("duration").getString("text") + "\n\t\t"
					+ directions.getJSONObject(i).getJSONObject("distance").getString("text"));
			for (int i = 10; i < directions.length(); i++) {
				list.add(i + " " + directions.getJSONObject(i).getString("html_instructions").replaceAll("<(.*?)\\>","") + "\n\t\t"
					+ directions.getJSONObject(i).getJSONObject("duration").getString("text") + "\n\t\t"
					+ directions.getJSONObject(i).getJSONObject("distance").getString("text"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		directionList.setAdapter(adapter);
	}
}

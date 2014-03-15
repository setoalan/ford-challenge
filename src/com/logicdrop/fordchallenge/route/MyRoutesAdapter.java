package com.logicdrop.fordchallenge.route;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.logicdrop.fordchallenge.R;

public class MyRoutesAdapter extends ArrayAdapter<Route>  {

	private final Context context;
	private ArrayList<Route> routes;
	public MyRoutesAdapter(Context context, ArrayList<Route> routes) {
		super(context, R.layout.my_routes, routes);
		this.context = context;
		this.routes = routes;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.my_routes, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setTypeface(null, Typeface.BOLD);
		textView.setText(routes.get(position).name + " | " + routes.get(position).start + " - " + routes.get(position).stop);
		return rowView;
	}
}
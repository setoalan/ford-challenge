package com.logicdrop.fordchallenge.route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.logicdrop.fordchallenge.R;
import com.logicdrop.fordchallenge.api.openxc.OpenXCBean;
 
public class RouteHistoryAdapter extends BaseExpandableListAdapter {
 
    private Activity context;
    private ArrayList<OpenXCBean> beanCollections;
    SimpleDateFormat df;
 
    public RouteHistoryAdapter(Activity context,
           ArrayList<OpenXCBean> beanCollections) {
        this.context = context;
        this.beanCollections = beanCollections;
        df  = new SimpleDateFormat("MMMM dd, yyyy hh:mma", Locale.US);
    }
 
    public Object getChild(int groupPosition, int childPosition) {
        return beanCollections.get(groupPosition);
    }
 
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent)
    {
        final OpenXCBean data = (OpenXCBean) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();
 
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.my_routes_item_child, null);
        }
 
        TextView item2 = (TextView) convertView.findViewById(R.id.avg_mpg_val);
        TextView item3 = (TextView) convertView.findViewById(R.id.avg_mph_val);
        TextView item4 = (TextView) convertView.findViewById(R.id.route_mileage_val);
        TextView item5 = (TextView) convertView.findViewById(R.id.total_mileage_val);
        TextView item6 = (TextView) convertView.findViewById(R.id.total_fuel_used_val);
        
        item2.setText(String.format("%1$, .2f", data.getAvgMPG()));
        item3.setText("" + data.getAvgMPH());
        item4.setText(String.format("%1$, .2f", data.getRouteMileage()));
        item5.setText(String.format("%1$, .2f", data.getTotalMileage()) + " miles");
        item6.setText(String.format("%1$, .2f", data.getTotalFuelUsed()) + " gallons");
        
        return convertView;
    }
 
    public int getChildrenCount(int groupPosition) {
    	return 1;
    }
 
    public Object getGroup(int groupPosition) {
        return beanCollections;
    }
    
 
    public int getGroupCount() {
        return beanCollections.size();
    }
 
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	String date = df.format(((OpenXCBean)getChild(groupPosition, 0)).getDate());
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_routes_item_parent,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.dailyRouteItem);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(date);
        
        return convertView;
    }
 
    public boolean hasStableIds() {
        return true;
    }
 
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
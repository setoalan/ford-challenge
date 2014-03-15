package com.logicdrop.fordchallenge.route;
 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
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
 
public class RecentRoutesAdapter extends BaseExpandableListAdapter {

	private final Activity context;
	private ArrayList<Route> routes;
	SimpleDateFormat df;

	public RecentRoutesAdapter(Activity context, ArrayList<Route> routes) {
		super();
		this.context = context;
		this.routes = routes;
		df  = new SimpleDateFormat("MM/dd/yy", Locale.US);
	}
 
    public Object getChild(int groupPosition, int childPosition) {
    		final ArrayList<OpenXCBean> beans = routes.get(groupPosition).getBeans();
    		if (beans.size() > childPosition) {
    			return beans.get(childPosition);
    		}
    		else {
    			return new OpenXCBean();
    		}
    }
 
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

	@SuppressLint("SimpleDateFormat")
	public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
		final OpenXCBean bean = (OpenXCBean) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();
 
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recent_routes_child, null);
        }
 
        TextView item = (TextView) convertView.findViewById(R.id.rrTotalMilesVal);
        TextView item2 = (TextView) convertView.findViewById(R.id.rrAvgMPGVal);
        TextView item3 = (TextView) convertView.findViewById(R.id.rrFuelUsedVal);
        TextView item4 = (TextView) convertView.findViewById(R.id.rrElapsedTimeVal);
        TextView item5 = (TextView) convertView.findViewById(R.id.rrAvgMPHVal);
        TextView item6 = (TextView) convertView.findViewById(R.id.rrDateRecordedVal);
        TextView item7 = (TextView) convertView.findViewById(R.id.rrTime);
        
        item.setText("" + bean.getTotalMileage());
        item2.setText("" + bean.getAvgMPG());
        item3.setText("" + bean.getTotalFuelUsed());
        item4.setText("" + bean.getElapsedTime() + "s");
        item5.setText("" + bean.getAvgMPH());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        item6.setText("" + sdf.format(bean.getDate()));
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mma");
        item7.setText("" + sdf2.format(bean.getDate()));
       
        return convertView;
    }
 
    public int getChildrenCount(int groupPosition) {
        return routes.get(groupPosition).getBeans().size();
    }
 
    public Object getGroup(int groupPosition) {
        return routes.get(groupPosition);
    }
 
    public int getGroupCount() {
        return routes.size();
    }
 
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String routeName = ((Route)getGroup(groupPosition)).name;
        //String date = df.format(((OpenXCBean)getChild(groupPosition, 0)).getDate());
        if (convertView == null) {
            LayoutInflater inflateInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflateInflater.inflate(R.layout.recent_routes_parent,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.recentRouteItem);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(routeName + " | " + ((Route)getGroup(groupPosition)).start + " - " + ((Route)getGroup(groupPosition)).stop);
        return convertView;
    }
 
    public boolean hasStableIds() {
        return true;
    }
 
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
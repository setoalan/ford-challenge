package com.logicdrop.fordchallenge.route;

import java.sql.Date;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.logicdrop.fordchallenge.Store;
import com.logicdrop.fordchallenge.api.openxc.OpenXCBean;

public class Route
{
	public long dateCreated;
	public String name;
	public String start;
	public String stop;
	public static Store db;
	private final static String TABLE_NAME = "Routes";
	private long id = -1;
	private static long recent = System.currentTimeMillis() - 432000000;
	ArrayList<OpenXCBean> runs;
	private long currentRun;
	
	public Route(Context context, final String name, final long date, final String start, final String stop)
	{
		setDatabase(context);
		this.dateCreated = date;	
		this.name = name;
		this.start = start;
		this.stop = stop;
		this.runs = Route.getBeans(context, this.name);
		this.deserialize(context);
	}
	
	private Route(Context context, ContentValues values)
	{
		setDatabase(context);
		this.name = values.getAsString("routeName");
		this.start = values.getAsString("start");
		this.stop = values.getAsString("stop");
		this.dateCreated = values.getAsLong("dateCreated");
		this.runs = Route.getBeans(context, this.name);
	}
	
	public ContentValues getContentValues()
	{
		ContentValues temp = new ContentValues();
		temp.put("routeName", this.name);
		temp.put("dateCreated", this.dateCreated);
		temp.put("start", this.start);
		temp.put("stop", this.stop);
		if (this.hasID()) {
			temp.put("id", this.id);
		}
		return temp;
	}
	
	public ArrayList<ContentValues> getBeanValues()
	{
		ArrayList<ContentValues> ret = new ArrayList<ContentValues>();
		for (final OpenXCBean bean: runs)
			ret.add(bean.getContentValues());
		
		return ret;
	}
	
	public ArrayList<Date> getDates()
	{
		ArrayList<Date> ret = new ArrayList<Date>();
		
		for (final OpenXCBean bean : runs) {
			ret.add(new Date(bean.getDate()));
		}
		return ret;
	}

	public void serialize()
	{
		this. id = Route.db.update(TABLE_NAME, this.getContentValues());
	}
	
	private void deserialize(Context context)
	{
		Cursor c = db.select(Route.TABLE_NAME, this.name);
		ContentValues map;
		if (c.moveToFirst()) {
			map = new ContentValues();
			DatabaseUtils.cursorRowToContentValues(c, map);
			this.id = map.getAsLong("id");
			this.name = map.getAsString("routeName");
			this.dateCreated = map.getAsLong("dateCreated");
			this.start = map.getAsString("start");
			this.stop = map.getAsString("stop");
			this.runs = Route.getBeans(context, this.name);
		}
		c.close();
	}
	
	public ArrayList<OpenXCBean> getBeans()
	{
		return this.runs;
	}
	
	public static ArrayList<OpenXCBean> getBeans(Context context, String routeName)
	{
		ArrayList<OpenXCBean> temp = new ArrayList<OpenXCBean>();
		if (!OpenXCBean.databaseSet()) {
			OpenXCBean.setDatabase(context);
		}
		Cursor c = db.select("Beans", routeName);;
		ContentValues map;
		if(c.moveToFirst())
		{
			do {
				map = new ContentValues();
				DatabaseUtils.cursorRowToContentValues(c, map);
				temp.add(new OpenXCBean(context, map, false));
			} while(c.moveToNext());			
		}
		c.close();
		return temp;
	}
	
	public static ArrayList<Route> getAllRoutes(Context context)
	{
		setDatabase(context);
		Cursor c = Route.db.select(Route.TABLE_NAME);
		ContentValues temp = new ContentValues();
		ArrayList<Route> ret = new ArrayList<Route>();
		while (c.moveToNext())
		{
			DatabaseUtils.cursorRowToContentValues(c, temp);
			ret.add(new Route(context, temp));
		}
		c.close();
		return ret;
	}
	
	public static ArrayList<Route> getRecentRoutes(Context context)
	{
		setDatabase(context);
		Cursor c = Route.db.query("SELECT * FROM Routes " +
		"WHERE dateCreated > "  + Route.recent, null);
		ContentValues temp = new ContentValues();
		ArrayList<Route> ret = new ArrayList<Route>();
		while (c.moveToNext())
		{
			DatabaseUtils.cursorRowToContentValues(c, temp);
			ret.add(new Route(context, temp));
		}
		c.close();
		return ret;
	}
	
	private static void setDatabase(Context context)
	{
		if (db == null) {
			db = new Store(context, "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id integer primary key autoincrement, routeName text not null unique, " +
					"start text, " +
					"stop text, " +
					" dateCreated integer);");
		}
	}
	
	private boolean hasID()
	{
		return this.id != -1; 
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDate(long date)
	{
		this.dateCreated = date;
	}
	
	@Override
	public String toString()
	{
		return this.name + String.valueOf(this.dateCreated);
	}
	
	public void startRun()
	{
		currentRun = System.currentTimeMillis();
	}
	
	public void stopRun(OpenXCBean stats)
	{
		stats.setElapsedTime(System.currentTimeMillis() - currentRun);
		stats.setRouteName(this.name);
		runs.add(stats);
	}
}

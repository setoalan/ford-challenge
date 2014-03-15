package com.logicdrop.fordchallenge.api.openxc;

import android.content.ContentValues;
import android.content.Context;

import com.logicdrop.fordchallenge.Store;

public class OpenXCBean
{
	private double avgMPG;
	private int avgMPH;
	private double routeMileage;
	private double totalMileage;
	private double totalFuelUsed;
	private double elapsedTime;
	private String routeName;
	private long date;
	private long id = -1;
	public static Store db = null;
	private final static String TABLE_NAME = "Beans";	
	
	public OpenXCBean(Context context)
	{	// For list test purposes
		setDatabase(context);
		this.avgMPG = 24.6;
		this.avgMPH = 32;
		this.routeMileage = 29.4;
		this.totalMileage = 411.6;
		this.totalFuelUsed = 16.73;
		this.elapsedTime = 20.73;
		this.date = System.currentTimeMillis();
	}
	
	public OpenXCBean(Context context, String routeName)
	{	// For list test purposes
		setDatabase(context);
		this.routeName = routeName;
		this.avgMPG = 24.6;
		this.avgMPH = 32;
		this.routeMileage = 29.4;
		this.totalMileage = 411.6;
		this.totalFuelUsed = 16.73;
		this.elapsedTime = 20.73;
		this.date = System.currentTimeMillis();
		this.serialize();
	}
	
	public OpenXCBean()
	{	// For list test purposes

		this.avgMPG = 0;
		this.avgMPH = 0;
		this.routeMileage = 0;
		this.totalMileage = 0;
		this.totalFuelUsed = 0;
		this.elapsedTime = 0;
		this.date = System.currentTimeMillis();
	}
	
	public OpenXCBean(Context context, String routeName, double avgmpg, int avgmph, double rmileage, double tmileage, double tfuelused, double elapsedtime)
	{
		setDatabase(context);
		this.routeName = routeName;
		this.avgMPG = avgmpg;
		this.avgMPH = avgmph;
		this.routeMileage = rmileage;
		this.totalMileage = tmileage;
		this.totalFuelUsed = tfuelused;
		this.elapsedTime = elapsedtime;
		this.date = System.currentTimeMillis();
		this.serialize();
	}
	
	public OpenXCBean(Context context, ContentValues map, boolean isNew)
	{
		setDatabase(context);
		this.routeName = map.getAsString("routeName");
		this.avgMPG = map.getAsDouble("avgMPG");
		this.avgMPH = map.getAsInteger("avgMPH");
		this.routeMileage = map.getAsDouble("routeMileage");
		this.totalMileage = map.getAsDouble("totalMileage");
		this.totalFuelUsed = map.getAsDouble("totalFuelUsed");
		this.elapsedTime = map.getAsDouble("elapsedTime");
		if(isNew)
		{
			map.put("date", System.currentTimeMillis());
			this.date = map.getAsLong("date");
			this.serialize();
		}
		else
		{
			if(map.getAsLong("date") != null)
					this.date = map.getAsLong("date");
		}
	}
	
	public ContentValues getContentValues()
	{
		ContentValues temp = new ContentValues();
		
		temp.put("avgMPG", avgMPG);
		temp.put("avgMPH", avgMPH);
		temp.put("routeMileage", routeMileage);
		temp.put("totalMileage", totalMileage);
		temp.put("totalFuelUsed", totalFuelUsed);
		temp.put("elapsedTime", elapsedTime);
		temp.put("routeName", routeName);
		temp.put("date", date);
		if (this.hasID())
		{
			temp.put("id", this.id);
		}
		
		return temp;
	}

	public void serialize()
	{
		this. id = db.update(TABLE_NAME, this.getContentValues());
	}
	
	public static boolean databaseSet()
	{
		return !(db == null);
	}
	
	public static void setDatabase(Context context)
	{
		if (db == null)
		{
			
			db = new Store(context, "CREATE TABLE IF NOT EXISTS " + OpenXCBean.TABLE_NAME + " (id integer primary key autoincrement, routeName text not null, " +
					"avgMPG real, " +
					"avgMPH integer, " +
					"routeMileage real, " +
					"totalMileage real, " +
					"totalFuelUsed real, " +
					"elapsedTime real, " +
					" date integer);");
		}
	}
	
	public boolean hasID()
	{
		return this.id != -1;
	}

	public double getAvgMPG() {
		return this.avgMPG;
	}

	public void setAvgMPG(double avgMPG) {
		this.avgMPG = avgMPG;
	}

	public int getAvgMPH() {
		return this.avgMPH;
	}

	public void setAvgMPH(int avgMPH) {
		this.avgMPH = avgMPH;
	}

	public double getRouteMileage() {
		return this.routeMileage;
	}

	public void setRouteMileage(double routeMileage) {
		this.routeMileage = routeMileage;
	}

	public double getTotalMileage() {
		return this.totalMileage;
	}

	public void setTotalMileage(double totalMileage) {
		this.totalMileage = totalMileage;
	}

	public double getTotalFuelUsed() {
		return this.totalFuelUsed;
	}

	public void setTotalFuelUsed(double totalFuelUsed) {
		this.totalFuelUsed = totalFuelUsed;
	}

	public double getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	
	public String getRouteName()
	{
		return routeName;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		this.date = date;
	}
}

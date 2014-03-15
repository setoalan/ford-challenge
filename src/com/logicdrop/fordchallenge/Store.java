package com.logicdrop.fordchallenge;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressWarnings("static-access")
public class Store
{
	private String DB_NAME = "Traffic_DB";
	private static final int DB_VERSION = 1;
	private String DB_CREATE_QUERY;
	private final String where = "id=";
	
	private static SQLiteDatabase store;
	private final SQLiteOpenHelper helper;
	
	public Store(final Context context, final String create_query)
	{
		this.DB_CREATE_QUERY = create_query;
		
		this.helper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
		{
			@Override
			public void onCreate(final SQLiteDatabase db)
			{
				db.execSQL(DB_CREATE_QUERY);
			}
			
			@Override
			public void onOpen(final SQLiteDatabase db)
			{
				db.execSQL(DB_CREATE_QUERY);
			}
			
			@Override
			public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int NewVersion)
			{
				db.execSQL("DROP TABLE IF EXISTS ");
				this.onCreate(db);
			}
		};
		
		this.store = helper.getWritableDatabase();
	}
	
	public long addEntry(final String TABLE_NAME, ContentValues data)
	{
		return this.store.insert(TABLE_NAME, null, data);
	}
	
	public void deleteEntry(final String TABLE_NAME, final String params)
	{
		this.store.delete(TABLE_NAME, params, null);
	}
	
	public long update(final String TABLE_NAME, ContentValues data)
	{
		int updated = this.store.update(TABLE_NAME, data, where + data.getAsLong("id"), null);
		if (updated > 0) {
			return data.getAsLong("id");
		}
		else {
			return this.addEntry(TABLE_NAME, data);
		}
	}
	
	public Cursor select(final String TABLE_NAME, final String routeName)
	{
		return this.store.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE routeName ='" + routeName + "'", null);
	}
	
	public Cursor select(final String TABLE_NAME)
	{
		return this.store.rawQuery("SELECT * FROM " + TABLE_NAME, null);
	}
	public String getFromCursor(Cursor select)
	{
		String data = null;
		if(select.moveToFirst()){
			do{
				data = select.getString(select.getColumnIndex("routeName"));
			}while(select.moveToNext());
		}
		return data;
	}
	
		
	public ArrayList<ContentValues> allEntries()
	{
		ArrayList<ContentValues> values = new ArrayList<ContentValues>();
		Cursor c = select("Beans");
		ContentValues temp;
		if(c.moveToFirst())
		{
			do  {
				temp = new ContentValues();
				DatabaseUtils.cursorRowToContentValues(c, temp);
				values.add(temp);
			} while(c.moveToNext());
		}		
		c.close();
		return values;
	}
	
	public ArrayList<ContentValues> specificEntries(String route)
	{
		ArrayList<ContentValues> values = new ArrayList<ContentValues>();
		Cursor c = select("Beans",  route );
		ContentValues temp;
		if(c.moveToFirst())
		{
			do  {
				temp = new ContentValues();
				DatabaseUtils.cursorRowToContentValues(c, temp);
				values.add(temp);
			} while(c.moveToNext());
		}		
		c.close();
		return values;
	}
	
	public Cursor query(String query, String[] args)
	{
		return this.store.rawQuery(query, args);
	}
}
package com.snapchat;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProjectDatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;	
	private static final String DATABASE_NAME = "SnapChat"; 
	private static final String TABLE_NAME = "ReadMessages";
	
	private static final String KEY_ID = "id";
	private static final String KEY_MESSAGEID = "Message_id";
	
	public ProjectDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String TABLE = "Create TABLE "+TABLE_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_MESSAGEID+" TEXT"+")";
		db.execSQL(TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void addMessage(String id){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_MESSAGEID, id);
		db.insert(TABLE_NAME, null, values);
		db.close();
	}
	
	public ArrayList<String> getallids(){
		ArrayList<String> list = new ArrayList<String>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_NAME;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	list.add(cursor.getString(1));
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    cursor.close();
	    db.close();
	    return list;	
		
	}
	
}

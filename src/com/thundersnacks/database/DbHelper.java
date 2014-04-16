package com.thundersnacks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper  {
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "virtualpantry.db";
    
    // SQL Statements
    private static final String SQL_CREATE_TABLE_USER =
    	    "CREATE TABLE " + DbSchema.UserTable.TABLE + " (" +
    	    DbSchema.UserTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    DbSchema.UserTable.COLUMN_USERNAME + " TEXT NOT NULL, " +
    	    DbSchema.UserTable.COLUMN_EMAIL + " TEXT NOT NULL, " +
    	    DbSchema.UserTable.COLUMN_PASSWORD + " TEXT NOT NULL);";
    
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
    @Override
	public void onCreate(SQLiteDatabase db) {
    	db.execSQL(SQL_CREATE_TABLE_USER);
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
    
    
	
}

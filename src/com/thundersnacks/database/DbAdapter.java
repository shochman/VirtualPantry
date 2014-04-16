package com.thundersnacks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

	public static final String KEY_UID = "uid";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_PID = "pid";
	
	private static final String TAG = "DBAdapter";
	
	private static final String DATABASE_NAME = "MyDB";
	private static final String USERS_TABLE = "users";
	private static final String PANTRY_TABLE = "pantry";
	private static final String U_P_ASSOCIATION_TABLE = "assoc";
	private static final int DATABASE_VERSION = 1;
	
	private static final String USERS_CREATE = 
			"create table users (uid integer primary key autoincrement, " + 
			"username text not null, email text not null, password text not null);";
	private static final String PANTRY_CREATE = 
			"create table pantry (pid integer primary key autoincrement, " +
			"pname text not null, slid integer);";	
	private static final String U_P_ASSOCIATION_CREATE = 
			"create table assoc (uid integer, pid integer, " + 
			"boolean isOwner, text permissions);";

	
	private final Context context;
	
	private static DatabaseHelper DBHelper;
	private static SQLiteDatabase db;
	
	public DbAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		private DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try {
				db.execSQL(USERS_CREATE);
				db.execSQL(PANTRY_CREATE);
				db.execSQL(U_P_ASSOCIATION_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS users");
			db.execSQL("DROP TABLE IF EXISTS pantry");
			db.execSQL("DROP TABLE IF EXISTS assoc");
			onCreate(db);
		}
		
		//---open the database---
		public DatabaseHelper open() throws SQLException
		{
			db = DBHelper.getWritableDatabase();
			return this;
		}
		
		//---close the database---
		public void close()
		{
			DBHelper.close();
		}
		
		//---insert a user into the database---
		public long insertUser(String username, String password, String email)
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_USERNAME, username);
			initialValues.put(KEY_PASSWORD, password);
			initialValues.put(KEY_EMAIL, email);
			return db.insert(USERS_TABLE, null, initialValues);
		}
		
		//---delete a particular user---
		public boolean deleteUser(long rowId) 
		{
			return db.delete(USERS_TABLE, KEY_UID + "=" + rowId, null) > 0;
		}
		
		//---retrieves all the user---
		public Cursor getAllUsers()
		{
			return db.query(USERS_TABLE, new String[] {KEY_UID, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL}, null, null, null, null, null);
		}
		
		//---retrieves a particular user---
		public Cursor getUser(long rowId) throws SQLException
		{
			Cursor mCursor = 
					db.query(true, USERS_TABLE, new String[] {KEY_UID,
							KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL}, KEY_UID + "=" + rowId, null,
							null, null, null, null);
			if(mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}
		
		public boolean updateUser(long rowId, String username, String email, String password) 
		{
			ContentValues args = new ContentValues();
			args.put(KEY_USERNAME, username);
			args.put(KEY_EMAIL, email);
			args.put(KEY_PASSWORD, password);
			return db.update(USERS_TABLE, args, KEY_UID + "=" + rowId, null) > 0;
			
		}
	}
}

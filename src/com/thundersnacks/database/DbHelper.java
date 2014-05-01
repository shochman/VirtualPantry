package com.thundersnacks.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "virtualpantry.db";
    
    // SQL Statements
    private static final String SQL_CREATE_TABLE_USER =
    	    "CREATE TABLE IF NOT EXISTS " + DbSchema.UserTable.TABLE + " (" +
    	    DbSchema.UserTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    DbSchema.UserTable.COLUMN_USERNAME + " TEXT NOT NULL, " +
    	    DbSchema.UserTable.COLUMN_EMAIL + " TEXT NOT NULL, " +
    	    DbSchema.UserTable.COLUMN_PASSWORD + " TEXT NOT NULL);";
    
    private static final String SQL_CREATE_TABLE_PANTRY =
    	    "CREATE TABLE IF NOT EXISTS " + DbSchema.PantryTable.TABLE + " (" +
    	    DbSchema.PantryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    DbSchema.PantryTable.COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
    	    "FOREIGN KEY(" + DbSchema.PantryTable.COLUMN_ASSOCIATED_USER + ") REFERENCES " 
    	    + DbSchema.UserTable.TABLE +"(" + DbSchema.UserTable._ID + "), " + 
    	    DbSchema.PantryTable.COLUMN_VISIBLE + " BOOLEAN NOT NULL);";
    
    private static final String SQL_CREATE_TABLE_SHOPPINGLIST =
    	    "CREATE TABLE IF NOT EXISTS " + DbSchema.ShoppingListTable.TABLE + " (" +
    	    DbSchema.ShoppingListTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    "FOREIGN KEY(" + DbSchema.ShoppingListTable.COLUMN_ASSOCIATED_PANTRY + ") REFERENCES " 
    	    + DbSchema.PantryTable.TABLE +"(" + DbSchema.PantryTable._ID + ");";

    private static final String SQL_CREATE_TABLE_FOODITEMS =
    	    "CREATE TABLE IF NOT EXISTS " + DbSchema.FoodItemTable.TABLE + " (" +
    	    DbSchema.FoodItemTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    "FOREIGN KEY(" + DbSchema.FoodItemTable.COLUMN_ASSOCIATED_PANTRY + ") REFERENCES " 
    	    + DbSchema.PantryTable.TABLE +"(" + DbSchema.PantryTable._ID + ")," +
    	    "FOREIGN KEY(" + DbSchema.FoodItemTable.COLUMN_ASSOCIATED_SHOPPING_LIST + ") REFERENCES " 
    	    + DbSchema.ShoppingListTable.TABLE +"(" + DbSchema.ShoppingListTable._ID + ")," +
    	    DbSchema.FoodItemTable.COLUMN_NAME + " TEXT NOT NULL, " +
    	    DbSchema.FoodItemTable.COLUMN_EXPIRATION_DATE + " DATE, " +
    	    DbSchema.FoodItemTable.COLUMN_UNIT + " INTEGER, " +
    	    DbSchema.FoodItemTable.COLUMN_CATEGORY + " INTEGER, " +
    	    DbSchema.FoodItemTable.COLUMN_PRICE + " DOUBLE, " +
    	    DbSchema.FoodItemTable.COLUMN_PICTURE + " TEXT);";
    
    private static final String SQL_DELETE_DB = "DROP TABLE IF EXISTS " + DbSchema.FoodItemTable.TABLE + ";" +
    											"DROP TABLE IF EXISTS " + DbSchema.ShoppingListTable.TABLE + ";" +	
    											"DROP TABLE IF EXISTS " + DbSchema.PantryTable.TABLE + ";" +
    											"DROP TABLE IF EXISTS " + DbSchema.UserTable.TABLE + ";"; 
    
    public DbHelper(Context context) {    
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
    @Override
	public void onCreate(SQLiteDatabase db) {
    	db.execSQL(SQL_CREATE_TABLE_USER + SQL_CREATE_TABLE_PANTRY + SQL_CREATE_TABLE_SHOPPINGLIST + SQL_CREATE_TABLE_FOODITEMS);    	
	}
    
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_DB);
        onCreate(db);
	}
	
	@Override
	public SQLiteDatabase getWritableDatabase() {
		return super.getWritableDatabase();
	}
	
	
}

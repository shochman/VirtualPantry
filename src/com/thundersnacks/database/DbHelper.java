package com.thundersnacks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "virtualpantry.db";
    
    // SQL Statements
    private static final String SQL_CREATE_TABLE_USER =
    	    "CREATE TABLE IF NOT EXISTS " + DbSchema.UserTable.TABLE + " (" +
    	    DbSchema.UserTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    DbSchema.UserTable.COLUMN_USERNAME + " TEXT NOT NULL, " +
    	    DbSchema.UserTable.COLUMN_EMAIL + " TEXT NOT NULL, " +
    	    DbSchema.UserTable.COLUMN_PASSWORD + " TEXT NOT NULL)";
    
    private static final String SQL_CREATE_TABLE_PANTRY =
    	    "CREATE TABLE IF NOT EXISTS " + DbSchema.PantryTable.TABLE + " (" +
    	    DbSchema.PantryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    DbSchema.PantryTable.COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
    	    DbSchema.PantryTable.COLUMN_VISIBLE + " INTEGER NOT NULL, " +
    	    DbSchema.PantryTable.COLUMN_ASSOCIATED_USER + " INTEGER, " +
    	    "FOREIGN KEY(" + DbSchema.PantryTable.COLUMN_ASSOCIATED_USER + ") REFERENCES " 
    	    + DbSchema.UserTable.TABLE +"(" + DbSchema.UserTable._ID + "))";
    
    private static final String SQL_CREATE_TABLE_SHOPPINGLIST =
    	    "CREATE TABLE IF NOT EXISTS " + DbSchema.ShoppingListTable.TABLE + " (" +
    	    DbSchema.ShoppingListTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    DbSchema.ShoppingListTable.COLUMN_ASSOCIATED_PANTRY + " INTEGER, " +
    	    "FOREIGN KEY(" + DbSchema.ShoppingListTable.COLUMN_ASSOCIATED_PANTRY + ") REFERENCES " 
    	    + DbSchema.PantryTable.TABLE +"(" + DbSchema.PantryTable._ID + "))";

    private static final String SQL_CREATE_TABLE_FOODITEMS =
    	    "CREATE TABLE IF NOT EXISTS " + DbSchema.FoodItemTable.TABLE + " (" +
    	    DbSchema.FoodItemTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    	    DbSchema.FoodItemTable.COLUMN_ASSOCIATED_PANTRY + " INTEGER, " +
    	    DbSchema.FoodItemTable.COLUMN_ASSOCIATED_SHOPPING_LIST + " INTEGER, " +
    	    DbSchema.FoodItemTable.COLUMN_NAME + " TEXT NOT NULL, " +
    	    DbSchema.FoodItemTable.COLUMN_EXPIRATION_DATE + " DATE, " +
    	    DbSchema.FoodItemTable.COLUMN_AMOUNT + " REAL, " +
    	    DbSchema.FoodItemTable.COLUMN_UNIT + " INTEGER, " +
    	    DbSchema.FoodItemTable.COLUMN_CATEGORY + " INTEGER, " +
    	    DbSchema.FoodItemTable.COLUMN_PRICE + " DOUBLE, " +
    	    DbSchema.FoodItemTable.COLUMN_PICTURE + " TEXT," +
    	    "FOREIGN KEY(" + DbSchema.FoodItemTable.COLUMN_ASSOCIATED_SHOPPING_LIST + ") REFERENCES " 
    	    + DbSchema.ShoppingListTable.TABLE +"(" + DbSchema.ShoppingListTable._ID + ")," +
    	    "FOREIGN KEY(" + DbSchema.FoodItemTable.COLUMN_ASSOCIATED_PANTRY + ") REFERENCES " 
    	    + DbSchema.PantryTable.TABLE +"(" + DbSchema.PantryTable._ID + "))";
    
    private static final String SQL_DELETE_FOOD_ITEMS = "DROP TABLE IF EXISTS " + DbSchema.FoodItemTable.TABLE;
    private static final String SQL_DELETE_SHOPPING_LISTS = "DROP TABLE IF EXISTS " + DbSchema.ShoppingListTable.TABLE;	
	private static final String SQL_DELETE_PANTRIES = "DROP TABLE IF EXISTS " + DbSchema.PantryTable.TABLE;
	private static final String SQL_DELETE_USERS = "DROP TABLE IF EXISTS " + DbSchema.UserTable.TABLE; 
    
    public DbHelper(Context context) {    
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
    @Override
	public void onCreate(SQLiteDatabase db) {
    	db.execSQL(SQL_CREATE_TABLE_USER);
    	db.execSQL(SQL_CREATE_TABLE_PANTRY);
    	db.execSQL(SQL_CREATE_TABLE_SHOPPINGLIST);
    	db.execSQL(SQL_CREATE_TABLE_FOODITEMS); 
	}
    
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_FOOD_ITEMS);
		db.execSQL(SQL_DELETE_SHOPPING_LISTS);
		db.execSQL(SQL_DELETE_PANTRIES);
		db.execSQL(SQL_DELETE_USERS);
        onCreate(db);
	}
	
	@Override
	public SQLiteDatabase getWritableDatabase() {
		return super.getWritableDatabase();
	}
	
	
}

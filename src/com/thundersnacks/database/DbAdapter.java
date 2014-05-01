package com.thundersnacks.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thundersnacks.virtualpantrymodel.*;

public class DbAdapter {

	private SQLiteOpenHelper helper;
	private static DbAdapter adapter = null;
	
	public static DbAdapter instance(Context context) {
		if(adapter == null) {
			adapter = new DbAdapter(context);
		}
		
		return adapter;
	}
	
	private DbAdapter(Context context) {
		helper = new DbHelper(context);
	}
	
	private Date parseDate(String dateString) {
		try {
			Calendar cal = GregorianCalendar.getInstance();
	        String[] pieces = dateString.split("-");
	        String year = pieces[0];
	        String month = pieces[1];
	        String day = pieces[2];
	        int year1 = Integer.parseInt(year);
	        int month1 = Integer.parseInt(month);
	        int day1 = Integer.parseInt(day);
	        cal.set(year1, month1, day1);
	        return cal.getTime();
		}
		catch(Exception e) {
			return new Date();
		}
	}
	
	private ShoppingList getAssociatedShoppingList(int pantryId) {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		String table = DbSchema.ShoppingListTable.TABLE;
		String[] columns = { DbSchema.ShoppingListTable._ID };
		String selection = DbSchema.ShoppingListTable.COLUMN_ASSOCIATED_PANTRY + " = " + pantryId;
		
		Cursor cursor = db.query(table, columns, selection, null, null, null, null);
		
		if(cursor != null) {
			cursor.moveToFirst();
		}
		else {
			return null; 	// no shopping list associated with that pantry
		}
		
		if(cursor.getCount() == 0) {
			throw new SQLException("No associated shopping list exists!!");
		}
		
		int idIndex = cursor.getColumnIndex(DbSchema.ShoppingListTable._ID);
		int id = cursor.getInt(idIndex);
		
		ShoppingList sl = new ShoppingList("", id);
		for(FoodItem item : getContents(id, false)) {
			sl.addItem(item);
		}
		
		cursor.close();
		helper.close();
		return sl;
		
	}

	private List<FoodItem> getContents(int id, boolean queryByPantryId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String table = DbSchema.FoodItemTable.TABLE;
		String[] columns = { DbSchema.FoodItemTable._ID, 
								DbSchema.FoodItemTable.COLUMN_AMOUNT,
								DbSchema.FoodItemTable.COLUMN_CATEGORY,
								DbSchema.FoodItemTable.COLUMN_EXPIRATION_DATE,
								DbSchema.FoodItemTable.COLUMN_NAME,
								DbSchema.FoodItemTable.COLUMN_PICTURE,
								DbSchema.FoodItemTable.COLUMN_UNIT,
								DbSchema.FoodItemTable.COLUMN_PRICE };
		String selectionColumn = queryByPantryId ? DbSchema.FoodItemTable.COLUMN_ASSOCIATED_PANTRY : DbSchema.FoodItemTable.COLUMN_ASSOCIATED_SHOPPING_LIST;  
		String selection = selectionColumn  + " = " + id;
		
		Cursor cursor = db.query(table, columns, selection, null, null, null, null);
		
		if(cursor != null) {
			cursor.moveToFirst();
		}
		else {
			return null; 	// no pantries associated with that user
		}
		
		List<FoodItem> items = new ArrayList<FoodItem>();
		
		if(cursor.getCount() == 0) {
			return items;
		}
		
		do {
			int idIndex = cursor.getColumnIndex(DbSchema.FoodItemTable._ID);
			int amountIndex = cursor.getColumnIndex(DbSchema.FoodItemTable.COLUMN_AMOUNT);
			int catIndex = cursor.getColumnIndex(DbSchema.FoodItemTable.COLUMN_CATEGORY);
			int expIndex = cursor.getColumnIndex(DbSchema.FoodItemTable.COLUMN_EXPIRATION_DATE);
			int nameIndex = cursor.getColumnIndex(DbSchema.FoodItemTable.COLUMN_NAME);
			int picIndex = cursor.getColumnIndex(DbSchema.FoodItemTable.COLUMN_PICTURE);
			int unitIndex = cursor.getColumnIndex(DbSchema.FoodItemTable.COLUMN_UNIT);
			int priceIndex = cursor.getColumnIndex(DbSchema.FoodItemTable.COLUMN_PRICE);
			
			int foodItemId = cursor.getInt(idIndex);
			double amount = cursor.getDouble(amountIndex);
			int categoryValue = cursor.getInt(catIndex);
			FoodItemCategory cat = FoodItemCategory.values()[categoryValue];
			String expString = cursor.getString(expIndex);
			Date expDate = parseDate(expString);
			String name = cursor.getString(nameIndex);
			String pic = cursor.getString(picIndex);
			int unitVal = cursor.getInt(unitIndex);
			FoodItemUnit unit = FoodItemUnit.values()[unitVal];
			double price = cursor.getDouble(priceIndex);
			
			items.add(new StandardFoodItem(name, foodItemId, expDate, amount, unit, pic, cat, price, expString ));
		} while(cursor.moveToNext());
		
		cursor.close();
		helper.close();
		return items;
	}
	
	public Pantry restorePantry(int userId) {
		
		SQLiteDatabase db = helper.getWritableDatabase();
		String table = DbSchema.PantryTable.TABLE;
		String[] columns = { DbSchema.PantryTable._ID, DbSchema.PantryTable.COLUMN_PANTRY_NAME };
		String selection = DbSchema.PantryTable.COLUMN_ASSOCIATED_USER + " = " + userId;
		Cursor cursor = db.query(table, columns, selection, null, null, null, null);
		
		if(cursor != null) {
			cursor.moveToFirst();
		}
		else {
			return null; 	// no pantries associated with that user
		}
		
		if(cursor.getCount() == 0) {
			long pantryID = insertPantry("My Pantry", userId);
			int i = (int)pantryID;
			long slID = insertShoppingList(i);
			int j = (int)slID;
			Pantry newPantry = new Pantry("My Pantry", i, true);
			ShoppingList newShoppingList = new ShoppingList("My Shopping List", j);
			newPantry.setShoppingList(newShoppingList);
			return newPantry;
		}
		
		int idIndex = cursor.getColumnIndex(DbSchema.PantryTable._ID);
		int nameIndex = cursor.getColumnIndex(DbSchema.PantryTable.COLUMN_PANTRY_NAME);
		
		int id = cursor.getInt(idIndex);
		String name = cursor.getString(nameIndex);
		
		Pantry pantry = new Pantry(name, id, true);
		ShoppingList shoppingList = getAssociatedShoppingList(id);
		shoppingList.setName(name);
		pantry.setShoppingList(shoppingList);
		for(FoodItem item : getContents(id, true)) {
			pantry.addItem(item);
		}
		
		cursor.close();
		helper.close();
		return pantry;
		
	}
	
	//---insert a user into the database---
	public boolean insertUser(String username, String password, String email)
	{		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(DbSchema.UserTable.COLUMN_USERNAME, username);
		initialValues.put(DbSchema.UserTable.COLUMN_PASSWORD, password);
		initialValues.put(DbSchema.UserTable.COLUMN_EMAIL, email);
		db.insert(DbSchema.UserTable.TABLE, null, initialValues);
		helper.close();
		return validateUserCredentials(email, password) != null;
	}
	
	//---insert a pantry into the database---
	public long insertPantry(String name, int userId)
	{		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(DbSchema.PantryTable.COLUMN_PANTRY_NAME, name);
		initialValues.put(DbSchema.PantryTable.COLUMN_ASSOCIATED_USER, userId);
		initialValues.put(DbSchema.PantryTable.COLUMN_VISIBLE, 1);
		long id = db.insert(DbSchema.PantryTable.TABLE, null, initialValues);
		helper.close();
		return id;
	}
	
	//---insert a shopping list into the database---
	public long insertShoppingList(int pantryId)
	{		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(DbSchema.ShoppingListTable.COLUMN_ASSOCIATED_PANTRY, pantryId);
		long id = db.insert(DbSchema.ShoppingListTable.TABLE, null, initialValues);
		helper.close();
		return id;
	}
		
	//---validates user credentials---
	public User validateUserCredentials(String email, String password) throws SQLException
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		String table = DbSchema.UserTable.TABLE;
		
		Cursor cursor = db.query(table, null, null, null, null, null, null);
		
		if(cursor != null) { 
			cursor.moveToFirst();
		}
		else {
			return null;
		}
		
		if(cursor.getCount() == 0){
			return null;
		}
		
		int idIndex = cursor.getColumnIndex(DbSchema.UserTable._ID);
		int usernameIndex = cursor.getColumnIndex(DbSchema.UserTable.COLUMN_USERNAME);
		int emailIndex = cursor.getColumnIndex(DbSchema.UserTable.COLUMN_EMAIL);
		int passwordIndex = cursor.getColumnIndex(DbSchema.UserTable.COLUMN_PASSWORD);
		
		while(!cursor.isAfterLast()) {
			int id = cursor.getInt(idIndex);
			String username = cursor.getString(usernameIndex);
			String pswd = cursor.getString(passwordIndex);
			String emailU = cursor.getString(emailIndex);
			if(email.equals(emailU) && password.equals(pswd)) {
				return new User(username, emailU, pswd, pswd, id);
			}
			cursor.moveToNext();
		}
		
		cursor.close();
		helper.close();
		
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//   SAVING
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean savePantry(Pantry p, SQLiteDatabase db) {		
		ContentValues args = new ContentValues();
		args.put(DbSchema.PantryTable.COLUMN_PANTRY_NAME, p.getName());
		args.put(DbSchema.PantryTable.COLUMN_VISIBLE, p.visible());
		return db.update(DbSchema.PantryTable.TABLE, args, DbSchema.PantryTable._ID + "=" + p.getDatabaseId(), null) > 0;
	}

	private boolean saveFoodItem(FoodItem fi, int pantryId, int shoppingListId, SQLiteDatabase db) {
		ContentValues args = new ContentValues();
		args.put(DbSchema.FoodItemTable.COLUMN_AMOUNT, fi.getAmount());
		args.put(DbSchema.FoodItemTable.COLUMN_CATEGORY, fi.getCategory().ordinal());
		args.put(DbSchema.FoodItemTable.COLUMN_EXPIRATION_DATE, fi.getExperiationDateString());
		args.put(DbSchema.FoodItemTable.COLUMN_PICTURE, fi.getPicture());
		args.put(DbSchema.FoodItemTable.COLUMN_PRICE, fi.getPrice());
		args.put(DbSchema.FoodItemTable.COLUMN_UNIT, fi.getUnit().ordinal());
		args.put(DbSchema.FoodItemTable.COLUMN_NAME, fi.getName());
		int affectedRows = db.update(DbSchema.FoodItemTable.TABLE, args, DbSchema.FoodItemTable._ID + "=" + fi.getDatabaseId(), null);
		long insert = -1; 
		if(affectedRows == 0){
			Cursor select = db.rawQuery("select * from fooditem where name = '"+ fi.getName()+"'", null);
			if(select != null) {
				select.moveToFirst();
				if(select.getCount() == 0) {
					args.put(DbSchema.FoodItemTable.COLUMN_ASSOCIATED_PANTRY, pantryId);
					args.put(DbSchema.FoodItemTable.COLUMN_ASSOCIATED_SHOPPING_LIST, shoppingListId);
					insert = db.insert(DbSchema.FoodItemTable.TABLE, null, args);
				}
			}
		}
		
		return insert == -1 && affectedRows == 0;
	}
	
	public boolean save(Pantry pantry) {
		ShoppingList shoppingList = pantry.getShoppingList();
		SQLiteDatabase db = helper.getWritableDatabase();
		
		boolean result = true;
		result &= savePantry(pantry, db);
		
		for(FoodItem item : pantry.getFoodItems()) {
			result &= saveFoodItem(item, pantry.getDatabaseId(), -1, db);
		}
		
		for(FoodItem item : shoppingList.getFoodItems()) {
			result &= saveFoodItem(item, -1, shoppingList.getDatabaseId(), db);
		}
		
		helper.close();
		return result;
		
	}
	
	public boolean removeFromPantry(FoodItem item) {
		SQLiteDatabase db = helper.getWritableDatabase(); 
		ContentValues args = new ContentValues();
		args.put(DbSchema.FoodItemTable.COLUMN_ASSOCIATED_PANTRY, -1);
		int affectedRows = db.update(DbSchema.FoodItemTable.TABLE, args, DbSchema.FoodItemTable._ID + "=" + item.getDatabaseId(), null);
		helper.close();
		return affectedRows > 0;
	}
	
	public boolean removeFromSL(FoodItem item) {
		SQLiteDatabase db = helper.getWritableDatabase(); 
		ContentValues args = new ContentValues();
		args.put(DbSchema.FoodItemTable.COLUMN_ASSOCIATED_SHOPPING_LIST, -1);
		int affectedRows = db.update(DbSchema.FoodItemTable.TABLE, args, DbSchema.FoodItemTable._ID + "=" + item.getDatabaseId(), null);
		helper.close();
		return affectedRows > 0;
	}
	
	public boolean addToSL(FoodItem item, ShoppingList list) {
		SQLiteDatabase db = helper.getWritableDatabase(); 
		ContentValues args = new ContentValues();
		args.put(DbSchema.FoodItemTable.COLUMN_ASSOCIATED_SHOPPING_LIST, list.getDatabaseId());
		int affectedRows = db.update(DbSchema.FoodItemTable.TABLE, args, DbSchema.FoodItemTable._ID + "=" + item.getDatabaseId(), null);
		helper.close();
		return affectedRows > 0;
	}
	
}

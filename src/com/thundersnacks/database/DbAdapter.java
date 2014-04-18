package com.thundersnacks.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thundersnacks.virtualpantrymodel.*;

public class DbAdapter {

	private SQLiteOpenHelper helper;
	
	public DbAdapter(Context context) {
		helper = new DbHelper(context);
	}
	
	private Date parseDate(String dateString) {
		// TODO: actually do this for real
		return new Date();
	}
	
	private ShoppingList getAssociatedShoppingList(int pantryId) {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		String table = DbSchema.ShoppingListTable.TABLE;
		String[] columns = { DbSchema.ShoppingListTable._ID };
		String selection = DbSchema.ShoppingListTable.COLUMN_ASSOCIATED_PANTRY + " = " + pantryId;
		String[] selectionArgs = {};
		String groupBy = "";
		String having = "";
		String orderBy = "";
		
		Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		helper.close();
		
		if(cursor != null) {
			cursor.moveToFirst();
		}
		else {
			return null; 	// no shopping list associated with that pantry
		}
		
		int idIndex = cursor.getColumnIndex(DbSchema.ShoppingListTable._ID);
		int id = cursor.getInt(idIndex);
		
		ShoppingList sl = new ShoppingList("", id);
		for(FoodItem item : getContents(id, false)) {
			sl.addItem(item);
		}
		
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
		helper.close();
		
		if(cursor != null) {
			cursor.moveToFirst();
		}
		else {
			return null; 	// no pantries associated with that user
		}
		
		List<FoodItem> items = new ArrayList<FoodItem>();
		
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
			
			items.add(new StandardFoodItem(name, foodItemId, expDate, amount, unit, pic, cat, price ));
		} while(cursor.moveToNext());
		
		return items;
		
	}
	
	public Pantry restorePantry(int userId) {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		String table = DbSchema.PantryTable.TABLE;
		String[] columns = { DbSchema.PantryTable._ID, DbSchema.PantryTable.COLUMN_PANTRY_NAME };
		String selection = "(" + DbSchema.PantryTable.COLUMN_ASSOCIATED_USER + " = " + userId
							+ " AND " + DbSchema.PantryTable.COLUMN_VISIBLE + " = 1 )";
		
		Cursor cursor = db.query(table, columns, selection, null, null, null, null);
		helper.close();
		
		if(cursor != null) {
			cursor.moveToFirst();
		}
		else {
			return null; 	// no pantries associated with that user
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
		
		return pantry;
		
	}
	
	//---insert a user into the database---
	public long insertUser(String username, String password, String email)
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(DbSchema.UserTable.COLUMN_USERNAME, username);
		initialValues.put(DbSchema.UserTable.COLUMN_PASSWORD, password);
		initialValues.put(DbSchema.UserTable.COLUMN_EMAIL, email);
		long result = db.insert(DbSchema.UserTable.TABLE, null, initialValues);
		helper.close();
		return result;
	}
		
	//---validates user credentials---
	public User validateUserCredentials(String username, String password) throws SQLException
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		String table = DbSchema.UserTable.TABLE;
		String[] columns = { DbSchema.UserTable._ID, 
								DbSchema.UserTable.COLUMN_EMAIL };
		String selection = "(" + DbSchema.UserTable.COLUMN_USERNAME + " = " + username
							+ " AND " + DbSchema.UserTable.COLUMN_PASSWORD + " = " + password + " )";
		
		Cursor cursor = db.query(table, columns, selection, null, null, null, null);
		helper.close();
		
		if(cursor != null) {
			cursor.moveToFirst();
		}
		else
			return null;
		
		int idIndex = cursor.getColumnIndex(DbSchema.UserTable._ID);
		int emailIndex = cursor.getColumnIndex(DbSchema.UserTable.COLUMN_EMAIL);
		
		int id = cursor.getInt(idIndex);
		String email = cursor.getString(emailIndex);
		
		return new User(username, email, password, password, id);
		
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

	private boolean saveFoodItem(FoodItem fi, SQLiteDatabase db) {
		ContentValues args = new ContentValues();
		args.put(DbSchema.FoodItemTable.COLUMN_AMOUNT, fi.getAmount());
		args.put(DbSchema.FoodItemTable.COLUMN_CATEGORY, fi.getCategory().ordinal());
		args.put(DbSchema.FoodItemTable.COLUMN_EXPIRATION_DATE, fi.getExperiationDate().toString());
		args.put(DbSchema.FoodItemTable.COLUMN_PICTURE, fi.getPicture());
		args.put(DbSchema.FoodItemTable.COLUMN_PRICE, fi.getPrice());
		args.put(DbSchema.FoodItemTable.COLUMN_UNIT, fi.getUnit().ordinal());
		args.put(DbSchema.FoodItemTable.COLUMN_NAME, fi.getName());
		return db.update(DbSchema.FoodItemTable.TABLE, args, DbSchema.FoodItemTable._ID + "=" + fi.getDatabaseId(), null) > 0;
	}
	
	public boolean save(Pantry pantry) {
		ShoppingList shoppingList = pantry.getShoppingList();
		SQLiteDatabase db = helper.getWritableDatabase();
		
		boolean result = true;
		result &= savePantry(pantry, db);
		
		for(FoodItem item : pantry.getFoodItems()) {
			result &= saveFoodItem(item, db);
		}
		
		for(FoodItem item : shoppingList.getFoodItems()) {
			result &= saveFoodItem(item, db);
		}
		
		helper.close();
		return result;
		
	}
	
}

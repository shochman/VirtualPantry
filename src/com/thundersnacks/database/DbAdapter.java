package com.thundersnacks.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.thundersnacks.virtualpantrymodel.*;

public class DbAdapter {

	private SQLiteOpenHelper helper;
	
	public DbAdapter(Context context) {
		helper = new DbHelper(context);
	}
	
	private Date parseDate(String dateString) {
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
							+ " AND " + DbSchema.PantryTable.COLUMN_VISIBLE + " = TRUE";
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
			return null; 	// no pantries associated with that user
		}
		
		int idIndex = cursor.getColumnIndex(DbSchema.PantryTable._ID);
		int nameIndex = cursor.getColumnIndex(DbSchema.PantryTable.COLUMN_PANTRY_NAME);
		
		int id = cursor.getInt(idIndex);
		String name = cursor.getString(nameIndex);
		
		Pantry pantry = new Pantry(name, id);
		ShoppingList shoppingList = getAssociatedShoppingList(id);
		shoppingList.setName(name);
		pantry.setShoppingList(shoppingList);
		for(FoodItem item : getContents(id, true)) {
			pantry.addItem(item);
		}
		
		return pantry;
		
	}
	
}

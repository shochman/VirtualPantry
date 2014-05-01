
package com.thundersnacks.virtualpantrymodel;

import java.util.Date;

public class StandardFoodItem extends FoodItem {
	
	public StandardFoodItem ( String name, int databaseId, Date expDate, double amount, FoodItemUnit unit,
			String pic, FoodItemCategory cat, double price, String expString ) {
		
		super( name, databaseId, expDate, amount, unit, pic, cat, price, expString );
		
	}

}

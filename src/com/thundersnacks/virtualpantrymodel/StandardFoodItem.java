package com.thundersnacks.virtualpantrymodel;

import java.util.Date;

public class StandardFoodItem extends FoodItem {
	
	public StandardFoodItem ( String name, int databaseId, Date expDate, String amount, String pic ) {
		
		super( name, databaseId, expDate, amount, pic );
		
	}

}

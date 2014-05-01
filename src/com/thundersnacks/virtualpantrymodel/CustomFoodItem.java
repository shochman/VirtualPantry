package com.thundersnacks.virtualpantrymodel;

import java.util.Date;

public class CustomFoodItem extends FoodItem {
	
	private int associatedUserId;
	
	public CustomFoodItem( String name, int databaseId, Date expDate, double amount, FoodItemUnit unit,
			String pic, FoodItemCategory cat, int associatedUserId, double price, String expString ) {
				
		super( name, databaseId, expDate, amount, unit, pic, cat, price, expString);
		this.associatedUserId = associatedUserId;
		
	}
	
	public int getAssociatedUserId() {
		
		return this.associatedUserId;
		
	}

}


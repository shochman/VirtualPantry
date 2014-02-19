package com.thundersnacks.virtualpantrymodel;

import java.util.Date;

public class CustomFoodItem extends FoodItem {
	
	private int associatedUserId;
	
	public CustomFoodItem( String name, int databaseId, Date expDate, String amount, 
			String pic, FoodItemCategory cat, int associatedUserId ) {
				
		super( name, databaseId, expDate, amount, pic, cat );
		this.associatedUserId = associatedUserId;
		
	}
	
	public int getAssociatedUserId() {
		
		return this.associatedUserId;
		
	}

}

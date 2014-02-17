package com.thundersnacks.virtualpantrymodel;

import java.util.Date;

public class CustomFoodItem extends FoodItem {
	
	private int associatedUserId;
	
	public CustomFoodItem( String name, int databaseId, Date expDate, String amount, String pic, int associatedUserId ) {
				
		super( name, databaseId, expDate, amount, pic );
		this.associatedUserId = associatedUserId;
		
	}
	
	public int getAssociatedUserId() {
		
		return this.associatedUserId;
		
	}

}

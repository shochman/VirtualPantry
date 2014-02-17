package com.thundersnacks.virtualpantrymodel;

import java.util.ArrayList;

public class Pantry {

	private String name;
	private int databaseid;
	private ArrayList<FoodItem> foodItems;
	ShoppingList shoppingList;
	
	public Pantry( String pantryName, int databaseId ) {
		this.name = "";
		this.databaseid = 0;
		this.foodItems = new ArrayList<FoodItem>();
	}
	
	public boolean addItem(FoodItem itemToAdd) {
		// return true if insert is successful
		// throws exception when item can't be added
		return foodItems.add(itemToAdd);
	}
	
	public boolean removeItem(FoodItem itemToRemove) {
		// return true if removal is successful
		// return false if no item is removed
		return foodItems.remove(itemToRemove);
	}
	
}

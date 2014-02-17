package com.thundersnacks.virtualpantrymodel;

import java.util.ArrayList;

public class Pantry {

	private String name;
	private int databaseid;
	private ArrayList<FoodItem> foodItems;
	ShoppingList shoppingList;
	
	public Pantry()
	{
		this.name = "";
		this.databaseid = 0;
		this.foodItems = new ArrayList<FoodItem>();
	}
	
	public boolean addItem(FoodItem itemToAdd)
	{
		//return true if insert is successful
		// throws an exception if item cannot be added
		return foodItems.add(itemToAdd);
	}
	
	public boolean removeItem(FoodItem itemToRemove)
	{
		//return true if removal is successful
		// throws an exception if item is not supported
		return foodItems.remove(itemToRemove);
	}
}

package com.thundersnacks.virtualpantrymodel;

import java.util.HashMap;
import java.util.Map;

public class ShoppingList{
	
	private int databaseID;
	private Map<FoodItem, Boolean> items;
	Pantry	pantry;
	
	public ShoppingList()
	{
		this.databaseID = 0;
		this.items = new HashMap<FoodItem, Boolean>();
	}
	public boolean addItem(FoodItem itemToAdd) 
	{
		items.put(itemToAdd,true);
		return items.containsKey(itemToAdd);
	}
	
	public boolean removeItem(FoodItem itemToRemove) 
	{
		items.remove(itemToRemove);
		return !items.containsKey(itemToRemove);
	}
	public Pantry getPantry()
	{
		return pantry;
	}
	public Map<FoodItem,Boolean> getItems()
	{
		return items;
	}
}

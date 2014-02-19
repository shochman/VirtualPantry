package com.thundersnacks.virtualpantrymodel;

import java.util.HashMap;
import java.util.Map;

public class ShoppingList{
	
	private int databaseId;
	private static Map<FoodItem, Boolean> items;
	Pantry	pantry;
	
	public ShoppingList()
	{
		this.databaseId = 0;
		this.items = new HashMap<FoodItem, Boolean>();
	}
	
	public ShoppingList(Map<FoodItem, Boolean> items, Pantry pantry)
	{
		this.items  = items;
		this.pantry = pantry;
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
	
	public int getDatabaseId() 
	{		
		return this.databaseId;		
	}
	
	public Pantry getPantry()
	{
		return this.pantry;
	}
	
	public static Map<FoodItem,Boolean> getItems()
	{
		return items;
	}
}

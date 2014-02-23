package com.thundersnacks.virtualpantrymodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pantry implements Iterable<FoodItem> {
//hi
	private String name;
	private int databaseId;
	private List<FoodItem> foodItems;
	private ShoppingList shoppingList;
	
	public Pantry( String pantryName, int databaseId ) {
		this.name = pantryName;
		this.databaseId = databaseId;
		this.foodItems = new ArrayList<FoodItem>();
	}
	
	public String getName() {		
		return this.name;		
	}
	
	public void setName( String name ) {		
		this.name = name;		
	}
	
	public int getDatabaseId() {		
		return this.databaseId;		
	}
	
	public ShoppingList getShoppingList() {
		return this.shoppingList;
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
	
	public Iterator<FoodItem> iterator() {
		return foodItems.iterator();
	}
	
	public List<FoodItem> getItemsByCategory(FoodItemCategory cat) {
		List<FoodItem> categorized = new ArrayList<FoodItem>();
		for(FoodItem item: this.foodItems) {
			if(item.getCategory().equals(cat))
				categorized.add(item);
		}
		
		return categorized;
	}
	
}

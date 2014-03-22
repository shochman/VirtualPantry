package com.thundersnacks.virtualpantrymodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Pantry implements Iterable<FoodItem> {

	private String name;
	private int databaseId;
	private List<FoodItem> foodItems;
	private ShoppingList shoppingList;
	private int howSorted;
	
	public Pantry() {
		foodItems = new ArrayList<FoodItem>();
	}
	
	public Pantry(String name, int databaseId) {
		this.name = name;
		this.databaseId = databaseId;
		this.foodItems = new ArrayList<FoodItem>();
	}
	
	public String getName() {		
		return name;		
	}
	
	public void setName( String name ) {		
		this.name = name;		
	}
	
	public int getDatabaseId() {		
		return databaseId;		
	}
	
	public ShoppingList getShoppingList() {
		return shoppingList;
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
	
	public void alphabeticalSort() {
		Collections.sort(foodItems, FoodItem.getAlphabeticalComparator());
		howSorted = 1;
	}
	
	public void expirationSort() {
		Collections.sort(foodItems, FoodItem.getExpirationComparator());
		howSorted = 2;
	}
	
	public void categorySort() {
		Collections.sort(foodItems, FoodItem.getCategoryComparator());
		howSorted = 0;
	}
	
	public int getHowSorted() {
		return howSorted;
	}
	
	public List<FoodItem> getFoodItems() {
		return foodItems;
	}

}

package com.thundersnacks.virtualpantrymodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ShoppingList{
	
	private String name;
	private int databaseId;
	private List<FoodItem> foodItems;
	Pantry	pantry;
	private int howSorted;
	
	public ShoppingList() {
		foodItems = new ArrayList<FoodItem>();	
	}
	
	public ShoppingList(String name, int databaseId) {
		this.name = name;
		this.databaseId = databaseId;
		foodItems = new ArrayList<FoodItem>();
	}
	
	public String getName() {		
		return name;		
	}
	
	public void setName(String name)  {		
		this.name = name;		
	}
	
	public int getDatabaseId() {		
		return databaseId;		
	}
	
	public Pantry getPantry() {
		return pantry;
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
	
	public boolean isInShoppingList(FoodItem food) {
		for (int i = 0; i < foodItems.size(); i++) {
    		if (foodItems.get(i).getName().equals(food.getName())) {
    			return true;
    		}
    	}
		return false;
	}
}
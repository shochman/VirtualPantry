package com.thundersnacks.virtualpantrymodel;

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
	
	private int databaseId;
	private static Map<FoodItem, Boolean> items;
	Pantry	pantry;
	
	public ShoppingList()
	{
		this.databaseId = 0;
		this.items = new HashMap<FoodItem, Boolean>();
	}
	
	Comparator<FoodItem> secondCharComparator = new Comparator<FoodItem>() {
        @Override public int compare(FoodItem s1, FoodItem s2) {
            return s1.getName().substring(1, 2).compareTo(s2.getName().substring(1, 2));
        }           
    };
	
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
	
	public boolean removeItemByName(String name)
	{
		for (Map.Entry<FoodItem, Boolean> e : items.entrySet()) {
			if (name.equals(e.getKey().getName())) {
				removeItem(e.getKey());
				break;
			}
		}
		return true;
	}

	
	public int getDatabaseId() 
	{		
		return this.databaseId;		
	}
	
	public Pantry getPantry()
	{
		return this.pantry;
	}
	
	public void setPantry(Pantry pantry)
	{
		this.pantry = pantry;
	}
	
	public static Map<FoodItem,Boolean> getItems()
	{
		return items;
	}
	
	public FoodItem getItem(String name)
	{
		for ( FoodItem key : items.keySet() ) 
		{
			if( key.getName().equals(name) )
				return key;
		}
		// Given name is not found or case sensitive to FoodItem keys in the set.
		return null;
	}
	
	public FoodItem getCheckedFoodItem()
	{
		for ( FoodItem key : items.keySet() ) 
		{
			if( items.get(key).equals(false) )
				{
					removeItem(key);
					return key;
				}
		}
		// Given name is not found or case sensitive to FoodItem keys in the set.
		return null;
	}
	
	public void setItemMapValue(FoodItem key, Boolean value)
	{
		items.put(key, value);
	}
	
	public static Map<FoodItem,Boolean> sortByComparator(Map<FoodItem,Boolean> unsortMap) {
		 
		LinkedList<Entry<FoodItem, Boolean>> list = new LinkedList<Entry<FoodItem, Boolean>>(unsortMap.entrySet());
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<FoodItem, Boolean>>() {
			
			@Override
			public int compare(Entry<FoodItem, Boolean> o1,
					Entry<FoodItem, Boolean> o2) {
				// TODO Auto-generated method stub
				/*
				 * return ((Comparable) ( (Map.Entry) (o1)).getKey() )
                 *       .compareTo(((Map.Entry) (o2)).getKey());
				 */
				return (( (String) (o1.getKey().getName())))
                        .compareTo(( (String) (o2.getKey().getName())));
			}
		});
			Comparator<FoodItem> secondCharComparator = new Comparator<FoodItem>() {
		        @Override public int compare(FoodItem s1, FoodItem s2) {
		            return s1.getName().substring(1, 2).compareTo(s2.getName().substring(1, 2));
		        }           
		    };

		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public boolean isInShoppingList(FoodItem food) {
		for (Iterator<Map.Entry<FoodItem, Boolean>> it = items.entrySet().iterator(); it.hasNext(); ) {
    		Entry entry = (Entry)it.next();
    		FoodItem test = (FoodItem)entry.getKey();
    		if (test.getName().equals(food.getName())) {
    			return true;
    		}
    	}
		return false;
	}
}

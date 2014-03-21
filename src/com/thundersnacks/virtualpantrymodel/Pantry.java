<<<<<<< HEAD
package com.thundersnacks.virtualpantrymodel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Owner
 * Date: 2/5/14
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pantry {
    private String name;
    private int databaseID;
    private List<FoodItem> foodItems;
    private ShoppingList shoppingList;

    public Pantry(String name) {
        this.name = name;
        this.shoppingList = new ShoppingList();
    }

    public String getName() {
        return this.name;
    }

    public int getDatabaseID() {
        return this.databaseID;
    }

    public List<FoodItem> getFoodItems(){
        return this.foodItems;
    }

    public ShoppingList getShoppingList() {
        return this.shoppingList;
    }

    public boolean addItem(FoodItem itemToAdd) {
        this.foodItems.add(itemToAdd);
        return true;
    }

    public boolean removeItem(FoodItem itemToRemove) {
        if(this.foodItems.contains(itemToRemove)) {
            this.foodItems.remove(itemToRemove);
            return true;
        }

        return false;
    }
}
=======
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
>>>>>>> d8427cccbe098bc19e19031876695251422b26bc

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

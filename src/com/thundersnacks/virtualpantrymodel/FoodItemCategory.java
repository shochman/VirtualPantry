package com.thundersnacks.virtualpantrymodel;

public enum FoodItemCategory {

	BEVERAGE, 
	PROTEIN,
	FRUIT,
	VEGETABLE,
	DAIRY, 
	FROZEN, 
	CONDIMENT, 
	SWEET, 
	SNACK, 
	GRAIN,
	FAT,
	OTHER;

    public String toString() {
    	switch (this) {
		case BEVERAGE:
			return "Beverage";
		case CONDIMENT:
			return "Condiment";
		case DAIRY:
			return "Dairy";
		case FAT:
			return "Fat";
		case FROZEN:
			return "Frozen";
		case FRUIT:
			return "Fruit";
		case GRAIN:
			return "Grain";
		case OTHER:
			return "Other";
		case PROTEIN:
			return "Protein";
		case SNACK:
			return "Snack";
		case SWEET:
			return "Sweet";
		case VEGETABLE:
			return "Vegetable";
		default:
			return "";
    	}
    }
    
}

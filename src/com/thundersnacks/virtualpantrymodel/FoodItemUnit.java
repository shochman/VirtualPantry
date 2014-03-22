package com.thundersnacks.virtualpantrymodel;

public enum FoodItemUnit {

	POUNDS,
	CANS,
	BOTTLES,
	GALLONS,
	CUPS,
	BAGS,
	TUBS,
	LOAF,
	BOX,
	UNITLESS,
	OTHER;

    public String toString() {
    	switch (this) {
		case POUNDS:
			return "Pound(s)";
		case CANS:
			return "Can(s)";
		case BOTTLES:
			return "Bottle(s)";
		case GALLONS:
			return "Gallon(s)";
		case CUPS:
			return "Cup(s)";
		case BAGS:
			return "Bag(s)";
		case TUBS:
			return "Tub(s)";
		case LOAF:
			return "Loaf";
		case BOX:
			return "Box(es)";
		case UNITLESS:
			return "No Unit";
		default:
			return "";
    	}
    }
    
}

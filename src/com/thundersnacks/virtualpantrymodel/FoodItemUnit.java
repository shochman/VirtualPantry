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
	GRAMS,
	OUNCES,
	FLUIDOUNCES,
	MILLILITERS,
	LITERS,
	PINTS,
	UNITLESS,
	OTHER;

    public String toString() {
    	switch (this) {
		case POUNDS:
			return "lb(s)";
		case CANS:
			return "can(s)";
		case BOTTLES:
			return "bottle(s)";
		case GALLONS:
			return "gal(s)";
		case CUPS:
			return "cup(s)";
		case BAGS:
			return "bag(s)";
		case TUBS:
			return "tub(s)";
		case LOAF:
			return "loaf";
		case BOX:
			return "box(es)";
		case GRAMS:
			return "g(s)";
		case OUNCES:
			return "oz(s)";
		case FLUIDOUNCES:
			return "fl oz(s)";
		case MILLILITERS:
			return "mL(s)";
		case LITERS:
			return "L(s)";
		case PINTS:
			return "pt(s)";
		case UNITLESS:
			return "(No Unit)";
		default:
			return "";
    	}
    }
    
}

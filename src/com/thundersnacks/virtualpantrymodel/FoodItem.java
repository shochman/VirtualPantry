package com.thundersnacks.virtualpantrymodel;

import java.util.Comparator;
import java.util.Date;

public abstract class FoodItem {
	
	private String name;
	private int databaseId;
	private Date expirationDate;
	private double amount;
	private FoodItemUnit unit;
	private String picture;
	private FoodItemCategory category;
	private double price;
	private String expString;
	
	FoodItem( String name, int databaseId, Date expDate, double amount, FoodItemUnit unit, String pic, FoodItemCategory cat, double pri, String expString) {
		this.name = name;
		this.databaseId = databaseId;
		this.expirationDate = expDate;
		this.amount = amount;
		this.unit = unit;
		this.picture = pic;
		this.category = cat;
		this.price=pri;
		this.expString = expString;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getDatabaseId() {
		return databaseId;
	}
	
	public Date getExperiationDate() {
		return expirationDate;
	}
	
	public void setExperiationDate(Date date) {
		this.expirationDate = date;
	}
	
	public String getExperiationDateString() {
		return expString;
	}
	
	public void setExperiationDateString(String date) {
		this.expString = date;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public FoodItemUnit getUnit() {
		return this.unit;
	}
	
	public void setUnit(FoodItemUnit u) {
		this.unit = u;
	}
	
	public String getPicture() {
		return picture;
	}
	
	public void setPicture(String Picture) {
		this.picture = Picture;
	}
	
	public FoodItemCategory getCategory() {
		return this.category;
	}
	
	public void setCategory(FoodItemCategory cat) {
		this.category = cat;
	}

	public double getPrice() {
		return this.price;
	}
	
	public void setPrice(double pri) {
		this.price = pri;
	}
	
	public static Comparator<FoodItem> getAlphabeticalComparator() {
		return new Comparator<FoodItem>() {
			public int compare(FoodItem a, FoodItem b)
			{
				return a.getName().compareTo(b.getName());
			}
		};
	}

	static Comparator<FoodItem> getExpirationComparator() {
	return new Comparator<FoodItem>() {
			public int compare(FoodItem a, FoodItem b)
			{
				return a.getExperiationDate().compareTo(b.getExperiationDate());
			}
		};
	}

	public static Comparator<FoodItem> getCategoryComparator() {
		return new Comparator<FoodItem>() {
			public int compare(FoodItem a, FoodItem b)
			{
				int comp = a.getCategory().compareTo(b.getCategory());
				if (comp == 0)
					return a.getName().compareTo(b.getName());
				else return comp;
			}
		};
	}
	
	public String toString() {
		return name;
	}
}

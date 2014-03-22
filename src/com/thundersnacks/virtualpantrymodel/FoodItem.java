package com.thundersnacks.virtualpantrymodel;

import java.util.Comparator;
import java.util.Date;

public abstract class FoodItem {
	
	private String name;
	private int databaseId;
	private Date expirationDate;
	private String amount;
	private String picture;
	private FoodItemCategory category;
	
	FoodItem( String name, int databaseId, Date expDate, String amount, String pic, FoodItemCategory cat ) {
		this.name = name;
		this.databaseId = databaseId;
		this.expirationDate = expDate;
		this.amount = amount;
		this.picture = pic;
		this.category = cat;
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
	
	public String getAmount() {
		return amount;
	}
	
	public void setAmount(String Amount) {
		this.amount = Amount;
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

	static Comparator<FoodItem> getAlphabeticalComparator() {
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

	static Comparator<FoodItem> getCategoryComparator() {
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

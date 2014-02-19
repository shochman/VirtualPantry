package com.thundersnacks.virtualpantrymodel;

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

}

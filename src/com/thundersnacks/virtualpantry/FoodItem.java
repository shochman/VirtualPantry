package com.thundersnacks.virtualpantry;

import java.sql.Date;

public class FoodItem {
	
	private String name;
	private int databaseId;
	private Date expirationDate;
	private String amount;
	private String picture;
	
	FoodItem()
	{
		this.name = "";
		this.databaseId = 0;
		this.expirationDate = new Date((2004-1900)*365*24*60*60*60);
		this.amount = "";
		this.picture = "";
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Date getExperiationDate()
	{
		return expirationDate;
	}
	public void setExperiationDate(Date date)
	{
		this.expirationDate = date;
	}
	public String getAmount()
	{
		return amount;
	}
	public void setAmount(String Amount)
	{
		this.amount = Amount;
	}
	public String getPicture()
	{
		return picture;
	}
	public void setPicture(String Picture)
	{
		this.picture = Picture;
	}

}

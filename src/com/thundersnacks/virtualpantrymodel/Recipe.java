package com.thundersnacks.virtualpantrymodel;

public class Recipe {
	private final String f2f_url;
	private final String img_url;
	private final String name;
	
	public String getF2f_url() {
		return f2f_url;
	}

	public String getImg_url() {
		return img_url;
	}

	public Recipe(String f2f_url, String img_url, String name) {
		this.f2f_url = f2f_url;
		this.img_url = img_url;
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

package com.thundersnacks.virtualpantrymodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class RecipeArray {
	private List<Recipe> recipes;
	
	public RecipeArray() {
		recipes = new ArrayList<Recipe>();
	}
	
	public RecipeArray(Recipe... rec) {
		recipes = new ArrayList<Recipe>();
		for(Recipe r : rec) {
			recipes.add(r);
		}
	}
	
	public void addRecipe(Recipe r) {
		recipes.add(r);
	}
	
	public List<Recipe> getRecipeArray() {
		return recipes;
	}
	
	public void parse(String jsonDoc) {
		JsonParserFactory factory = JsonParserFactory.getInstance();
		JSONParser parser = factory.newJsonParser();
		Map rootJSON = parser.parseJson(jsonDoc);
		List recipeList = (List)rootJSON.get("recipes");
		for(int i=0; i < recipeList.size(); i++) {
			Recipe rec = new Recipe((String)((Map)recipeList.get(i)).get("f2f_url"),
									(String)((Map)recipeList.get(i)).get("image_url"),
									(String)((Map)recipeList.get(i)).get("title"));
			this.addRecipe(rec);
		}
	}
}

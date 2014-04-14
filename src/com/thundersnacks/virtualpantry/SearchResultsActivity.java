package com.thundersnacks.virtualpantry;

import java.util.ArrayList;
import java.util.List;

import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class SearchResultsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {

		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			//use the query to search pantry data
			FoodItemCategory[] categoryList = FoodItemCategory.values();

			PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");

			List<FoodItem> foodItem = pf.getPantry().getFoodItems();
			List<FoodItem> foodResults = new ArrayList<FoodItem>();
			for(FoodItem food : foodItem){
				if (food.getName().equals(query))
				{	
					foodResults.add(food);
				}

				}
			}
		}

	}
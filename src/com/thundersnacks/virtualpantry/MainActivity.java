package com.thundersnacks.virtualpantry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.BarCodeParser;
import com.thundersnacks.virtualpantrymodel.BarCodeParser.Table;
import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;
import com.thundersnacks.virtualpantrymodel.FoodItemUnit;
import com.thundersnacks.virtualpantrymodel.Recipe;
import com.thundersnacks.virtualpantrymodel.RecipeArray;
import com.thundersnacks.virtualpantrymodel.StandardFoodItem;
import com.thundersnacks.zxing.IntentIntegrator;
import com.thundersnacks.zxing.IntentResult;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	RadioGroup sortByMenu;
	Button sortByOkButton;
	RadioButton sortByRadioButton;
	Dialog addDialog;
	String text;
	static final int REQUEST_EXIT = 0;
	static PantryFragment pantryFragment;
	static ShoppingListFragment shoppingListFragment;
	
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
	    private Dialog dialog;
		@Override
	    protected String doInBackground(String... urls) {
	      String response = "";
	      for (String url : urls) {
	    	  //Exit if  cancel(boolean)  is called on this task
	    	if(!this.isCancelled()){
		        DefaultHttpClient client = new DefaultHttpClient();
		        HttpGet httpGet = new HttpGet(url);
		        try {
		          HttpResponse execute = client.execute(httpGet);
		          InputStream content = execute.getEntity().getContent();
	
		          BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		          String s = "";
		          while ((s = buffer.readLine()) != null) {
		            response += s;
		          }
	
		        } catch (Exception e) {
		          e.printStackTrace();
		        }
		      }
	      }
	      return response;
	    }
	    @Override 
	    protected void onPreExecute(){
	    	this.dialog = new Dialog(MainActivity.this);
	    	this.dialog.setTitle("Processing...");
	    	this.dialog.show();
	    }
	    @Override
	    protected void onPostExecute(String result) {
	    	//super.onPostExecute(result);
	    	text = result;
	    	this.dialog.dismiss();
	    }
	  }
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    @Override
	    protected void onPreExecute() {
	        // TODO Auto-generated method stub
	        super.onPreExecute();
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	          InputStream in = new java.net.URL(urldisplay).openStream();
	          mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    @Override 
	    protected void onPostExecute(Bitmap result) {
	        super.onPostExecute(result);
	        bmImage.setImageBitmap(Bitmap.createScaledBitmap(result, 100, 100, false));
	    }
	  }
	
	
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
	    private Fragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;

	    /** Constructor used each time a new tab is created.
	      * @param activity  The host Activity, used to instantiate the fragment
	      * @param tag  The identifier tag for the fragment
	      * @param clz  The fragment's Class, used to instantiate the fragment
	      */
	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }

	    /* The following are each of the ActionBar.TabListener callbacks */

	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	            mFragment = Fragment.instantiate(mActivity, mClass.getName());
	            ft.add(android.R.id.content, mFragment, mTag);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	        if (((MainActivity) mActivity).firstOpen == true) {
	        	((MainActivity) mActivity).updateSearch();
	        }
	    }

	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	            if (((MainActivity) mActivity).firstOpen == true) {
	            	MenuItem searchMenuItem = menu.findItem(R.id.action_search);
	            	searchMenuItem.collapseActionView();
		        }
	        }
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
	}
	
	static Menu menu;
	boolean firstOpen = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        // Notice that setContentView() is not used, because we use the root
        // android.R.id.content as the container for each fragment

        // setup action bar for tabs
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        Tab pantryTab = actionBar.newTab().setText(R.string.title_activity_pantry).setTabListener(new TabListener<PantryFragment>(this, "Pantry", PantryFragment.class));
        actionBar.addTab(pantryTab);

        Tab shoppingListTab = actionBar.newTab().setText(R.string.title_activity_shopping_list).setTabListener(new TabListener<ShoppingListFragment>(this, "Shopping List", ShoppingListFragment.class));
        actionBar.addTab(shoppingListTab);
        actionBar.setSelectedNavigationItem(1);
        actionBar.setSelectedNavigationItem(0);
    }
    
    @Override
	protected void onNewIntent(Intent intent) {
    	if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
    		handleSearch(intent);
    	}
	}
    
    private void handleSearch(Intent intent) {
    	String query = intent.getStringExtra(SearchManager.QUERY);
		//use the query to search pantry data
		//FoodItemCategory[] categoryList = FoodItemCategory.values();
    	if (getActionBar().getSelectedTab().getPosition() == 0) {
    		PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");
    		List<FoodItem> foodItem = pf.getPantry().getFoodItems();
    		List<FoodItem> foodResults = new ArrayList<FoodItem>();
    		for(FoodItem food : foodItem) {
    			if (food.getName().toLowerCase().contains(query.toLowerCase())) {	
    				foodResults.add(food);
    			}
    		}
    		Collections.sort(foodResults, FoodItem.getAlphabeticalComparator());
    		pf.setSearchFoodItems(foodResults);
    		pf.createPantry(true);
    	} else if (getActionBar().getSelectedTab().getPosition() == 1) {
    		ShoppingListFragment slf = (ShoppingListFragment) getFragmentManager().findFragmentByTag("Shopping List");
    		List<FoodItem> foodItem = slf.getShoppingList().getFoodItems();
    		List<FoodItem> foodResults = new ArrayList<FoodItem>();
    		for(FoodItem food : foodItem) {
    			if (food.getName().toLowerCase().contains(query.toLowerCase())) {
    				foodResults.add(food);
    			}
    		}
    		Collections.sort(foodResults, FoodItem.getAlphabeticalComparator());
    		slf.setSearchFoodItems(foodResults);
    		slf.createShoppingList(true);
    	}
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setOnActionExpandListener(new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
            	if (getActionBar().getSelectedTab().getPosition() == 0) {
            		PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");
            		pf.createPantry(false);
            	} else if (getActionBar().getSelectedTab().getPosition() == 1) {
            		ShoppingListFragment slf = (ShoppingListFragment) getFragmentManager().findFragmentByTag("Shopping List");
            		slf.createShoppingList(false);
            	}
                return true;       // Return true to collapse action view
            }
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;      // Return true to expand action view
            }
        });
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Pantry");
        this.menu = menu;
        firstOpen = true;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    	boolean defValue = false;
    	boolean checkedAuto = sharedPref.getBoolean("auto_add", defValue);
    	String itemsUpdated = "Added items:";
    	PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");
		ShoppingListFragment slf = (ShoppingListFragment) getFragmentManager().findFragmentByTag("Shopping List");
		pantryFragment = pf;
		shoppingListFragment = slf;
		
    	if(checkedAuto)
    	{			
			FoodItemCategory[] categoryList = FoodItemCategory.values();
			int position = 0, expired = 0;
			while (position < categoryList.length)
			{
				List<FoodItem> categorized = pf.getPantry().getItemsByCategory(categoryList[position]);
				for(FoodItem Item: categorized) {
					if(Item.getExperiationDate().before(new Date()))
						{	expired++;
							slf.getShoppingList().addItem(Item);
							if( expired > 1 )
							{
								itemsUpdated += ",";
							}
							itemsUpdated += " "+ Item.getName();
						}
				}
				position++;
			}
			int addAnd = itemsUpdated.lastIndexOf(" ");
			String lastItem = itemsUpdated.substring(addAnd);
			itemsUpdated = itemsUpdated.substring(0, addAnd-1)+ " and"+ lastItem;
    	}
    	
    	String pantrySortPref = sharedPref.getString("pantry_sort_preference", "");
    	if(pantrySortPref.equals("alphabetical")) {
			pf.getPantry().alphabeticalSort();
			pf.createPantry(false);
    	}    	
    	else if(pantrySortPref.equals("expiration date")) {
			pf.getPantry().expirationSort();
			pf.createPantry(false);
    	}
    	
    	String shoppingListSortPref = sharedPref.getString("shopping_list_sort_preference", "");
    	if(shoppingListSortPref.equals("alphabetical")) {
    		slf.getShoppingList().alphabeticalSort();
    		slf.createShoppingList(false);
    	}    
    	if(shoppingListSortPref.equals("category")) {
    		slf.getShoppingList().categorySort();
    		slf.createShoppingList(false);
    	} 
    	
    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.shopping_cart_green)
    	        .setContentTitle("Shopping List updated")
    	        .setContentText(itemsUpdated);
    	// Creates an explicit intent for an Activity in your app
    	Intent resultIntent = new Intent(this, MainActivity.class);

    	// The stack builder object will contain an artificial back stack for the
    	// started Activity.
    	// This ensures that navigating backward from the Activity leads out of
    	// your application to the Home screen.
    	try{
    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    	// Adds the back stack for the Intent (but not the Intent itself)
    	stackBuilder.addParentStack(MainActivity.class);
    	// Adds the Intent that starts the Activity to the top of the stack
    	stackBuilder.addNextIntent(resultIntent);
    	PendingIntent resultPendingIntent =
    	        stackBuilder.getPendingIntent(
    	            0,
    	            PendingIntent.FLAG_UPDATE_CURRENT
    	        );
    	mBuilder.setContentIntent(resultPendingIntent);
    	}
    	finally{
    	NotificationManager mNotificationManager =
    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	// mId allows you to update the notification later on.
    	mNotificationManager.notify(RESULT_OK, mBuilder.build());
    	}
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_settings:
            	Intent intent = new Intent(this, SettingsActivity.class);
            	startActivityForResult(intent, REQUEST_EXIT);
            	/*
            	 * TODO: Add or update information about Pantry foodItem by checking Expiration dates
            	 * 		 and updating the shopping cart to green showing the user its added to the 
            	 * 		 shoppingList.
            	 */
            	
                return true;
            case R.id.action_recipe:
            	final Dialog recipeDialog = new Dialog(this);
            	recipeDialog.setTitle("Select Food Items");
            	recipeDialog.setContentView(R.layout.find_recipe);
            	PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");
            	final ListView lv = (ListView) recipeDialog.findViewById(R.id.recipe_list);
            	class FoodItemsAdapter extends ArrayAdapter<FoodItem> {
                    public FoodItemsAdapter(Activity activity, List<FoodItem> list) {
                       super(activity, R.layout.shopping_list_item, list);
                    }

                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                       // Get the data item for this position
                       final FoodItem foodItem = getItem(position);    
                       // Check if an existing view is being reused, otherwise inflate the view
                       if (convertView == null) {
                          convertView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_list_item, null);
                       }
                       
                       convertView.setOnClickListener(new OnClickListener(){
            				@Override
            				public void onClick(View v) {
            						((CheckableRelativeLayout) v).toggle();
            						if (((CheckableRelativeLayout) v).isChecked()) {
            							lv.setItemChecked(position, true);
            						} else {
            							lv.setItemChecked(position, false);
            						}
            				}
            	        });
                       
                       ImageView categoryImage = (ImageView) convertView.findViewById(R.id.category_image);
                        if (foodItem.getCategory() == FoodItemCategory.BEVERAGE) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.beverage));
                  		} else if (foodItem.getCategory() == FoodItemCategory.PROTEIN) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.protein));
                  		} else if (foodItem.getCategory() == FoodItemCategory.FRUIT) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.fruit));
                  		} else if (foodItem.getCategory() == FoodItemCategory.VEGETABLE) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.vegetable));
                  		} else if (foodItem.getCategory() == FoodItemCategory.DAIRY) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.dairy));
                  		} else if (foodItem.getCategory() == FoodItemCategory.FROZEN) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.frozen));
                  		} else if (foodItem.getCategory() == FoodItemCategory.CONDIMENT) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.condiment));
                  		} else if (foodItem.getCategory() == FoodItemCategory.SWEET) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.sweet));
                  		} else if (foodItem.getCategory() == FoodItemCategory.SNACK) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.snack));
                  		} else if (foodItem.getCategory() == FoodItemCategory.GRAIN) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.grain));
                  		} else if (foodItem.getCategory() == FoodItemCategory.FAT) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.fat));
                  		} else if (foodItem.getCategory() == FoodItemCategory.OTHER) {
                  			categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.other));
                  		} 
                       	TextView item = (TextView) convertView.findViewById(R.id.item);
                   		item.setText(foodItem.getName());
                   		TextView itemQuantity = (TextView) convertView.findViewById(R.id.item_quantity);
                        itemQuantity.setText(foodItem.getAmount() + " " + foodItem.getUnit());
                   		return convertView;
                    }
            	}
            	lv.setAdapter(new FoodItemsAdapter(this, pf.getPantry().getFoodItems()));
            	Button recipeButton = (Button) recipeDialog.findViewById(R.id.find_recipe);
            	recipeButton.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					SparseBooleanArray checked = lv.getCheckedItemPositions();
    					int size = lv.getAdapter().getCount()-1;
    					List<String> foodRecipeArray = new ArrayList<String>();
    					for(int i = size; i >= 0; i--) {
    						if (checked.get(i)) {
    							FoodItem foodToUse;
    								foodToUse = (FoodItem) lv.getAdapter().getItem(i);
    								foodRecipeArray.add(foodToUse.getName());
    								//((CheckBox) lv.getChildAt(i).findViewById(R.id.shopping_list_checkbox)).toggle();	
    						}
    					}
    					int i = 0;
    					String[] stringArr = new String[foodRecipeArray.size()];
    					for (String s : foodRecipeArray) {
    						stringArr[i++] = s;
    					}
    					RecipeArray recipeArray = suggestRecipes(stringArr);
    					recipeDialog.dismiss();
    					openRecipeResults(recipeArray);	
    				}
    			});
            	recipeDialog.show();
            	return true;
            case R.id.action_new:
            	addDialog = new Dialog(this);
            	if (getActionBar().getSelectedTab().getPosition() == 0) {
            		addDialog.setTitle("Add New Item");
            		addDialog.setContentView(R.layout.add_popup);
            		addDialog.show();
            		Button addButton = (Button) addDialog.findViewById(R.id.addButtonPantry);
                    addButton.setOnClickListener(new View.OnClickListener() {
				//		
						@Override
							public void onClick(View v) {
							boolean filled = false;
							 
							EditText nameText = (EditText) addDialog.findViewById(R.id.nameEdit);
						    EditText quantityText = (EditText) addDialog.findViewById(R.id.quantityEdit);			
							PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");
					    	Spinner categoryText = (Spinner) addDialog.findViewById(R.id.category_spinner);
					    	Spinner unitText = (Spinner) addDialog.findViewById(R.id.unit_spinner);
					    	DatePicker expirationDate = (DatePicker) addDialog.findViewById(R.id.dpResult);
					    	EditText priceText = (EditText) addDialog.findViewById(R.id.priceEdit);
						    View focusView = null;
						    
					    	
						    
						    if (!TextUtils.isEmpty(nameText.getText().toString()) && !TextUtils.isEmpty(quantityText.getText().toString())&& !TextUtils.isEmpty(priceText.getText().toString()))
						    	filled = true;
						    
							if (!filled){
								if(TextUtils.isEmpty(nameText.getText().toString())){
									nameText.setError(getString(R.string.error_field_required));
									focusView = nameText;
								}else if (TextUtils.isEmpty(quantityText.getText().toString())){
									quantityText.setError(getString(R.string.error_field_required));
									focusView = quantityText;									
								}else if (TextUtils.isEmpty(priceText.getText().toString())){
									priceText.setError(getString(R.string.error_field_required));
									focusView = priceText;									
								}
								
								focusView.requestFocus();
								
							}else{		
								String name = nameText.getText().toString();
						    	double quantity = Double.parseDouble(quantityText.getText().toString());
						    	String category = categoryText.getSelectedItem().toString();
						    	String unit = unitText.getSelectedItem().toString();
						    	Date expDate = new Date(expirationDate.getYear(), expirationDate.getMonth(), expirationDate.getDayOfMonth());
						    	double price = Double.parseDouble(priceText.getText().toString());
						    	for (FoodItemCategory fic : FoodItemCategory.values()) {
						    		if (fic.toString().equals(category)) {
						    			for (FoodItemUnit fiu : FoodItemUnit.values()) {
								    		if (fiu.toString().equals(unit)) {
								    			pf.addNewItem(new StandardFoodItem(name, 0, expDate, quantity, fiu, "y", fic,price));
								    			break;
								    		}
								    	}
						    		}
						    	}
						    					    		
								addDialog.dismiss();
							}
						}
					});
                    Button barcodeButton = (Button) addDialog.findViewById(R.id.barcodeButton);
                    barcodeButton.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
							scanIntegrator.initiateScan();
						}
                    	
                    });
            	} else if (getActionBar().getSelectedTab().getPosition() == 1) {
            		addDialog.setContentView(R.layout.add_popup_shoppinglist);
                	addDialog.setTitle("Add New Item");
                    addDialog.show();
                    Button addButton = (Button) addDialog.findViewById(R.id.addButton);
                    addButton.setOnClickListener(new View.OnClickListener() {
									
                    	@Override
						public void onClick(View v) {
							boolean filled = false;
							
                    		ShoppingListFragment slf = (ShoppingListFragment) getFragmentManager().findFragmentByTag("Shopping List");
							EditText nameText = (EditText) addDialog.findViewById(R.id.nameEdit);
							EditText quantityText = (EditText) addDialog.findViewById(R.id.quantityEdit);
							Spinner categoryText = (Spinner) addDialog.findViewById(R.id.category_spinner);
					    	Spinner unitText = (Spinner) addDialog.findViewById(R.id.unit_spinner);
					    	EditText priceText = (EditText) addDialog.findViewById(R.id.priceEdit);
					    	View focusView = null;
					    	
					    	if (!TextUtils.isEmpty(nameText.getText().toString()) && !TextUtils.isEmpty(quantityText.getText().toString())&& !TextUtils.isEmpty(priceText.getText().toString()))
						    	filled = true;
						    
							if (!filled){
								if(TextUtils.isEmpty(nameText.getText().toString())){
									nameText.setError(getString(R.string.error_field_required));
									focusView = nameText;
								}else if (TextUtils.isEmpty(quantityText.getText().toString())){
									quantityText.setError(getString(R.string.error_field_required));
									focusView = quantityText;									
								}else if (TextUtils.isEmpty(priceText.getText().toString())){
									priceText.setError(getString(R.string.error_field_required));
									focusView = priceText;									
								}
								
								focusView.requestFocus();
								
							}else{		
								String name = nameText.getText().toString();
						    	double quantity = Double.parseDouble(quantityText.getText().toString());
								String category = categoryText.getSelectedItem().toString();
						    	String unit = unitText.getSelectedItem().toString();
						    	double price = Double.parseDouble(priceText.getText().toString());
								for (FoodItemCategory fic : FoodItemCategory.values()) {
						    		if (fic.toString().equals(category)) {
						    			for (FoodItemUnit fiu : FoodItemUnit.values()) {
								    		if (fiu.toString().equals(unit)) {
								    			slf.addNewItem(new StandardFoodItem(name, 0, new Date(), quantity, fiu, "y", fic,price));
								    			break;
								    		}
								    	}
						    		}
						    	}
								addDialog.dismiss();
                    		}
                    	}
					});
                    Button barcodeButton = (Button) addDialog.findViewById(R.id.barcodeButton);
                    barcodeButton.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
							scanIntegrator.initiateScan();
						}
                    	
                    });
                }
            	return true;
            case R.id.action_share:
            	final Dialog shareDialog = new Dialog(this);
            	shareDialog.setContentView(R.layout.share_popup);
            	shareDialog.setTitle("Share Your Pantry");
            	shareDialog.show();
            	/*
            	 * Add code to Emulate a pantry so we can
            	 * test how the share pantry should work
            	 * if the Server is not up and running.
            	 */
            	return true;
           // default:
             //   return super.onOptionsItemSelected(item);
                
                
            case R.id.action_sort:
            	final Dialog sortDialog = new Dialog(this);
            	sortDialog.setContentView(R.layout.sortby_popup);
            	sortDialog.setTitle("Sorting Method");
            	sortDialog.show();
            	sortByRadioButtonListener(sortDialog);
            	return true;
            case R.id.action_help:
            	final Dialog helpDialog = new Dialog(this);
            	helpDialog.setContentView(R.layout.help_popup);
            	helpDialog.setTitle("Help");
            	helpDialog.show();
            	return true;
            	
            default:
                return super.onOptionsItemSelected(item);
        
        }
    } // :)
    
    public void updateSearch() {
    	SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (getActionBar().getSelectedTab().getPosition() == 0)
        	searchView.setQueryHint("Search Pantry");
        else if (getActionBar().getSelectedTab().getPosition() == 1)
        	searchView.setQueryHint("Search Shopping List");
    }
    
    
    //////////////////////////////////////////////////////////////
    
 public void sortByRadioButtonListener(final Dialog sortDialog){
    	
    	sortByMenu = (RadioGroup) sortDialog.findViewById(R.id.sortByRadioGroup);
    	 sortByOkButton =(Button) sortDialog.findViewById(R.id.SortByButton);
    	
    	sortByOkButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");
				// TODO Auto-generated method stub
				int sortById = sortByMenu.getCheckedRadioButtonId();
			//	sortByRadioButton = (RadioButton) findViewById(sortById);
				
				if(getActionBar().getSelectedTab().getPosition() == 0){
					if(sortById==R.id.alphabetical){
						pf.getPantry().alphabeticalSort();
					}
					else if(sortById==R.id.categoryCode){
						pf.getPantry().categorySort();
					}
					else if(sortById==R.id.expirationDate){
						pf.getPantry().expirationSort();
					}
					pf.createPantry(false);
				}
				else if(getActionBar().getSelectedTab().getPosition() == 1){
					ShoppingListFragment slf = (ShoppingListFragment) getFragmentManager().findFragmentByTag("Shopping List");
					if(sortById==R.id.alphabetical){
						slf.getShoppingList().alphabeticalSort();
						slf.createShoppingList(false);
					}
					else if(sortById==R.id.categoryCode){
						slf.getShoppingList().categorySort();
						slf.createShoppingList(false);
					}
				}
				sortDialog.dismiss();
			}
    	});
    }
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == REQUEST_EXIT){
			if (resultCode == RESULT_OK){
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		    	startActivity(intent);
				this.finish();
			}
		} else if (requestCode == IntentIntegrator.REQUEST_CODE) {
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
			if (scanningResult != null) {
				//we have a result
				String scanContent = scanningResult.getContents();
				String scanFormat = scanningResult.getFormatName();
				
				BarCodeParser barCodeParser = new BarCodeParser();
				Table table = null;
				
				DownloadWebPageTask task = new DownloadWebPageTask();
				String xmlText = null;
				try {
					xmlText = task.execute(new String[] { "http://www.upcdatabase.com/item/"+scanContent }).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					if(xmlText != "" && xmlText != null)
						table = barCodeParser.parse(xmlText);
				
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), table != null ? table.description : "", Toast.LENGTH_LONG).show();
				EditText nameText = (EditText) addDialog.findViewById(R.id.nameEdit);
				EditText quantityText = (EditText) addDialog.findViewById(R.id.quantityEdit);
				Spinner unitText = (Spinner) addDialog.findViewById(R.id.unit_spinner);
				String description = table != null ? table.description : "null";
				String size = table != null ? table.size : "0";
				nameText.setText(table != null ? table.description : "null");
				quantityText.setText(table != null ? table.size : "0");
				unitText.setSelection(table != null ? table.foodItemUnit.ordinal() : FoodItemUnit.UNITLESS.ordinal());
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}
    
    
    ///////////////////////////////////////////////////////////
	public RecipeArray suggestRecipes(String... foodItems) {
    	String url = "http://food2fork.com/api/search?key=a2490ddc95bd1869db2b2d2f17133530&count=10&q=";
    	for(int i = 0 ; i < foodItems.length; i++) {
    		//item.replaceAll("\\s", "%20");
    		//item += ",";
    		foodItems[i] = foodItems[i].replaceAll("\\s", "%20");
    		url += foodItems[i];
    		url += ",";
    		//url += item;
    		//url += ",";
    	}
    	//url = url.replaceAll("\\s", "%20");
    	Toast.makeText(getApplicationContext(), "Searching for recipes...", Toast.LENGTH_LONG).show();
    	DownloadWebPageTask task = new DownloadWebPageTask();
    	String jsonDoc = null;
    	try {
    		jsonDoc = task.execute( new String[]{url} ).get();
    	} catch(InterruptedException e) {
    		e.printStackTrace();
    	} catch(ExecutionException e) {
    		e.printStackTrace();
    	}
    	RecipeArray recipes;
    	if (jsonDoc.equals("{\"count\": 0, \"recipes\": []}")) {
    		recipes = null;
    	} else {
    		recipes = new RecipeArray();
    		recipes.parse(jsonDoc);
    	}
    	return recipes;
	}
    
    public void openRecipeResults(RecipeArray recipeArray) {
    	class FoodItemsAdapter extends ArrayAdapter<Recipe> {
            public FoodItemsAdapter(Activity activity, List<Recipe> list) {
               super(activity, R.layout.recipe_item, list);
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
               // Get the data item for this position
               final Recipe recipe = getItem(position);    
               // Check if an existing view is being reused, otherwise inflate the view
               if (convertView == null) {
                  convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_item, null);
               }
               
               convertView.setOnClickListener(new OnClickListener(){
    				@Override
    				public void onClick(View v) {
    					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipe.getF2f_url()));
    					startActivity(browserIntent);	
    				}
    	        });
               	TextView recipeNameText = (TextView) convertView.findViewById(R.id.recipe_name);
           		recipeNameText.setText(recipe.getName());
           		ImageView recipeImageView = (ImageView) convertView.findViewById(R.id.recipe_image);
           		new DownloadImageTask(recipeImageView).execute(recipe.getImg_url());
           		return convertView;
            }
    	}
    	if (recipeArray != null) {
    		Dialog recipeResultsDialog = new Dialog(this);
    		recipeResultsDialog.setTitle("Recipe Results");
        	recipeResultsDialog.setContentView(R.layout.recipe_results);
    		final ListView lv = (ListView) recipeResultsDialog.findViewById(R.id.recipe_results_list);
    		lv.setAdapter(new FoodItemsAdapter(this, ((List<Recipe>)recipeArray.getRecipeArray())));
    		recipeResultsDialog.show();
    	} else {
    		Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_LONG).show();
    	}
    }
    
    
    
    
    
    
}


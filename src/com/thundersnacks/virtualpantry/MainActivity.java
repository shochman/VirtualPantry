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
import com.thundersnacks.virtualpantrymodel.StandardFoodItem;
import com.thundersnacks.zxing.IntentIntegrator;
import com.thundersnacks.zxing.IntentResult;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
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
    			if (food.getName().equals(query)) {	
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
    			if(food.getName().equals(query)) {
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
    	NotificationManager mNotificationManager =
    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	// mId allows you to update the notification later on.
    	mNotificationManager.notify(RESULT_OK, mBuilder.build());
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
              
            	String query = getIntent().getStringExtra(SearchManager.QUERY);
    			//use the query to search pantry data
    		//	FoodItemCategory[] categoryList = FoodItemCategory.values();

    			PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");

    			List<FoodItem> foodItem = pf.getPantry().getFoodItems();
    			List<FoodItem> foodResults = new ArrayList<FoodItem>();
    			for(FoodItem food : foodItem){
    				if (food.getName().equals(query))
    				{	
    					foodResults.add(food);
    				}
    			}
            	
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
						    
					    	
						    
						    if (!TextUtils.isEmpty(nameText.getText().toString()) && !TextUtils.isEmpty(quantityText.getText().toString()))
						    	filled = true;
						    
							if (!filled){
								if(TextUtils.isEmpty(nameText.getText().toString())){
									nameText.setError(getString(R.string.error_field_required));
									focusView = nameText;
								}else if (TextUtils.isEmpty(quantityText.getText().toString())){
									quantityText.setError(getString(R.string.error_field_required));
									focusView = quantityText;									
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
					    	
					    	if (!TextUtils.isEmpty(nameText.getText().toString()) && !TextUtils.isEmpty(quantityText.getText().toString()))
						    	filled = true;
						    
							if (!filled){
								if(TextUtils.isEmpty(nameText.getText().toString())){
									nameText.setError(getString(R.string.error_field_required));
									focusView = nameText;
								}else if (TextUtils.isEmpty(quantityText.getText().toString())){
									quantityText.setError(getString(R.string.error_field_required));
									focusView = quantityText;									
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
		}
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
    
    
    ///////////////////////////////////////////////////////////
    
    
    
    
    
    
    
}


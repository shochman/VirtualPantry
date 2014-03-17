package com.thundersnacks.virtualpantry;

import java.util.Collections;
import java.util.Date;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;
import com.thundersnacks.virtualpantrymodel.StandardFoodItem;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
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

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	RadioGroup sortByMenu;
	Button sortByOkButton;
	RadioButton sortByRadioButton;
	
	
	
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
	        }
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
	}
	
	Menu menu;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search Pantry");
        this.menu = menu;
        firstOpen = true;
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                //Handle search
                return true;
            case R.id.action_settings:
            	Intent intent = new Intent(this, SettingsActivity.class);
            	startActivity(intent);
            	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            	boolean defValue = false;
            	boolean checkedAuto = sharedPref.getBoolean("auto_add", defValue);
            	/*
            	 * TODO: Add or update information about Pantry foodItem by checking Expiration dates
            	 * 		 and updating the shopping cart to green showing the user its added to the 
            	 * 		 shoppingList.
            	 */
            	
                return true;
            case R.id.action_new:
            	final Dialog addDialog = new Dialog(this);
            	if (getActionBar().getSelectedTab().getPosition() == 0) {
            		addDialog.setTitle("Add New Item");
            		addDialog.setContentView(R.layout.add_popup);
            		addDialog.show();
            		Button addButton = (Button) addDialog.findViewById(R.id.addButtonPantry);
                    addButton.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							PantryFragment pf = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");
							EditText nameText = (EditText) addDialog.findViewById(R.id.nameEdit);
					    	EditText quantityText = (EditText) addDialog.findViewById(R.id.quantityEdit);
					    	Spinner categoryText = (Spinner) addDialog.findViewById(R.id.category_spinner);
					    	DatePicker expirationDate = (DatePicker) addDialog.findViewById(R.id.dpResult);
					    	String name = nameText.getText().toString();
					    	String quantity = quantityText.getText().toString();
					    	String category = categoryText.getSelectedItem().toString();
					    	Date expDate = new Date(expirationDate.getYear(), expirationDate.getMonth(), expirationDate.getDayOfMonth());
					    	for (FoodItemCategory fic : FoodItemCategory.values()) {
					    		if (fic.toString().equals(category)) {
					    			pf.addNewItem(new StandardFoodItem(name, 0, expDate, quantity, "y", fic));
					    			break;
					    		}
					    	}
							addDialog.dismiss();
						}
					});
            	}
                else if (getActionBar().getSelectedTab().getPosition() == 1) {
                	View menuItemView = findViewById(R.id.action_new);
                	PopupMenu popupMenu = new PopupMenu(this, menuItemView);
                	popupMenu.inflate(R.menu.shoppinglist_add_options);
                	popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch(item.getItemId()) {
                			case R.id.action_new_item:
                				addDialog.setContentView(R.layout.add_popup_shoppinglist);
                				addDialog.setTitle("Add New Item");
                                addDialog.show();
                                Button addButton = (Button) addDialog.findViewById(R.id.addButton);
                                addButton.setOnClickListener(new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										
										ShoppingListFragment slf = (ShoppingListFragment) getFragmentManager().findFragmentByTag("Shopping List");
										EditText nameText = (EditText) addDialog.findViewById(R.id.nameEdit);
								    	EditText quantityText = (EditText) addDialog.findViewById(R.id.quantityEdit);
								    	Spinner categoryText = (Spinner) addDialog.findViewById(R.id.category_spinner);
								    	String name = nameText.getText().toString();
								    	String quantity = quantityText.getText().toString();
								    	String category = categoryText.getSelectedItem().toString();
								    	for (FoodItemCategory fic : FoodItemCategory.values()) {
								    		if (fic.toString().equals(category)) {
								    			slf.addNewItem(new StandardFoodItem(name, 0, new Date(), quantity, "y", fic ));
								    			break;
								    		}
								    	}
										addDialog.dismiss();
									}
								});

                                return true;
                			case R.id.action_add_from_pantry:
                				addDialog.setContentView(R.layout.add_popup_shoppinglist_frompantry);
                				addDialog.setTitle("Add Item(s) From Pantry");
                				PantryFragment pantryFrag = (PantryFragment) getFragmentManager().findFragmentByTag("Pantry");
                				PantryFragment.SavedTabsListAdapter savedTabs = pantryFrag.new SavedTabsListAdapter();
                				
                				String[] pantryString = new String[savedTabs.getGroupCount()];
                				for(int i = 0; i < pantryString.length; i++)
                					pantryString[i] = (String) savedTabs.getGroup(i);
                				
                				ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_multiple_choice, pantryString);
                		        
                		        // Getting the reference to the listview object of the layout
                		        ListView listView = (ListView) addDialog.findViewById(R.id.PopupListView);
                		        listView.setTextFilterEnabled(true);
                		        // Setting adapter to the listview
                		        listView.setAdapter(adapter);
                		        
                                addDialog.show();
                                return true;
                			}
							return false;
						}
                	});
                	popupMenu.show();
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
					pf.createPantry();
				}
				else if(getActionBar().getSelectedTab().getPosition() == 1){
					ShoppingListFragment slf = (ShoppingListFragment) getFragmentManager().findFragmentByTag("Shopping List");
					if(sortById==R.id.alphabetical){
						slf.getShoppingList().alphabeticalSort();
						slf.createShoppingList();
					}
					else if(sortById==R.id.categoryCode){
						slf.getShoppingList().categorySort();
						slf.createShoppingList();
					}
				}
				sortDialog.dismiss();
			}
    	});
    }
    
    
    
    
    ///////////////////////////////////////////////////////////
    
    
    
    
    
    
    
}

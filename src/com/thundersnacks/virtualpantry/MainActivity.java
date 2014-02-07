package com.thundersnacks.virtualpantry;

import com.thundersnacks.virtualpantry.PantryFragment.SavedTabsListAdapter;
import com.thundersnacks.virtualpantry.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;


@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
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
                return true;
            case R.id.action_new:
            	final Dialog addDialog = new Dialog(this);
                addDialog.setContentView(R.layout.add_popup);
                addDialog.setTitle("Add New Item");
                addDialog.show();
    		    return true;
            case R.id.action_share:
            	final Dialog shareDialog = new Dialog(this);
            	shareDialog.setContentView(R.layout.share_popup);
            	shareDialog.setTitle("Share Your Pantry");
            	shareDialog.show();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void updateSearch() {
    	SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (getActionBar().getSelectedTab().getPosition() == 0)
        	searchView.setQueryHint("Search Pantry");
        else if (getActionBar().getSelectedTab().getPosition() == 1)
        	searchView.setQueryHint("Search Shopping List");
    }
 
}

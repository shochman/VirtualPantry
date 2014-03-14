package com.thundersnacks.virtualpantry;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.*;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Pieced together from:
 * http://stackoverflow.com/questions/13811023/fragment-onlistitemclick
 * http://www.yogeshblogspot.com/how-to-get-selected-items-from-multi-select-list-view/
 * 
 */
public class ShoppingListFragment extends Fragment implements OnItemClickListener{
	private ListView mainListView = null;
	String[] foodString;
	private ShoppingList shoppingList;
	View view;
	FoodItem food;
	ListView itemListView;
	static List<String> foodItems;
	public PantryFragment pf;

	public ShoppingListFragment() {
		this.shoppingList = new ShoppingList();
	}
	
	@Override
	public void onItemClick(
	    		AdapterView<?> parent, View v, int position, long id)
	    		{
	    		//---toggle the check displayed next to the item---
	    	    String s="";
	    		int len =parent.getCount();
	    		SparseBooleanArray checked=((AbsListView) parent).getCheckedItemPositions();
	    		for (int i = 0; i < len; i++)
	    			 if (checked.get(i)) {
	    			  String item = foodString[i];
	    			  s=s+" "+item;
	    			  /* do whatever you want with the checked item */
	    			  System.out.println(item);
	    			 }
	    		Toast.makeText(this.getActivity(),"Selected Items- " + s,Toast.LENGTH_SHORT).show();
	    		}  
	  
	@Override
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        shoppingList = new ShoppingList();
        shoppingList.addItem(new StandardFoodItem("Cheddar Cheese",0,new Date(),"1oz","y", FoodItemCategory.DAIRY));
        shoppingList.addItem(new StandardFoodItem("Strawberry Milk",1,new Date(),"16oz"," ", FoodItemCategory.DAIRY));
        shoppingList.addItem(new StandardFoodItem("Oatmeal",2,new Date(),"6oz"," ", FoodItemCategory.GRAIN));
        shoppingList.addItem(new StandardFoodItem("M&M's",3,new Date(),"3.37lb"," ", FoodItemCategory.SWEET));
        shoppingList.addItem(new StandardFoodItem("Frozen yogurt",4,new Date(),"4.2oz"," ", FoodItemCategory.FROZEN));
        shoppingList.addItem(new StandardFoodItem("Avacados",5,new Date(),"5oz"," ", FoodItemCategory.FAT)); 
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.shopping_list, container, false);
        pf = (PantryFragment) getActivity().getFragmentManager().findFragmentByTag("Pantry");
        createShoppingList();
        return view;
    }
	
	public void createShoppingList() {
			final Map<FoodItem, Boolean> foodMap = shoppingList.sortByComparator(shoppingList.getItems());
	        final String[] foodString = new String[foodMap.size()];
	        foodItems=Arrays.asList(foodString);
	        int ipos = 0;
	        for (TreeMap.Entry<FoodItem, Boolean> e : foodMap.entrySet())
	            foodString[ipos++] = e.getKey().getName();
	        
	        // The checkbox for the each item is specified by the layout android.R.layout.simple_list_item_multiple_choice
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, foodString);
	        adapter.sort(new Comparator<String>(){
	        	public int compare(String object1,String object2) {
	        		return object1.compareToIgnoreCase(object2);
	        	};
	        });
	        // Getting the reference to the listview object of the layout
	        ListView listView = (ListView) view.findViewById(R.id.listview);
	        listView.setTextFilterEnabled(true);
	        // Setting adapter to the listview
	        listView.setAdapter(adapter);
	        /*
	         * Find a way to get the AdapterView to our desired method
	         *///
	        listView.setOnItemClickListener(new OnItemClickListener(){
	        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
	        		//---toggle the check displayed next to the item---
		    	    String s="";
		    		int len =parent.getCount();
		    		SparseBooleanArray checked=((AbsListView) parent).getCheckedItemPositions();
		    		((AbsListView) parent).getCheckedItemCount();
		    		
		    		if (position < len)
		    			 if (checked.get(position)) {
		    			  String item = foodString[position];
		    			  s=item;
		    			  /*
			    			 * The checked Item will be updated to the Item attribute so that it can be 
			    			 * added to the pantry and removed from the shoppingList.
			    			 */
		    			 }
		    		if(s != "")
		    		{
		    			Toast.makeText(ShoppingListFragment.this.getActivity(),"Selected Items- " + s,Toast.LENGTH_SHORT).show();
		    			
		    		}
		    		
	        	}
	        });
	        
	     // Create a ListView-specific touch listener. ListViews are given special treatment because
	        // by default they handle touches for their list items... i.e. they're in charge of drawing
	        // the pressed state (the list selector), handling list item clicks, etc.
	        SwipeDismissListViewTouchListener touchListener =
	                new SwipeDismissListViewTouchListener(
	                        listView,
	                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
	                            @Override
	                            public boolean canDismiss(int position) {
	                                return true;
	                            }

	                            @Override
	                            public void onDismiss(ListView listView, int[] reverseSortedPositions, boolean dismissRight) {
	                                for (int position : reverseSortedPositions) {
	                                	for (Iterator<Map.Entry<FoodItem, Boolean>> it = foodMap.entrySet().iterator(); it.hasNext(); ) {
	                                		Entry entry = (Entry)it.next();
                                    		FoodItem test = (FoodItem)entry.getKey();
                                    		if (test.getName().equals(itemListView.getAdapter().getItem(position))) {
                                    			food = test;
                                    			break;
                                    		}
                                    	}
	                                	shoppingList.removeItemByName((String)listView.getAdapter().getItem(position));
	                                    createShoppingList();
	                                }
	                            }
	                        });
	        listView.setOnTouchListener(touchListener);
	        // Setting this scroll listener is required to ensure that during ListView scrolling,
	        // we don't look for swipes.
	        listView.setOnScrollListener(touchListener.makeScrollListener());
	        
	        Button addToPantryButton = (Button) view.findViewById(R.id.addToPantryButton);
	        addToPantryButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String s="";
					SparseBooleanArray checked = itemListView.getCheckedItemPositions();
					int len = itemListView.getCheckedItemCount();
					int position = 0;
					int key = 0;
					while (position < len){ 
						 key = checked.keyAt(position);
		    			 if (checked.get(key)) {
		    			  String item = foodString[key];
		    			  s=item;
		    			  /*
			    			 * The checked Item will be updated to the Item attribute so that it can be 
			    			 * added to the pantry and removed from the shoppingList.
			    			 */
		    			  if(s != "")
		    			  	{
				    			FoodItem foodItem = shoppingList.getItem(s);
				    			shoppingList.setItemMapValue(foodItem, false);
			    			}
		    			 }
		    			 position++;
					}
					updatePantry();
				}
			});
	        itemListView = listView;
	}
    
    public void addNewItem(FoodItem fi)
    {
    	if( !(fi.getName().equals("") || fi.getAmount().equals("")) )
        {
    		shoppingList.addItem(fi);
    		createShoppingList();
        } 
    }
    
    public void updatePantry()
    {
    	PantryFragment pantryFrag = (PantryFragment) this.getActivity().getFragmentManager().findFragmentByTag("Pantry");
		StandardFoodItem itemToAdd;
		while( (itemToAdd = (StandardFoodItem) shoppingList.getCheckedFoodItem()) != null)
			pantryFrag.getPantry().addItem(itemToAdd);
		createShoppingList(); 
    }
    
    public boolean isInShoppingList(FoodItem food) {
    	return shoppingList.isInShoppingList(food);
    }
}
package com.thundersnacks.virtualpantry;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

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
        shoppingList.addItem(new StandardFoodItem("Cheese",0,new Date(),"r","y", FoodItemCategory.DAIRY));
        shoppingList.addItem(new StandardFoodItem("Milk",1,new Date(),"","", FoodItemCategory.DAIRY));
        shoppingList.addItem(new StandardFoodItem("Cereal",2,new Date(),"","", FoodItemCategory.GRAIN));
        shoppingList.addItem(new StandardFoodItem("Cookies",3,new Date(),"","", FoodItemCategory.SWEET));
        shoppingList.addItem(new StandardFoodItem("Ice Cream",4,new Date(),"","", FoodItemCategory.FROZEN));
        shoppingList.addItem(new StandardFoodItem("Butter",5,new Date(),"","", FoodItemCategory.FAT)); 
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.shopping_list, container, false);
        createShoppingList();
        return view;
    }
	
	public void createShoppingList() {
		 Map<FoodItem, Boolean> food = ShoppingList.getItems();
	        final String[] foodString = new String[food.size()];
	        int ipos = 0;
	        for (Map.Entry<FoodItem, Boolean> e : food.entrySet())
	            foodString[ipos++] = e.getKey().getName();
	        
	       
	        //String[] foodString = {"Cheese", "Milk", "Cereal", "Cookies", "Ice Cream", "Milk", "Butter"};
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
	        //AbsListView chec = ((AbsListView) ((AdapterView<?>) adapter).findFocus();
	        /*
	         * Find a way to get the AdapterView to our desired method
	         */
	        listView.setOnItemClickListener(new OnItemClickListener(){
	        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
	        		//---toggle the check displayed next to the item---
		    	    String s="";
		    		int len =parent.getCount();
		    		SparseBooleanArray checked=((AbsListView) parent).getCheckedItemPositions();
		    		if (position < len)
		    			 if (checked.get(position)) {
		    			  String item = foodString[position];
		    			  s=item;
		    			  /* do whatever you want with the checked item */
		    			 }
		    		
		    		((AbsListView) parent).setItemChecked(position, false); // remove when the update button is added
		    		//parent.updateViewLayout(view, ViewGroup new ViewGroup());
		    		if(s != "")
		    		{
		    			/*
		    			 * The checked Item with be updated to the Item so that it can be 
		    			 * added to the pantry and removed from the shoppingList.
		    			 */
		    			FoodItem foodItem = shoppingList.getItem(s);
		    			shoppingList.setItemMapValue(foodItem, false);
		    			updatePantry(); // remove when the update button is added
		    			
		    			Toast.makeText(ShoppingListFragment.this.getActivity(),"Selected Items- " + s,Toast.LENGTH_SHORT).show();
		    			createShoppingList(); 
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
	                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
	                                for (int position : reverseSortedPositions) {
	                                	
	                                	//String foo = (String)listView.getAdapter().getItem(position);
	                                	//shoppingList.removeItemByName(foo);
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
					
				}
			});
	}
    
    public void addNewItem(Dialog addDialog)
    {
    	EditText nameText = (EditText) addDialog.findViewById(R.id.nameEdit);
    	EditText quantityText = (EditText) addDialog.findViewById(R.id.quantityEdit);
    	Spinner categoryText = (Spinner) addDialog.findViewById(R.id.category_spinner);
    	String name = nameText.getText().toString();
    	String quantity = quantityText.getText().toString();
    	String category = categoryText.toString();
    	if( !(name.equals("") || quantity.equals("")) )
        {
    		shoppingList.addItem(new StandardFoodItem(name,0,new Date(),quantity,"y", FoodItemCategory.BEVERAGE ));
    		createShoppingList();
        }
    }
    
    public void updatePantry()
    {
    	PantryFragment pantryFrag = (PantryFragment) this.getActivity().getFragmentManager().findFragmentByTag("Pantry");
    		StandardFoodItem itemToAdd;
    		while( (itemToAdd = (StandardFoodItem) shoppingList.getCheckedFoodItem()) != null)
    			pantryFrag.getPantry().addItem(itemToAdd);
    }
    
}

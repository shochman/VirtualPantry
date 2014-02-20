package com.thundersnacks.virtualpantry;

import java.util.Date;
import java.util.Map;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.ShoppingList;
import com.thundersnacks.virtualpantrymodel.StandardFoodItem;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	private ShoppingList shoppingList;
	String[] foodString;

	public ShoppingListFragment()
	{
		this.shoppingList = new ShoppingList();
	}
	public void setShoppingList(ShoppingList shoppingList)
	{
		this.shoppingList = shoppingList;
	}
	
	public static void addItemButton(View v)
	{
		System.out.println("addButton Pressed!!");
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
        shoppingList.addItem(new StandardFoodItem("Cheese",0,new Date(),"r","y", null));
        shoppingList.addItem(new StandardFoodItem("Milk",1,new Date(),"","", null));
        shoppingList.addItem(new StandardFoodItem("Cereal",2,new Date(),"","", null));
        shoppingList.addItem(new StandardFoodItem("Cookies",3,new Date(),"","", null));
        shoppingList.addItem(new StandardFoodItem("Ice Cream",4,new Date(),"","", null));
        shoppingList.addItem(new StandardFoodItem("Butter",5,new Date(),"","", null)); 
        
        Button addItemButton = (Button) this.getActivity().findViewById(R.id.addButton);
        addItemButton.setOnClickListener((OnClickListener) this);
    }
	//public void onClick(View v){
	//	switch (v.getId()){
    //    		System.out.println("add pressed... ok!!");
    //    	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shopping_list, container, false);
        
        Map<FoodItem, Boolean> food = ShoppingList.getItems();
        final String[] foodString = new String[food.size()];
        int ipos = 0;
        for (Map.Entry<FoodItem, Boolean> e : food.entrySet())
            foodString[ipos++] = e.getKey().getName();
        
       
        //String[] foodString = {"Cheese", "Milk", "Cereal", "Cookies", "Ice Cream", "Milk", "Butter"};
        // The checkbox for the each item is specified by the layout android.R.layout.simple_list_item_multiple_choice
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, foodString);
        
        // Getting the reference to the listview object of the layout
        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setTextFilterEnabled(true);
        // Setting adapter to the listview
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		//---toggle the check displayed next to the item---
	    	    String s="";
	    		int len =parent.getCount();
	    		SparseBooleanArray checked=((AbsListView) parent).getCheckedItemPositions();
	    		for (int i = 0; i < len; i++)
	    			 if (checked.get(i)) {
	    			  String item = foodString[i];
	    			  s=s+" "+item;
	    			  /* do whatever you want with the checked item */
	    			 }
	    		if(s != "")
	    			Toast.makeText(ShoppingListFragment.this.getActivity(),"Selected Items- " + s,Toast.LENGTH_SHORT).show();
        	}
        });
        return view;
    }
    
    public void addNewItem(Dialog addDialog)
    {
    	
    	EditText nameText = (EditText) addDialog.findViewById(R.id.nameEdit);
    	EditText quantityText = (EditText) addDialog.findViewById(R.id.quantityEdit);
    	Spinner categoryText = (Spinner) addDialog.findViewById(R.id.category_spinner);
    	String name = nameText.getText().toString();
    	String quantity = quantityText.getText().toString();
    	String category = categoryText.toString();
    	
    	
    }
    
}

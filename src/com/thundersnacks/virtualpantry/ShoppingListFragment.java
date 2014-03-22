package com.thundersnacks.virtualpantry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantry.PantryFragment.ExpandableListAdapter;
import com.thundersnacks.virtualpantry.PantryFragment.FoodItemsAdapter;
import com.thundersnacks.virtualpantrymodel.*;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Pieced together from:
 * http://stackoverflow.com/questions/13811023/fragment-onlistitemclick
 * http://www.yogeshblogspot.com/how-to-get-selected-items-from-multi-select-list-view/
 * 
 */

public class ShoppingListFragment extends Fragment {
	
	private ShoppingList shoppingList;
	View view;
	ExpandableListView elv;
	ListView lv;
	public PantryFragment pf;
	
	public ShoppingListFragment() {
		shoppingList = null;
	}

	public void setShoppingList(ShoppingList shoppingList) {
		this.shoppingList = shoppingList;
	}
	
	public ShoppingList getShoppingList() {
		return shoppingList;
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
        shoppingList.alphabeticalSort();
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shopping_list, null);
        pf = (PantryFragment) getActivity().getFragmentManager().findFragmentByTag("Pantry");
		elv = (ExpandableListView) view.findViewById(R.id.shopping_list_expandable_list);
		elv.setAdapter(new ExpandableListAdapter(getActivity(), shoppingList.getFoodItems()));
        lv = (ListView) view.findViewById(R.id.shopping_list_list);
        lv.setAdapter(new FoodItemsAdapter(getActivity(), shoppingList.getFoodItems()));
        createShoppingList();
        Button addToPantryButton = (Button) view.findViewById(R.id.addToPantryButton);
	    addToPantryButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SparseBooleanArray checked;
					int size;
					if (lv.getVisibility() == View.VISIBLE) {
						checked = lv.getCheckedItemPositions();
						size = lv.getAdapter().getCount()-1;
					} else {
						checked = elv.getCheckedItemPositions();
						size = elv.getAdapter().getCount()-1;
					}
					for(int i = size; i >= 0; i--) {
						if (checked.get(i)) {
							FoodItem foodToRemove;
							if (lv.getVisibility() == View.VISIBLE) {
								foodToRemove = (FoodItem) lv.getAdapter().getItem(i);
								lv.setItemChecked(i, false);
								((CheckBox) lv.getChildAt(i).findViewById(R.id.shopping_list_checkbox)).toggle();
							} else {
 								foodToRemove = (FoodItem) elv.getAdapter().getItem(i);
								elv.setItemChecked(i, false);
			               		((CheckBox) elv.getChildAt(i).findViewById(R.id.shopping_list_checkbox)).toggle();
							}
		               		((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
		               		pf.addNewItem(foodToRemove);
		               		shoppingList.removeItem(foodToRemove);
						}
					}
            		((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
            		((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
				}
			});
        return view;
    }
	
	public void createShoppingList() {
		/**if (shoppingList.getHowSorted() == 0) {
			lv.setVisibility(View.INVISIBLE);
			elv.setVisibility(View.VISIBLE);
			elv.invalidateViews();
		} else {*/
			elv.setVisibility(View.INVISIBLE);
			lv.setVisibility(View.VISIBLE);
			lv.invalidateViews();
		//}
	}
	
	public class FoodItemsAdapter extends ArrayAdapter<FoodItem> {
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
           
           // Create a ListView-specific touch listener. ListViews are given special treatment because
           // by default they handle touches for their list items... i.e. they're in charge of drawing
           // the pressed state (the list selector), handling list item clicks, etc.
           final SwipeDismissListViewTouchListener touchListener =
                   new SwipeDismissListViewTouchListener(
                           lv,
                           new SwipeDismissListViewTouchListener.DismissCallbacks() {
                               @Override
                               public boolean canDismiss(int position) {
                                   return true;
                               }

                               @Override
                               public void onDismiss(ListView listView, int[] reverseSortedPositions, boolean dismissRight) {
                                   for (int position : reverseSortedPositions) {
                                   		FoodItem foodToRemove = ((FoodItem)lv.getAdapter().getItem(position));
                                   		shoppingList.removeItem(foodToRemove);
                                   		((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
                                   		((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
                                   		((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
                                   	}
                               	}
                           });
           convertView.setOnTouchListener(touchListener);
    
           TextView item = (TextView) convertView.findViewById(R.id.item);
           
           convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (touchListener.getAllowClick()){
						((CheckableRelativeLayout) v).toggle();
						if (((CheckableRelativeLayout) v).isChecked()) {
							lv.setItemChecked(position, true);
						} else {
							lv.setItemChecked(position, false);
						}
	        		}
				
				}
	        });
           
           convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
    			final Dialog editDialog = new Dialog(ShoppingListFragment.this.getActivity());
                editDialog.setContentView(R.layout.edit_popup);
                editDialog.setTitle("Edit Item");
                EditText nameText = (EditText) editDialog.findViewById(R.id.nameEdit);
            	EditText quantityText = (EditText) editDialog.findViewById(R.id.quantityEdit);
            	Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
            	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
            	nameText.setText(foodItem.getName());
            	quantityText.setText(foodItem.getAmount());
            	int numberOfCat = 0;
            	for (FoodItemCategory fic : FoodItemCategory.values()) {
            		if (fic == foodItem.getCategory()) {
            			categoryText.setSelection(numberOfCat);
            			break;
            		}
            		numberOfCat++;
            	}
            	expirationDate.updateDate(1900+foodItem.getExperiationDate().getYear(), foodItem.getExperiationDate().getMonth(), foodItem.getExperiationDate().getDate());
                editDialog.show();
                Button addButton = (Button) editDialog.findViewById(R.id.editButton);
                addButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						editItem(editDialog, foodItem);
						editDialog.dismiss();
					}
                });
                return true;
			}
        	   
           });
       		item.setText(foodItem.getName());
       		return convertView;
        }
	}
	
	public class ExpandableListAdapter extends BaseExpandableListAdapter {
		private Context context;
	    private List<String> categories = new ArrayList<String>();
        private Map<String, List<FoodItem>> foodMap = new HashMap<String, List<FoodItem>>();
	    public ExpandableListAdapter(Context context, List<FoodItem> foodItems) {
	    	this.context = context;
	    	for(int i = 0; i < FoodItemCategory.values().length; i++) {
	    		categories.add(FoodItemCategory.values()[i].toString());
	    		List<FoodItem> categoryItems = new ArrayList<FoodItem>();
	    		for (int j = 0; j < shoppingList.getFoodItems().size(); j++) {
	    			if (shoppingList.getFoodItems().get(j).getCategory().equals(FoodItemCategory.values()[i]))
	    				categoryItems.add(shoppingList.getFoodItems().get(j));
	    		}
	    		foodMap.put(FoodItemCategory.values()[i].toString(), categoryItems);
	        }
	    }
	    
	    @Override
	    public int getGroupCount() {
	        return categories.size();
	    }
	    
        @Override
        public int getChildrenCount(int groupPosition) {
            return foodMap.get(categories.get(groupPosition)).size();
        }
 
        @Override
        public Object getGroup(int groupPosition) {
            return categories.get(groupPosition);
        }
        
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return foodMap.get(categories.get(groupPosition)).get(childPosition);
        }
 
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
 
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
 
        @Override
        public boolean hasStableIds() {
            return true;
        }
	 
	    public boolean isChildSelectable(int groupPosition, int childPosition) {
	        return true;
	    }

	    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final FoodItem foodItem = (FoodItem) getChild(groupPosition, childPosition);
            LayoutInflater inflater = ShoppingListFragment.this.getActivity().getLayoutInflater();
     
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.shopping_list_item, null);
            }
            
         // Create a ListView-specific touch listener. ListViews are given special treatment because
            // by default they handle touches for their list items... i.e. they're in charge of drawing
            // the pressed state (the list selector), handling list item clicks, etc.
            final SwipeDismissListViewTouchListener touchListener =
                    new SwipeDismissListViewTouchListener(
                            elv,
                            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                @Override
                                public boolean canDismiss(int position) {
                                    return true;
                                }

                                @Override
                                public void onDismiss(ListView listView, int[] reverseSortedPositions, boolean dismissRight) {
                                    for (int position : reverseSortedPositions) {
                                    		FoodItem foodToRemove = ((FoodItem)elv.getAdapter().getItem(position));
                                    		shoppingList.removeItem(foodToRemove);
                                    		((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
                                    		((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
                                    		((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
                                    	}
                                	}
                            });
            convertView.setOnTouchListener(touchListener);
     
            TextView item = (TextView) convertView.findViewById(R.id.item);
            final CheckBox cb = (CheckBox) convertView.findViewById(R.id.shopping_list_checkbox);
            
            convertView.setOnClickListener(new OnClickListener(){
 				@Override
 				public void onClick(View v) {
 					if (touchListener.getAllowClick()){
 						int position = 1;
 			            for(int i = 0; i < groupPosition; i++) {
 			            	position++;
 			            	for (int j = 0; j < getChildrenCount(i); j++) {
 			            		position++;
 			            	}
 			            }
 			            position += childPosition;
 						//cb.toggle();
 						((CheckableRelativeLayout) v).toggle();
 						if (cb.isChecked()) {
 							elv.setItemChecked(position, true);
 						} else {
 							elv.setItemChecked(position, false);
 						}
 	        		}
 				
 				}
 	        });
            
            convertView.setOnLongClickListener(new OnLongClickListener() {

 			@Override
 			public boolean onLongClick(View v) {
     			final Dialog editDialog = new Dialog(ShoppingListFragment.this.getActivity());
                 editDialog.setContentView(R.layout.edit_popup);
                 editDialog.setTitle("Edit Item");
                 EditText nameText = (EditText) editDialog.findViewById(R.id.nameEdit);
             	EditText quantityText = (EditText) editDialog.findViewById(R.id.quantityEdit);
             	Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
             	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
             	nameText.setText(foodItem.getName());
             	quantityText.setText(foodItem.getAmount());
             	int numberOfCat = 0;
             	for (FoodItemCategory fic : FoodItemCategory.values()) {
             		if (fic == foodItem.getCategory()) {
             			categoryText.setSelection(numberOfCat);
             			break;
             		}
             		numberOfCat++;
             	}
             	expirationDate.updateDate(1900+foodItem.getExperiationDate().getYear(), foodItem.getExperiationDate().getMonth(), foodItem.getExperiationDate().getDate());
                 editDialog.show();
                 Button addButton = (Button) editDialog.findViewById(R.id.editButton);
                 addButton.setOnClickListener(new View.OnClickListener() {
 					
 					@Override
 					public void onClick(View v) {
 						
 						editItem(editDialog, foodItem);
 						editDialog.dismiss();
 					}
                 });
                 return true;
 			}
         	   
            });
        		item.setText(foodItem.getName());
        		return convertView;
        }

		@Override
		 public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String categoryName = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) ShoppingListFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.group_item, null);
            }
            TextView item = (TextView) convertView.findViewById(R.id.item);
            item.setTypeface(null, Typeface.BOLD);
            item.setText(categoryName);
            return convertView;
        }
		
		public void addFoodItem(FoodItem fi) {
			for(int i = 0; i < getGroupCount(); i++) {
				if (getGroup(i).equals(fi.getCategory().toString())) {
					foodMap.get(getGroup(i)).add(fi);
					break;
				}
			}
		}
		
		public void removeFoodItem(FoodItem fi) {
			for(int i = 0; i < getGroupCount(); i++) {
				if(getGroup(i).equals(fi.getCategory().toString())) {
					foodMap.get(getGroup(i)).remove(fi);
				}
			}
		}
	}
	
	 public void addNewItem(FoodItem fi) {
     	if( !(fi.getName().equals("") || fi.getAmount().equals("")) )
         {
     		shoppingList.addItem(fi);
     		createShoppingList();
         } 
     }
     
     public boolean isInShoppingList(FoodItem food) {
     	return shoppingList.isInShoppingList(food);
     }
     
     public void editItem(Dialog editDialog, FoodItem food)
     {
     	((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(food);
     	EditText nameText = (EditText) editDialog.findViewById(R.id.nameEdit);
     	EditText quantityText = (EditText) editDialog.findViewById(R.id.quantityEdit);
     	Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
     	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
     	String name = nameText.getText().toString();
     	String quantity = quantityText.getText().toString();
     	String category = categoryText.getSelectedItem().toString();
     	Calendar cal = GregorianCalendar.getInstance();
         cal.set(expirationDate.getYear(), expirationDate.getMonth(), expirationDate.getDayOfMonth());
     	Date expDate = cal.getTime(); 
     	food.setName(name);
     	food.setAmount(quantity);
     	for (FoodItemCategory fic : FoodItemCategory.values()) {
     		if (fic.toString().equals(category)) {
     			food.setCategory(fic);
     			break;
     		}
     	}
     	food.setExperiationDate(expDate);
     	((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
     	((ExpandableListAdapter) elv.getExpandableListAdapter()).addFoodItem(food);
     	((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
     }
}
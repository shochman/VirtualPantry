package com.thundersnacks.virtualpantry;

import java.text.DecimalFormat;
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
import com.thundersnacks.virtualpantry.PantryFragment.SearchFoodItemsAdapter;
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
	ListView slv;
	List<FoodItem> searchFoodItems;
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
	
	public void setSearchFoodItems(List<FoodItem> searchFoodItems) {
		this.searchFoodItems = searchFoodItems;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);
        shoppingList = new ShoppingList();
        shoppingList.addItem(new StandardFoodItem("Cheddar Cheese",0,new Date(),1,FoodItemUnit.POUNDS, "y", FoodItemCategory.DAIRY,2.36));
        shoppingList.addItem(new StandardFoodItem("Strawberry Milk",1,new Date(),0.5,FoodItemUnit.GALLONS," ", FoodItemCategory.DAIRY,4.69));
        shoppingList.addItem(new StandardFoodItem("Oatmeal",2,new Date(),1,FoodItemUnit.BOX," ", FoodItemCategory.GRAIN,2.50));
        shoppingList.addItem(new StandardFoodItem("M&M's",3,new Date(),2.63,FoodItemUnit.POUNDS," ", FoodItemCategory.SWEET,8.89));
        shoppingList.addItem(new StandardFoodItem("Frozen yogurt",4,new Date(),16,FoodItemUnit.OUNCES," ", FoodItemCategory.FROZEN,5.60));
        shoppingList.addItem(new StandardFoodItem("Avacados",5,new Date(),3,FoodItemUnit.UNITLESS," ", FoodItemCategory.FAT,2.00)); 
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
        slv = (ListView) view.findViewById(R.id.search_shopping_list_list);
        searchFoodItems = new ArrayList<FoodItem>();
        for (FoodItem fi : shoppingList.getFoodItems()) {
        	searchFoodItems.add(fi);
        }
        slv.setAdapter(new SearchFoodItemsAdapter(getActivity(), searchFoodItems));
        createShoppingList(false);
        
        TextView totalPrice = (TextView) view.findViewById(R.id.totalPrice);
    	double tp=0;
        for (FoodItem fi : shoppingList.getFoodItems()) {
        	tp+=fi.getPrice();
        }
        DecimalFormat f = new DecimalFormat("##.00");  // this will helps you to always keeps in two decimal places
        totalPrice.setText("Total: $"+f.format(tp));
        Button addToPantryButton = (Button) view.findViewById(R.id.addToPantryButton);
	    addToPantryButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SparseBooleanArray checked;
					int size;
					if (lv.getVisibility() == View.VISIBLE) {
						checked = lv.getCheckedItemPositions();
						size = lv.getAdapter().getCount()-1;
					} else if (elv.getVisibility() == View.VISIBLE) {
						checked = elv.getCheckedItemPositions();
						size = elv.getAdapter().getCount()-1;
					} else {
						checked = slv.getCheckedItemPositions();
						size = slv.getAdapter().getCount()-1;
					}
					for(int i = size; i >= 0; i--) {
						if (checked.get(i)) {
							FoodItem foodToRemove;
							if (lv.getVisibility() == View.VISIBLE) {
								foodToRemove = (FoodItem) lv.getAdapter().getItem(i);
								lv.setItemChecked(i, false);
								//((CheckBox) lv.getChildAt(i).findViewById(R.id.shopping_list_checkbox)).toggle();
							} else if (elv.getVisibility() == View.VISIBLE) {
 								foodToRemove = (FoodItem) elv.getAdapter().getItem(i);
								elv.setItemChecked(i, false);
			               		((CheckBox) elv.getChildAt(i).findViewById(R.id.shopping_list_checkbox)).toggle();
							} else {
								foodToRemove = (FoodItem) slv.getAdapter().getItem(i);
								slv.setItemChecked(i, false);
								for (int j = 0; j < lv.getAdapter().getCount(); j++) {
									if (foodToRemove.getName().equals(((FoodItem) lv.getAdapter().getItem(j)).getName())) {
										lv.setItemChecked(j, false);
			       					}
								}
								//((CheckBox) lv.getChildAt(i).findViewById(R.id.shopping_list_checkbox)).toggle();
							}
		               		((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
		               		searchFoodItems.remove(foodToRemove);
		               		pf.addNewItem(foodToRemove);
		               		shoppingList.removeItem(foodToRemove);
						}
					}
            		((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
            		((SearchFoodItemsAdapter) slv.getAdapter()).notifyDataSetChanged();
            		((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
				
            		TextView totalPrice = (TextView) view.findViewById(R.id.totalPrice);
                	double tp=0;
                    for (FoodItem food : shoppingList.getFoodItems()) {
                    	tp+=food.getPrice();
                    }
                    DecimalFormat f = new DecimalFormat("##.00");  // this will helps you to always keeps in two decimal places				
                    totalPrice.setText("Total: $"+f.format(tp));
				}
			});
        return view;
    }
	
	public void createShoppingList(boolean search) {
		if (search) {
			slv.setVisibility(View.VISIBLE);
			lv.setVisibility(View.INVISIBLE);
			elv.setVisibility(View.INVISIBLE);
			slv.setAdapter(new SearchFoodItemsAdapter(getActivity(), searchFoodItems));
			//((SearchFoodItemsAdapter) slv.getAdapter()).notifyDataSetChanged();
			slv.invalidateViews();
		} else {
			/**if (shoppingList.getHowSorted() == 0) {
				lv.setVisibility(View.INVISIBLE);
				elv.setVisibility(View.VISIBLE);
				slv.setVisibility(View.INVISIBLE);
				elv.invalidateViews();
			} else {*/
				elv.setVisibility(View.INVISIBLE);
				lv.setVisibility(View.VISIBLE);
				slv.setVisibility(View.INVISIBLE);
				lv.invalidateViews();
			//}
		}
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
						for (int i = 0; i < slv.getAdapter().getCount(); i++) {
							if (((FoodItem) slv.getAdapter().getItem(i)).getName().equals(foodItem.getName())) {
								if (((CheckableRelativeLayout) v).isChecked()) {
									slv.setItemChecked(position, true);
								} else {
									slv.setItemChecked(position, false);
								}
           					}
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
            	EditText priceText = (EditText) editDialog.findViewById(R.id.priceEdit);
            	Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
            	Spinner unitText = (Spinner) editDialog.findViewById(R.id.unit_spinner);
            	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
            	nameText.setText(foodItem.getName());
            	priceText.setText(Double.toString(foodItem.getPrice()));
            	quantityText.setText(Double.toString(foodItem.getAmount()));
            	int numberOfCat = 0;
            	int numberOfUnit = 0;
            	for (FoodItemUnit fic : FoodItemUnit.values()) {
            		if (fic == foodItem.getUnit()) {
            			unitText.setSelection(numberOfUnit);
            			break;
            		}
            		numberOfUnit++;
            	}   
            	
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
        	TextView price = (TextView) convertView.findViewById(R.id.item_price);
        	price.setText("$"+Double.toString(foodItem.getPrice()));
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
             	Spinner unitText = (Spinner) editDialog.findViewById(R.id.unit_spinner);
            	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
             	nameText.setText(foodItem.getName());
             	quantityText.setText(Double.toString(foodItem.getAmount()));
             	int numberOfUnit = 0;
            	for (FoodItemUnit fic : FoodItemUnit.values()) {
            		if (fic == foodItem.getUnit()) {
            			unitText.setSelection(numberOfUnit);
            			break;
            		}
            		numberOfUnit++;
            	}     
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
            TextView price = (TextView) convertView.findViewById(R.id.item_price);
        	price.setText("$"+Double.toString(foodItem.getPrice()));
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
	
	public class SearchFoodItemsAdapter extends ArrayAdapter<FoodItem> {
        public SearchFoodItemsAdapter(Activity activity, List<FoodItem> list) {
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
                           slv,
                           new SwipeDismissListViewTouchListener.DismissCallbacks() {
                               @Override
                               public boolean canDismiss(int position) {
                                   return true;
                               }

                               @Override
                               public void onDismiss(ListView listView, int[] reverseSortedPositions, boolean dismissRight) {
                                   for (int position : reverseSortedPositions) {
                                   		FoodItem foodToRemove = ((FoodItem)slv.getAdapter().getItem(position));
                                   		shoppingList.removeItem(foodToRemove);
                                   		searchFoodItems.remove(foodToRemove);
                                   		((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
                                   		((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
                                   		((SearchFoodItemsAdapter) slv.getAdapter()).notifyDataSetChanged();
                                   		((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
                                   	}
                               	}
                           });
           convertView.setOnTouchListener(touchListener);
           
           convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (touchListener.getAllowClick()){
						((CheckableRelativeLayout) v).toggle();
						if (((CheckableRelativeLayout) v).isChecked()) {
							slv.setItemChecked(position, true);
						} else {
							slv.setItemChecked(position, false);
						}
						for (int i = 0; i < lv.getAdapter().getCount(); i++) {
							if (((FoodItem) lv.getAdapter().getItem(i)).getName().equals(foodItem.getName())) {
								if (((CheckableRelativeLayout) v).isChecked()) {
									lv.setItemChecked(i, true);
								} else {
									lv.setItemChecked(i, false);
								}
           					}
						}
	        		}
				}
	        });
           
           SparseBooleanArray checked = lv.getCheckedItemPositions();
           int size = lv.getAdapter().getCount()-1;
           for(int j = size; j >= 0; j--) {
				if (checked.get(j)) {
					FoodItem foodToRemove = (FoodItem) lv.getAdapter().getItem(j);
					for (int i = 0; i < slv.getAdapter().getCount(); i++) {
						if (foodToRemove.getName().equals(foodItem.getName())) {
							slv.setItemChecked(position, true);
       					}
					}
					//((CheckBox) lv.getChildAt(i).findViewById(R.id.shopping_list_checkbox)).toggle();
				}
           }
           ((SearchFoodItemsAdapter) slv.getAdapter()).notifyDataSetChanged();
           
           convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
    			final Dialog editDialog = new Dialog(ShoppingListFragment.this.getActivity());
                editDialog.setContentView(R.layout.edit_popup);
                editDialog.setTitle("Edit Item");
                EditText nameText = (EditText) editDialog.findViewById(R.id.nameEdit);
            	EditText quantityText = (EditText) editDialog.findViewById(R.id.quantityEdit);
            	Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
            	Spinner unitText = (Spinner) editDialog.findViewById(R.id.unit_spinner);
            	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
            	nameText.setText(foodItem.getName());
            	quantityText.setText(Double.toString(foodItem.getAmount()));
            	int numberOfCat = 0;
            	int numberOfUnit = 0;
            	for (FoodItemUnit fic : FoodItemUnit.values()) {
            		if (fic == foodItem.getUnit()) {
            			unitText.setSelection(numberOfUnit);
            			break;
            		}
            		numberOfUnit++;
            	}   
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
            TextView price = (TextView) convertView.findViewById(R.id.item_price);
        	price.setText("$"+Double.toString(foodItem.getPrice()));
       		return convertView;
        }
	}
	
	 public void addNewItem(FoodItem fi) {
		 if( !(fi.getName().equals("") || fi.getAmount() == 0) ) {	//TODO - correct second term?
     		shoppingList.addItem(fi);
     		if (shoppingList.getHowSorted() == 0) {
        		shoppingList.categorySort();
        	} else if (shoppingList.getHowSorted() == 1) {
        		shoppingList.alphabeticalSort();
        	} else {
        		shoppingList.expirationSort();
        	}
     		((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
        	((ExpandableListAdapter) elv.getExpandableListAdapter()).addFoodItem(fi);
        	((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
        	TextView totalPrice = (TextView) view.findViewById(R.id.totalPrice);
        	double tp=0;
            for (FoodItem food : shoppingList.getFoodItems()) {
            	tp+=food.getPrice();
            	DecimalFormat f = new DecimalFormat("##.00");  // this will helps you to always keeps in two decimal places
            	totalPrice.setText("Total: $"+f.format(tp));
            }
         } 
     }
	 
	 public void removeItem(FoodItem foodToRemove) {
		 ((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
		 shoppingList.removeItem(foodToRemove);
		 ((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
		 ((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
		 TextView totalPrice = (TextView) view.findViewById(R.id.totalPrice);
     	double tp=0;
         for (FoodItem food : shoppingList.getFoodItems()) {
         	tp+=food.getPrice();
         }
         DecimalFormat f = new DecimalFormat("##.00");  // this will helps you to always keeps in two decimal places
         totalPrice.setText("Total: $"+f.format(tp));
	 }
     
     public boolean isInShoppingList(FoodItem food) {
     	return shoppingList.isInShoppingList(food);
     }
     
     public void editItem(Dialog editDialog, FoodItem food) {
     	((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(food);
     	EditText nameText = (EditText) editDialog.findViewById(R.id.nameEdit);
     	EditText quantityText = (EditText) editDialog.findViewById(R.id.quantityEdit);
     	EditText priceText = (EditText) editDialog.findViewById(R.id.priceEdit);
     	Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
     	Spinner unitText = (Spinner) editDialog.findViewById(R.id.unit_spinner);
    	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
    	String name = nameText.getText().toString();
    	double quantity = Double.parseDouble(quantityText.getText().toString());
    	String category = categoryText.getSelectedItem().toString();
    	String unit = unitText.getSelectedItem().toString();
     	Calendar cal = GregorianCalendar.getInstance();
        cal.set(expirationDate.getYear(), expirationDate.getMonth(), expirationDate.getDayOfMonth());
     	Date expDate = cal.getTime(); 
     	double price = Double.parseDouble(priceText.getText().toString());
     	food.setName(name);
     	food.setAmount(quantity);
     	food.setPrice(price);
    	for (FoodItemUnit fic : FoodItemUnit.values()) {
    		if (fic.toString().equals(unit)) {
    			food.setUnit(fic);
    			break;
    		}
    	}  
     	for (FoodItemCategory fic : FoodItemCategory.values()) {
     		if (fic.toString().equals(category)) {
     			food.setCategory(fic);
     			break;
     		}
     	}
     	food.setExperiationDate(expDate);
     	if (shoppingList.getHowSorted() == 0) {
    		shoppingList.categorySort();
    	} else if (shoppingList.getHowSorted() == 1) {
    		shoppingList.alphabeticalSort();
    	}
     	((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
     	((ExpandableListAdapter) elv.getExpandableListAdapter()).addFoodItem(food);
     	((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
     	TextView totalPrice = (TextView) view.findViewById(R.id.totalPrice);
    	double tp=0;
        for (FoodItem food1 : shoppingList.getFoodItems()) {
        	tp+=food1.getPrice();
        }
        DecimalFormat f = new DecimalFormat("##.00");  // this will helps you to always keeps in two decimal places
        totalPrice.setText("Total: $"+f.format(tp));
     }
}
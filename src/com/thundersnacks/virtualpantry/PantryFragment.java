package com.thundersnacks.virtualpantry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;
import com.thundersnacks.virtualpantrymodel.Pantry;
import com.thundersnacks.virtualpantrymodel.StandardFoodItem;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
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
 * Android samples: com.example.android.apis.view.ExpandableList1
 * http://androidword.blogspot.com/2012/01/how-to-use-expandablelistview.html
 * http://stackoverflow.com/questions/6938560/android-fragments-setcontentview-alternative
 * http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment-android
 */
public class PantryFragment extends Fragment {
	
	private Pantry pantry;
	View view;
	ExpandableListView elv;
	ListView lv;
	public ShoppingListFragment slf;
	
	public PantryFragment() {
		pantry = null;
	}

	public void setPantry( Pantry pantry ) {
		this.pantry = pantry;
	}
	
	public Pantry getPantry() {
		return pantry;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        pantry = new Pantry("My Pantry", 0);
        Calendar cal = GregorianCalendar.getInstance();
        
        cal.set(2014, 2, 28);
        Date d1 = cal.getTime();
        
        cal.set(2014, 4, 2);
        Date d2 =  cal.getTime();
        
        cal.set(2014, 7, 17);
        Date d3 = cal.getTime();
        
        cal.set(2014, 4, 25);
        Date d4 = cal.getTime();
        
        cal.set(2014, 11, 6);
        Date d5 = cal.getTime();
        
        cal.set(2014, 10, 10);
        Date d6 = cal.getTime();
        
        cal.set(2014, 0, 1);
        Date d7 = cal.getTime();
        
        pantry.addItem(new StandardFoodItem("Coke", 0, d5, "6 cans", "", FoodItemCategory.BEVERAGE));
        pantry.addItem(new StandardFoodItem("Sprite", 0, d6 , "4 cans", "", FoodItemCategory.BEVERAGE));
        
        pantry.addItem(new StandardFoodItem("Ketchup", 0, d3, "1 bottle", "", FoodItemCategory.CONDIMENT));
        pantry.addItem(new StandardFoodItem("Mustard", 0, d2, "1 bottle", "", FoodItemCategory.CONDIMENT));
        
        pantry.addItem(new StandardFoodItem("2% Milk", 0, d2, "1 gallon", "", FoodItemCategory.DAIRY));
        pantry.addItem(new StandardFoodItem("Shredded Cheese", 0, d3, "1 bag", "", FoodItemCategory.DAIRY));
        pantry.addItem(new StandardFoodItem("Yogurt", 0, d1, "3 cups", "", FoodItemCategory.DAIRY));
        
        pantry.addItem(new StandardFoodItem("Butter", 0, d4, "1/2 stick", "", FoodItemCategory.FAT));
        
        pantry.addItem(new StandardFoodItem("Ice Cream", 0, d3, "1 tub", "", FoodItemCategory.FROZEN));
        
        pantry.addItem(new StandardFoodItem("Apple", 0, d2, "1", "", FoodItemCategory.FRUIT));
        pantry.addItem(new StandardFoodItem("Pear", 0, d2, "1", "", FoodItemCategory.FRUIT));
        
        pantry.addItem(new StandardFoodItem("Bread", 0, d1, "1/2 loaf", "", FoodItemCategory.GRAIN));
        pantry.addItem(new StandardFoodItem("Cereal", 0, d3, "2 boxes", "", FoodItemCategory.GRAIN));
        
        pantry.addItem(new StandardFoodItem("Chicken Breast", 0, d2, "1 lb", "", FoodItemCategory.PROTEIN));
        pantry.addItem(new StandardFoodItem("Ground Beef", 0, d4, "1/2 lb", "", FoodItemCategory.PROTEIN));
        
        pantry.addItem(new StandardFoodItem("Potato Chips", 0, d3, "1 bag", "", FoodItemCategory.SNACK));
        pantry.addItem(new StandardFoodItem("Pretzels", 0, d7, "1 bag", "", FoodItemCategory.SNACK));
        
        pantry.addItem(new StandardFoodItem("Chocolate", 0, d7, "2 bars", "", FoodItemCategory.SWEET));
        pantry.addItem(new StandardFoodItem("Cookies", 0, d3, "1 bag", "", FoodItemCategory.SWEET));
        
        pantry.addItem(new StandardFoodItem("Carrots", 0, d1, "2 bags", "", FoodItemCategory.VEGETABLE));
        pantry.addItem(new StandardFoodItem("Lettuce", 0, d4, "1 bag", "", FoodItemCategory.VEGETABLE));
        pantry.addItem(new StandardFoodItem("Broccoli", 0, d2, "1 stalk", "", FoodItemCategory.VEGETABLE));
        
        pantry.categorySort();
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pantry, null);
        slf = (ShoppingListFragment) getActivity().getFragmentManager().findFragmentByTag("Shopping List");
		elv = (ExpandableListView) view.findViewById(R.id.pantry_expandable_list);
		elv.setAdapter(new ExpandableListAdapter(getActivity(), pantry.getFoodItems()));
        lv = (ListView) view.findViewById(R.id.pantry_list);
        lv.setAdapter(new FoodItemsAdapter(getActivity(), pantry.getFoodItems()));
        createPantry();
        return view;
    }
	
	public void createPantry() {
		if (pantry.getHowSorted() == 0) {
			lv.setVisibility(View.INVISIBLE);
			elv.setVisibility(View.VISIBLE);
			elv.invalidateViews();
		} else {
			elv.setVisibility(View.INVISIBLE);
			lv.setVisibility(View.VISIBLE);
			lv.invalidateViews();
		}
	}
	
	public class FoodItemsAdapter extends ArrayAdapter<FoodItem> {
        public FoodItemsAdapter(Activity activity, List<FoodItem> list) {
           super(activity, R.layout.pantry_item, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           // Get the data item for this position
           final FoodItem foodItem = getItem(position);    
           // Check if an existing view is being reused, otherwise inflate the view
           if (convertView == null) {
              convertView = LayoutInflater.from(getContext()).inflate(R.layout.pantry_item, null);
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
                                   		pantry.removeItem(foodToRemove);
                                   		((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
                                   		((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
                                   		((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
                                   	}
                               	}
                           });
           convertView.setOnTouchListener(touchListener);
           
           convertView.setOnClickListener(new OnClickListener() {
           		public void onClick(View v) {
           			if (touchListener.getAllowClick()) {
           				final Dialog editDialog = new Dialog(PantryFragment.this.getActivity());
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
           		}
           	}
           	});
           
       		ImageView addToShoppingList = (ImageView) convertView.findViewById(R.id.add_to_shopping_list);
       		if(slf.isInShoppingList(foodItem)) {
       			addToShoppingList.setImageDrawable(getResources().getDrawable(R.drawable.shopping_cart_green));
       		} else {
       			addToShoppingList.setImageDrawable(getResources().getDrawable(R.drawable.shopping_cart));
       		}
       		addToShoppingList.setOnClickListener(new OnClickListener() {
       			public void onClick(View v) {
       				slf.addNewItem(foodItem);
       				Toast.makeText(PantryFragment.this.getActivity(),foodItem.getName() + " added to shopping list",Toast.LENGTH_SHORT).show();
       				lv.invalidateViews();
       			}
       		});
       		TextView item = (TextView) convertView.findViewById(R.id.item);
       		item.setText(foodItem.getName());
       		TextView itemQuantity = (TextView) convertView.findViewById(R.id.item_quantity);
            itemQuantity.setText("x" + foodItem.getAmount());
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
	    		for (int j = 0; j < pantry.getFoodItems().size(); j++) {
	    			if (pantry.getFoodItems().get(j).getCategory().equals(FoodItemCategory.values()[i]))
	    				categoryItems.add(pantry.getFoodItems().get(j));
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
            LayoutInflater inflater = PantryFragment.this.getActivity().getLayoutInflater();
     
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.pantry_item, null);
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
                                    	FoodItem foodToRemove = (FoodItem)elv.getAdapter().getItem(position);
                                    	pantry.removeItem(foodToRemove);
                                    	((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
                                    	((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
                                    	((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
                                    }
                                }
                            });
            convertView.setOnTouchListener(touchListener);
            
            convertView.setOnClickListener(new OnClickListener() {
            	public void onClick(View v) {
            		if (touchListener.getAllowClick()) {
            			final Dialog editDialog = new Dialog(PantryFragment.this.getActivity());
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
            		}
            	}
            });
            
            ImageView addToShoppingList = (ImageView) convertView.findViewById(R.id.add_to_shopping_list);
        	if(slf.isInShoppingList(foodItem)) {
            	addToShoppingList.setImageDrawable(getResources().getDrawable(R.drawable.shopping_cart_green));
            } else {
            	addToShoppingList.setImageDrawable(getResources().getDrawable(R.drawable.shopping_cart));
            }
            addToShoppingList.setOnClickListener(new OnClickListener() {
     
                public void onClick(View v) {
                	slf.addNewItem(foodItem);
                	Toast.makeText(PantryFragment.this.getActivity(),foodItem.getName() + " added to shopping list",Toast.LENGTH_SHORT).show();
                	elv.invalidateViews();
                }
            });
            TextView item = (TextView) convertView.findViewById(R.id.item);
            item.setText(foodItem.getName());
            TextView itemQuantity = (TextView) convertView.findViewById(R.id.item_quantity);
            itemQuantity.setText("x" + foodItem.getAmount());
            return convertView;
        }

		@Override
		 public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String categoryName = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) PantryFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    
    public void addNewItem(FoodItem fi)
    {
    	pantry.addItem(fi);
    	if (pantry.getHowSorted() == 0) {
    		pantry.categorySort();
    	} else if (pantry.getHowSorted() == 1) {
    		pantry.alphabeticalSort();
    	} else {
    		pantry.expirationSort();
    	}
    	((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
    	((ExpandableListAdapter) elv.getExpandableListAdapter()).addFoodItem(fi);
    	((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
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
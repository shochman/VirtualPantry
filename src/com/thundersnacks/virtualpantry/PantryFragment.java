package com.thundersnacks.virtualpantry;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;
import com.thundersnacks.virtualpantrymodel.Pantry;
import com.thundersnacks.virtualpantrymodel.StandardFoodItem;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
 
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
	FoodItem food;
	ExpandableListView elv;
	public ShoppingListFragment slf;
	
	public PantryFragment() {
		this.pantry = null;
	}

	public void setPantry( Pantry pantry ) {
		this.pantry = pantry;
	}
	
	public Pantry getPantry() {
		return this.pantry;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
        
		super.onCreate(savedInstanceState);
        pantry = new Pantry("My Pantry", 0);
        
        Date d1 = new Date(2014,2,28);
        
        Date d2 = new Date(2014, 4, 2);
        
        Date d3 = new Date(2014, 7, 15);
        
        Date d4 = new Date(2014, 9, 25);
        
        Date d5 = new Date(2014, 11, 6 );
        
        Date d6 = new Date(2015, 10, 10);
        
        Date d7 = new Date(2014, 0, 1);
        
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
        
    }
	
	public void createPantry() {
		elv = (ExpandableListView) view.findViewById(R.id.list);
		Boolean[] openedTab = new Boolean[elv.getExpandableListAdapter().getGroupCount()];
		for (int i = 0; i < elv.getExpandableListAdapter().getGroupCount(); i++) {
			openedTab[i] = elv.isGroupExpanded(i);
		}
        elv.setAdapter(new SavedTabsListAdapter());
        for (int i = 0; i < elv.getExpandableListAdapter().getGroupCount(); i++) {
			if (openedTab[i]) elv.expandGroup(i);
		}
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.saved_tab, null); 
        elv = (ExpandableListView) view.findViewById(R.id.list);
        elv.setAdapter(new SavedTabsListAdapter());
        slf = (ShoppingListFragment) getActivity().getFragmentManager().findFragmentByTag("Shopping List");
        return view;
    }
 
    public class SavedTabsListAdapter extends BaseExpandableListAdapter {
    	 
        private String[] groups = new String[FoodItemCategory.values().length];
        private String[][] children = new String[FoodItemCategory.values().length][]; 
    	
        public SavedTabsListAdapter() {
        	for( int i = 0; i < FoodItemCategory.values().length; i ++) {
        		groups[i] = FoodItemCategory.values()[i].toString();
        		List<FoodItem> items = pantry.getItemsByCategory(FoodItemCategory.values()[i]);
        		children[i] = new String[items.size()];
        		for( int j = 0; j < items.size(); j++ ) {
        			children[i][j] = items.get(j).getName();
        		}
            } 
        }
        
        @Override
        public int getGroupCount() {
            return groups.length;
        }
 
        @Override
        public int getChildrenCount(int i) {
            return children[i].length;
        }
 
        @Override
        public Object getGroup(int i) {
            return groups[i];
        }
        
        @Override
        public Object getChild(int i, int i1) {
            return children[i][i1];
        }
 
        @Override
        public long getGroupId(int i) {
            return i;
        }
 
        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }
 
        @Override
        public boolean hasStableIds() {
            return true;
        }
        
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
        
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String foodItem = (String) getChild(groupPosition, childPosition);
            LayoutInflater inflater = PantryFragment.this.getActivity().getLayoutInflater();
     
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.child_item, null);
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
                                    	for (Iterator<FoodItem> it = pantry.iterator(); it.hasNext(); ) {
                                    		FoodItem test = it.next();
                                    		if (test.getName().equals(elv.getAdapter().getItem(position))) {
                                    			food = test;
                                    			break;
                                    		}
                                    	}
                                    	if (dismissRight) {
    										slf.addNewItem(food);
                                    	} else {
                                    		pantry.removeItem(food);
                                    	}
                                        createPantry();
                                    }
                                }
                            });
            convertView.setOnTouchListener(touchListener);
     
            TextView item = (TextView) convertView.findViewById(R.id.item);
            
            convertView.setOnClickListener(new OnClickListener() {
            	public void onClick(View v) {
            		if (touchListener.getAllowClick()) {
            			final Dialog viewDialog = new Dialog(PantryFragment.this.getActivity());
            			viewDialog.setContentView(R.layout.display_popup);
            			viewDialog.setTitle("View Item");
            			EditText nameText = (EditText) viewDialog.findViewById(R.id.nameEdit);
                		EditText quantityText = (EditText) viewDialog.findViewById(R.id.quantityEdit);
                		Spinner categoryText = (Spinner) viewDialog.findViewById(R.id.category_spinner);
                		DatePicker expirationDate = (DatePicker) viewDialog.findViewById(R.id.dpResult);
                		FoodItem food = null;
                		for (Iterator<FoodItem> it = pantry.iterator(); it.hasNext(); ) {
                			FoodItem test = it.next();
                			if (test.getName() == foodItem) {
                				food = test;
                				break;
                			}
                		}
                		nameText.setText(food.getName());
                		quantityText.setText(food.getAmount());
                		int numberOfCat = 0;
                		for (FoodItemCategory fic : FoodItemCategory.values()) {
                			if (fic == food.getCategory()) {
                				categoryText.setSelection(numberOfCat);
                				break;
                			}
                			numberOfCat++;
                		}
                		expirationDate.updateDate(food.getExperiationDate().getYear(), food.getExperiationDate().getMonth(), food.getExperiationDate().getDate());
            			viewDialog.show();
            		}
            	}
            });
     
            ImageView edit = (ImageView) convertView.findViewById(R.id.edit);
            edit.setOnClickListener(new OnClickListener() {
     
                public void onClick(View v) {
                	final Dialog editDialog = new Dialog(PantryFragment.this.getActivity());
                    editDialog.setContentView(R.layout.edit_popup);
                    editDialog.setTitle("Edit Item");
                    EditText nameText = (EditText) editDialog.findViewById(R.id.nameEdit);
                	EditText quantityText = (EditText) editDialog.findViewById(R.id.quantityEdit);
                	Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
                	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
                	food = null;
                	for (Iterator<FoodItem> it = pantry.iterator(); it.hasNext(); ) {
                		FoodItem test = it.next();
                		if (test.getName() == foodItem) {
                			food = test;
                			break;
                		}
                	}
                	nameText.setText(food.getName());
                	quantityText.setText(food.getAmount());
                	int numberOfCat = 0;
                	for (FoodItemCategory fic : FoodItemCategory.values()) {
                		if (fic == food.getCategory()) {
                			categoryText.setSelection(numberOfCat);
                			break;
                		}
                		numberOfCat++;
                	}
                	expirationDate.updateDate(food.getExperiationDate().getYear(), food.getExperiationDate().getMonth(), food.getExperiationDate().getDate());
                    editDialog.show();
                    Button addButton = (Button) editDialog.findViewById(R.id.editButton);
                    addButton.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							editItem(editDialog, food);
							editDialog.dismiss();
						}
					});
                }
            });
     
            item.setText(foodItem);
            return convertView;
        }
 
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
 
    }
    
    public void addNewItem(FoodItem fi)
    {
    	pantry.addItem(fi);
		createPantry();
    }
    
    public void editItem(Dialog editDialog, FoodItem food)
    {
    	EditText nameText = (EditText) editDialog.findViewById(R.id.nameEdit);
    	EditText quantityText = (EditText) editDialog.findViewById(R.id.quantityEdit);
    	Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
    	DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
    	String name = nameText.getText().toString();
    	String quantity = quantityText.getText().toString();
    	String category = categoryText.getSelectedItem().toString();
    	Date expDate = new Date(expirationDate.getYear(), expirationDate.getMonth(), expirationDate.getDayOfMonth()); 
    	food.setName(name);
    	food.setAmount(quantity);
    	for (FoodItemCategory fic : FoodItemCategory.values()) {
    		if (fic.toString().equals(category)) {
    			food.setCategory(fic);
    			break;
    		}
    	}
    	food.setExperiationDate(expDate);
    	createPantry();
    }
}

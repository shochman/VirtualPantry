package com.thundersnacks.virtualpantry;
import java.util.Date;
import java.util.List;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;
import com.thundersnacks.virtualpantrymodel.Pantry;
import com.thundersnacks.virtualpantrymodel.StandardFoodItem;

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
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
	
	public PantryFragment() {
		this.pantry = null;
	}

	public void setPantry( Pantry pantry ) {
		this.pantry = pantry;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
        
		super.onCreate(savedInstanceState);
        pantry = new Pantry("My Pantry", 0);
        
        pantry.addItem(new StandardFoodItem("Coke", 0, new Date(), "6 cans", "", FoodItemCategory.BEVERAGE));
        pantry.addItem(new StandardFoodItem("Sprite", 0, new Date(), "4 cans", "", FoodItemCategory.BEVERAGE));
        
        pantry.addItem(new StandardFoodItem("Ketchup", 0, new Date(), "1 bottle", "", FoodItemCategory.CONDIMENT));
        pantry.addItem(new StandardFoodItem("Mustard", 0, new Date(), "1 bottle", "", FoodItemCategory.CONDIMENT));
        
        pantry.addItem(new StandardFoodItem("2% Milk", 0, new Date(), "1 gallon", "", FoodItemCategory.DAIRY));
        pantry.addItem(new StandardFoodItem("Shredded Cheese", 0, new Date(), "1 bag", "", FoodItemCategory.DAIRY));
        pantry.addItem(new StandardFoodItem("Yogurt", 0, new Date(), "3 cups", "", FoodItemCategory.DAIRY));
        
        pantry.addItem(new StandardFoodItem("Butter", 0, new Date(), "1/2 stick", "", FoodItemCategory.FAT));
        
        pantry.addItem(new StandardFoodItem("Ice Cream", 0, new Date(), "1 tub", "", FoodItemCategory.FROZEN));
        
        pantry.addItem(new StandardFoodItem("Apple", 0, new Date(), "1", "", FoodItemCategory.FRUIT));
        pantry.addItem(new StandardFoodItem("Pear", 0, new Date(), "1", "", FoodItemCategory.FRUIT));
        
        pantry.addItem(new StandardFoodItem("Bread", 0, new Date(), "1/2 loaf", "", FoodItemCategory.GRAIN));
        pantry.addItem(new StandardFoodItem("Cereal", 0, new Date(), "2 boxes", "", FoodItemCategory.GRAIN));
        
        pantry.addItem(new StandardFoodItem("Chicken Breast", 0, new Date(), "1 lb", "", FoodItemCategory.PROTEIN));
        pantry.addItem(new StandardFoodItem("Ground Beef", 0, new Date(), "1/2 lb", "", FoodItemCategory.PROTEIN));
        
        pantry.addItem(new StandardFoodItem("Potato Chips", 0, new Date(), "1 bag", "", FoodItemCategory.SNACK));
        pantry.addItem(new StandardFoodItem("Pretzels", 0, new Date(), "1 bag", "", FoodItemCategory.SNACK));
        
        pantry.addItem(new StandardFoodItem("Chocolate", 0, new Date(), "2 bars", "", FoodItemCategory.SWEET));
        pantry.addItem(new StandardFoodItem("Cookies", 0, new Date(), "1 bag", "", FoodItemCategory.SWEET));
        
        pantry.addItem(new StandardFoodItem("Carrots", 0, new Date(), "2 bags", "", FoodItemCategory.VEGETABLE));
        pantry.addItem(new StandardFoodItem("Lettuce", 0, new Date(), "1 bag", "", FoodItemCategory.VEGETABLE));
        pantry.addItem(new StandardFoodItem("Broccoli", 0, new Date(), "1 stalk", "", FoodItemCategory.VEGETABLE));
        
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.saved_tab, null); 
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.list);
        elv.setAdapter(new SavedTabsListAdapter());
        return v;
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
     
            TextView item = (TextView) convertView.findViewById(R.id.item);
            
            convertView.setOnClickListener(new OnClickListener() {
            	public void onClick(View v) {
            		final Dialog viewDialog = new Dialog(PantryFragment.this.getActivity());
            		viewDialog.setContentView(R.layout.display_popup);
            		viewDialog.setTitle("View Item");
            		viewDialog.show();
            	}
            });
     
            ImageView edit = (ImageView) convertView.findViewById(R.id.edit);
            edit.setOnClickListener(new OnClickListener() {
     
                public void onClick(View v) {
                	final Dialog editDialog = new Dialog(PantryFragment.this.getActivity());
                    editDialog.setContentView(R.layout.edit_popup);
                    editDialog.setTitle("Edit Item");
                    editDialog.show();
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
 
}

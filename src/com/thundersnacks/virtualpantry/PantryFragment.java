package com.thundersnacks.virtualpantry;
import java.util.List;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;
import com.thundersnacks.virtualpantrymodel.Pantry;

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
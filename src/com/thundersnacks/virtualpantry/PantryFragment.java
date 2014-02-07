package com.thundersnacks.virtualpantry;

import java.util.ArrayList;
import java.util.List;

import com.thundersnacks.virtualpantry.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
 
/**
 * Pieced together from:
 * Android samples: com.example.android.apis.view.ExpandableList1
 * http://androidword.blogspot.com/2012/01/how-to-use-expandablelistview.html
 * http://stackoverflow.com/questions/6938560/android-fragments-setcontentview-alternative
 * http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment-android
 */
public class PantryFragment extends Fragment {
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.saved_tab, null);
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.list);
        elv.setAdapter(new SavedTabsListAdapter());
        return v;
    }
 
    public class SavedTabsListAdapter extends BaseExpandableListAdapter {
        private String[] groups = {"Beverages", "Protein", "Fruit", "Vegetables", "Dairy", "Frozen", "Condiments", "Sweets", "Snacks", "Grains", "Other"};
        private String[][] children = {
            { "Orange Juice", "Dr. Pepper", "Pepsi" },
            { "Chicken", "Eggs" },
            { "Strawberries", "Oranges", "Bananas", "Grapes" },
            { "Carrots", "Peas", "Corn", "Broccoli" },
            { "Milk", "Cheese" },
            { "Ice Cream" },
            { "Ketchup", "Mustard", "Mayonnaise" },
            { "Cookies", "Cheesecake", "Chocolate" },
            { "Crackers", "Chips" },
            { "Spaghetti", "Cereal", "Bread", "Rice" },
            { "Potatoes", "Apple Sauce", "Peanut Butter" }
        };
    	
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
            String laptopName = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) PantryFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.group_item, null);
            }
            TextView item = (TextView) convertView.findViewById(R.id.item);
            item.setTypeface(null, Typeface.BOLD);
            item.setText(laptopName);
            return convertView;
        }
        
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String laptop = (String) getChild(groupPosition, childPosition);
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
     
            item.setText(laptop);
            return convertView;
        }
 
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
 
    }
 
}
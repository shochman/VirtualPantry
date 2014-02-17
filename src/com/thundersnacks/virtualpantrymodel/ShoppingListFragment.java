package com.thundersnacks.virtualpantrymodel;

import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantry.R.id;
import com.thundersnacks.virtualpantry.R.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShoppingListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shopping_list, container, false);
        
        String[] food = new String[]{"Cheese", "Milk", "Cereal", "Cookies", "Ice Cream", "Milk", "Butter"};
        // The checkbox for the each item is specified by the layout android.R.layout.simple_list_item_multiple_choice
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, food);
   
        // Getting the reference to the listview object of the layout
        ListView listView = (ListView) view.findViewById(R.id.listview);
 
        // Setting adapter to the listview
        listView.setAdapter(adapter);
        return view;
    }
}

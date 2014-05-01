package com.thundersnacks.virtualpantry;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thundersnacks.database.DbAdapter;
import com.thundersnacks.virtualpantry.R;
import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;
import com.thundersnacks.virtualpantrymodel.FoodItemUnit;
import com.thundersnacks.virtualpantrymodel.Pantry;
import com.thundersnacks.virtualpantrymodel.ShoppingList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
	private View view;
	private ExpandableListView elv;
	private ListView lv;
	private ListView slv;
	private List<FoodItem> searchFoodItems;
	private DbAdapter db = DbAdapter.instance(null);
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

	public void setSearchFoodItems(List<FoodItem> searchFoodItems) {
		this.searchFoodItems = searchFoodItems;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Pantry loadedPantry = db.restorePantry(LoginActivity.currentUser.getDatabaseId());
		if(loadedPantry!=null) {
			setPantry(loadedPantry);
		}
		else {
			Pantry mPantry = new Pantry();
			ShoppingList mShoppingList = new ShoppingList();
			mPantry.setShoppingList(mShoppingList);
			setPantry(mPantry);
		}
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.pantry, null);
		slf = (ShoppingListFragment) getActivity().getFragmentManager().findFragmentByTag("Shopping List");
		elv = (ExpandableListView) view.findViewById(R.id.pantry_expandable_list);
		elv.setAdapter(new ExpandableListAdapter(getActivity(), pantry.getFoodItems()));
        lv = (ListView) view.findViewById(R.id.pantry_list);
        lv.setAdapter(new FoodItemsAdapter(getActivity(), pantry.getFoodItems()));
        slv = (ListView) view.findViewById(R.id.search_pantry_list);
        searchFoodItems = new ArrayList<FoodItem>();
        for (FoodItem fi : pantry.getFoodItems()) {
        	searchFoodItems.add(fi);
        }
        slv.setAdapter(new SearchFoodItemsAdapter(getActivity(), searchFoodItems));
        createPantry(false);
        if(slf != null) {
        	slf.setShoppingList(pantry.getShoppingList());
        }
        return view;
    }

	public void createPantry(boolean search) {
		if (search) {
			slv.setVisibility(View.VISIBLE);
			lv.setVisibility(View.INVISIBLE);
			elv.setVisibility(View.INVISIBLE);
			slv.setAdapter(new SearchFoodItemsAdapter(getActivity(), searchFoodItems));
			//((SearchFoodItemsAdapter) slv.getAdapter()).notifyDataSetChanged();
			slv.invalidateViews();
		} else {
			if (pantry.getHowSorted() == 0) {
				lv.setVisibility(View.INVISIBLE);
				elv.setVisibility(View.VISIBLE);
				slv.setVisibility(View.INVISIBLE);
				elv.invalidateViews();
			} else {
				elv.setVisibility(View.INVISIBLE);
				lv.setVisibility(View.VISIBLE);
				slv.setVisibility(View.INVISIBLE);
				lv.invalidateViews();
			}
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
						EditText priceText = (EditText) editDialog.findViewById(R.id.priceEdit);
						Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
						Spinner unitText = (Spinner) editDialog.findViewById(R.id.unit_spinner);           				
						DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
						nameText.setText(foodItem.getName());
						quantityText.setText(Double.toString(foodItem.getAmount()));
						DecimalFormat df = new DecimalFormat("#.00");
						priceText.setText(df.format(foodItem.getPrice()));
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
					if (slf.isInShoppingList(foodItem)) {
						slf.removeItem(foodItem);
						Toast.makeText(PantryFragment.this.getActivity(),foodItem.getName() + " removed from shopping list",Toast.LENGTH_SHORT).show();
						lv.invalidateViews();
					} else {
						slf.addNewItem(foodItem);
						Toast.makeText(PantryFragment.this.getActivity(),foodItem.getName() + " added to shopping list",Toast.LENGTH_SHORT).show();
						lv.invalidateViews();
					}
				}
			});

			ImageView increment = (ImageView) convertView.findViewById(R.id.increment_quantity);
			increment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					foodItem.setAmount(foodItem.getAmount() + 1);
					lv.invalidateViews();
				}

			});
			ImageView decrement = (ImageView) convertView.findViewById(R.id.decrement_quantity);
			decrement.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					foodItem.setAmount(foodItem.getAmount() - 1);
					if (foodItem.getAmount() <= 0){
						pantry.removeItem(foodItem);
						((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodItem);
						((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
						((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
					}
					lv.invalidateViews();
				}

			});

			final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
			progressBar.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					AlertDialog.Builder seek = new AlertDialog.Builder(
							PantryFragment.this.getActivity());
					View exp =  LayoutInflater.from(PantryFragment.this.getActivity()).inflate(R.layout.seek,null,false);

					// set title
					final	SeekBar	colour =(SeekBar) exp.findViewById(R.id.seekBar1);
					seek.setTitle("Your Title");
					seek.setView(exp);
					seek.setTitle("Progress");
					seek.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {

							dialog.cancel();
							DecimalFormat df = new DecimalFormat("#.00");
							foodItem.setAmount(Double.parseDouble(df.format(foodItem.getAmount()*(1 -  (double)colour.getProgress()/100))));
							if (foodItem.getAmount() <= 0){
								pantry.removeItem(foodItem);
								((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodItem);
								((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
								((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
							}
							lv.invalidateViews();
						}
					});
					seek.show();


					colour.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

							//	progressBar.setOnGenericMotionListener();

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							progressBar.setProgress(colour.getMax()-colour.getProgress());

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
			ImageView expirationWarning = (ImageView) convertView.findViewById(R.id.expiration_warning);
			Calendar warningCal = Calendar.getInstance();
			warningCal.add(Calendar.DATE, 3);
			Calendar expireCal = Calendar.getInstance();
			if (foodItem.getExperiationDate().before(expireCal.getTime())) {
				expirationWarning.setImageDrawable(getResources().getDrawable(R.drawable.expired));
			} else if (foodItem.getExperiationDate().before(warningCal.getTime())) {
				expirationWarning.setImageDrawable(getResources().getDrawable(R.drawable.warning_yellow));
			} else {
				expirationWarning.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
			}
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
						Spinner unitText = (Spinner) editDialog.findViewById(R.id.unit_spinner);
						DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
						nameText.setText(foodItem.getName());
						EditText priceText = (EditText) editDialog.findViewById(R.id.priceEdit);
						DecimalFormat df = new DecimalFormat("#.00");
						priceText.setText(df.format(foodItem.getPrice()));
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
					if (slf.isInShoppingList(foodItem)) {
						slf.removeItem(foodItem);
						Toast.makeText(PantryFragment.this.getActivity(),foodItem.getName() + " removed from shopping list",Toast.LENGTH_SHORT).show();
						elv.invalidateViews();
					} else {
						slf.addNewItem(foodItem);
						Toast.makeText(PantryFragment.this.getActivity(),foodItem.getName() + " added to shopping list",Toast.LENGTH_SHORT).show();
						elv.invalidateViews();
					}
				}
			});

			ImageView increment = (ImageView) convertView.findViewById(R.id.increment_quantity);
			increment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					foodItem.setAmount(foodItem.getAmount() + 1);
					elv.invalidateViews();
				}

			});
			ImageView decrement = (ImageView) convertView.findViewById(R.id.decrement_quantity);
			decrement.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					foodItem.setAmount(foodItem.getAmount() - 1);
					if (foodItem.getAmount() <= 0){
						pantry.removeItem(foodItem);
						((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodItem);
						((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
						((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
					}
					elv.invalidateViews();
				}

			});

			final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
			progressBar.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					AlertDialog.Builder seek = new AlertDialog.Builder(
							PantryFragment.this.getActivity());
					View exp =  LayoutInflater.from(PantryFragment.this.getActivity()).inflate(R.layout.seek,null,false);

					// set title

					final	SeekBar	colour =(SeekBar) exp.findViewById(R.id.seekBar1);seek.setTitle("Your Title");
					seek.setView(exp);
					seek.setTitle("Progress");
					seek.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {

							dialog.cancel();
							DecimalFormat df = new DecimalFormat("#.00");
							foodItem.setAmount(Double.parseDouble(df.format(foodItem.getAmount()*(1 -  (double)colour.getProgress()/100))));							if (foodItem.getAmount() <= 0){
								pantry.removeItem(foodItem);
								((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodItem);
								((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
								((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
							}
							elv.invalidateViews();
						}
					});
					seek.show();


					colour.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub


						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

							//	progressBar.setOnGenericMotionListener();

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							progressBar.setProgress(colour.getMax()-colour.getProgress());


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
			ImageView expirationWarning = (ImageView) convertView.findViewById(R.id.expiration_warning);
			Calendar warningCal = Calendar.getInstance();
			warningCal.add(Calendar.DATE, 3);
			Calendar expireCal = Calendar.getInstance();
			if (foodItem.getExperiationDate().before(expireCal.getTime())) {
				expirationWarning.setImageDrawable(getResources().getDrawable(R.drawable.expired));
			} else if (foodItem.getExperiationDate().before(warningCal.getTime())) {
				expirationWarning.setImageDrawable(getResources().getDrawable(R.drawable.warning_yellow));
			} else {
				expirationWarning.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
			}
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
					Collections.sort(foodMap.get(getGroup(i)), FoodItem.getCategoryComparator());
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
										pantry.removeItem(foodToRemove);
										searchFoodItems.remove(foodToRemove);
										((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodToRemove);
										((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
										((SearchFoodItemsAdapter) slv.getAdapter()).notifyDataSetChanged();
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
						Spinner unitText = (Spinner) editDialog.findViewById(R.id.unit_spinner);           				
						DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
						nameText.setText(foodItem.getName());
						quantityText.setText(Double.toString(foodItem.getAmount()));
						EditText priceText = (EditText) editDialog.findViewById(R.id.priceEdit);
						DecimalFormat df = new DecimalFormat("#.00");
						priceText.setText(df.format(foodItem.getPrice()));
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
					if (slf.isInShoppingList(foodItem)) {
						slf.removeItem(foodItem);
						Toast.makeText(PantryFragment.this.getActivity(),foodItem.getName() + " removed from shopping list",Toast.LENGTH_SHORT).show();
						slv.invalidateViews();
					} else {
						slf.addNewItem(foodItem);
						Toast.makeText(PantryFragment.this.getActivity(),foodItem.getName() + " added to shopping list",Toast.LENGTH_SHORT).show();
						slv.invalidateViews();
					}
				}
			});

			ImageView increment = (ImageView) convertView.findViewById(R.id.increment_quantity);
			increment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					foodItem.setAmount(foodItem.getAmount() + 1);
					slv.invalidateViews();
				}

			});
			ImageView decrement = (ImageView) convertView.findViewById(R.id.decrement_quantity);
			decrement.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					foodItem.setAmount(foodItem.getAmount() - 1);
					if (foodItem.getAmount() <= 0){
						pantry.removeItem(foodItem);
						searchFoodItems.remove(foodItem);
						((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodItem);
						((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
						((SearchFoodItemsAdapter) slv.getAdapter()).notifyDataSetChanged();
						((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
					}
					slv.invalidateViews();
				}

			});

			final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
			progressBar.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					AlertDialog.Builder seek = new AlertDialog.Builder(
							PantryFragment.this.getActivity());
					View exp =  LayoutInflater.from(PantryFragment.this.getActivity()).inflate(R.layout.seek,null,false);

					// set title
					final	SeekBar	colour =(SeekBar) exp.findViewById(R.id.seekBar1);
					seek.setTitle("Your Title");
					seek.setView(exp);
					seek.setTitle("Progress");
					seek.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {

							dialog.cancel();
							DecimalFormat df = new DecimalFormat("#.00");
							foodItem.setAmount(Double.parseDouble(df.format(foodItem.getAmount()*(1 -  (double)colour.getProgress()/100))));							if (foodItem.getAmount() <= 0){
								pantry.removeItem(foodItem);
								((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(foodItem);
								((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
								((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
							}
							slv.invalidateViews();
						}
					});
					seek.show();


					colour.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							// TODO Auto-generated method stub


						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

							//	progressBar.setOnGenericMotionListener();

						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							progressBar.setProgress(colour.getMax()-colour.getProgress());


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
			ImageView expirationWarning = (ImageView) convertView.findViewById(R.id.expiration_warning);
			Calendar warningCal = Calendar.getInstance();
			warningCal.add(Calendar.DATE, 3);
			Calendar expireCal = Calendar.getInstance();
			if (foodItem.getExperiationDate().before(expireCal.getTime())) {
				expirationWarning.setImageDrawable(getResources().getDrawable(R.drawable.expired));
			} else if (foodItem.getExperiationDate().before(warningCal.getTime())) {
				expirationWarning.setImageDrawable(getResources().getDrawable(R.drawable.warning_yellow));
			} else {
				expirationWarning.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
			}
			return convertView;
		}
	}

	public void addNewItem(FoodItem fi) {
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

	public void editItem(Dialog editDialog, FoodItem food) {
		((ExpandableListAdapter) elv.getExpandableListAdapter()).removeFoodItem(food);
		EditText nameText = (EditText) editDialog.findViewById(R.id.nameEdit);
		EditText quantityText = (EditText) editDialog.findViewById(R.id.quantityEdit);
		Spinner categoryText = (Spinner) editDialog.findViewById(R.id.category_spinner);
		Spinner unitText = (Spinner) editDialog.findViewById(R.id.unit_spinner);
		DatePicker expirationDate = (DatePicker) editDialog.findViewById(R.id.dpResult);
		EditText priceText = (EditText) editDialog.findViewById(R.id.priceEdit);

		View focusView = null;
		boolean filled = false; 


		if (!TextUtils.isEmpty(nameText.getText().toString()) && !TextUtils.isEmpty(quantityText.getText().toString())&& !TextUtils.isEmpty(priceText.getText().toString()))
			filled = true;

		if (!filled) {
			if (TextUtils.isEmpty(nameText.getText().toString())) {
				nameText.setError(getString(R.string.error_field_required));
				focusView = nameText;
			} else if (TextUtils.isEmpty(quantityText.getText().toString())) {
				quantityText.setError(getString(R.string.error_field_required));
				focusView = quantityText;									
			}else if (TextUtils.isEmpty(priceText.getText().toString())) {
				priceText.setError(getString(R.string.error_field_required));
				focusView = priceText;									
			}

			focusView.requestFocus();

		} else {		
			String name = nameText.getText().toString();
			double quantity = Double.parseDouble(quantityText.getText().toString());
			String unit = unitText.getSelectedItem().toString();
			String category = categoryText.getSelectedItem().toString();
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(expirationDate.getYear(), expirationDate.getMonth(), expirationDate.getDayOfMonth());
			Date expDate = cal.getTime(); 
			food.setName(name);
			food.setAmount(quantity);
			double price = Double.parseDouble(priceText.getText().toString());
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
			if (pantry.getHowSorted() == 0) {
				pantry.categorySort();
			} else if (pantry.getHowSorted() == 1) {
				pantry.alphabeticalSort();
			} else if (pantry.getHowSorted() == 2) {
				pantry.expirationSort();
			}
			((FoodItemsAdapter) lv.getAdapter()).notifyDataSetChanged();
			((ExpandableListAdapter) elv.getExpandableListAdapter()).addFoodItem(food);
			((ExpandableListAdapter) elv.getExpandableListAdapter()).notifyDataSetChanged();
			lv.invalidateViews();
			elv.invalidateViews();

			editDialog.dismiss();
		}

	}
}

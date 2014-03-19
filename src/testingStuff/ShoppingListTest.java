package testingStuff;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.thundersnacks.virtualpantrymodel.FoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;
import com.thundersnacks.virtualpantrymodel.Pantry;
import com.thundersnacks.virtualpantrymodel.ShoppingList;
import com.thundersnacks.virtualpantrymodel.StandardFoodItem;

import junit.framework.TestCase;

public class ShoppingListTest extends TestCase {

	private Pantry testP = new Pantry("TP", 69);
	private ShoppingList testS = new ShoppingList();
	/*public void testShoppingList() {
		fail("Not yet implemented");
	}*/

	public void testAddItem() {
		Date myDate = new Date();
		FoodItem testFood = new StandardFoodItem("name", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
		assertTrue("The add function must return true", testS.addItem(testFood));
	}

	public void testRemoveItem() {
		Date myDate = new Date();
		FoodItem testFood1 = new StandardFoodItem("name1", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
		testS.addItem(testFood1);
		assertTrue("The add function must return true", testS.removeItem(testFood1));
	}

	public void testRemoveItemByName() {
		Date myDate = new Date();
		FoodItem testFood2 = new StandardFoodItem("name2", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
		testS.addItem(testFood2);
		assertTrue("The add function must return true", testS.removeItemByName("name2"));
	}

	//needs to be updated once database ID's are generated. 
	public void testGetDatabaseId() {
		int a = testS.getDatabaseId();
		assertEquals("Database ID incorrect", a, testS.getDatabaseId());
	}

	public void testSetPantry() {
		testS.setPantry(testP);
		assertEquals("Database ID incorrect", testP, testS.getPantry());
	}
	
	public void testGetPantry() {
		testS.setPantry(testP);
		assertEquals("Database ID incorrect", testP, testS.getPantry());
	}

	public void testGetItems() {
	Map<FoodItem, Boolean> itemz = null;
	Date myDate = new Date();
	FoodItem testFood3 = new StandardFoodItem("name1", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
	FoodItem testFood4 = new StandardFoodItem("name2", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
	itemz.put(testFood3,true);
	itemz.put(testFood4,true);
	assertEquals("Get list of items not correct", itemz, testS.getItems());
	}

	public void testGetItem() {
		Date myDate = new Date();
		FoodItem testFood5 = new StandardFoodItem("name", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
		testS.addItem(testFood5);
		Map<FoodItem, Boolean> itemz = testS.getItems();
		FoodItem testFood6 = null;
		for ( FoodItem key : itemz.keySet() ) 
		{
			if( key.getName().equals("name") )
				testFood6 = key;
		}
		assertEquals("Get item by name failure", testFood6, testS.getItem("name"));
	}

	public void testGetCheckedFoodItem() {
		Date myDate = new Date();
		FoodItem testFood7 = new StandardFoodItem("name", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
		testS.addItem(testFood7);
		Map<FoodItem, Boolean> itemz = testS.getItems();
		FoodItem testFood8 = null;
		for ( FoodItem key : itemz.keySet() ) 
		{
			if( itemz.get(key).equals(false) )
				{
					testS.removeItem(key);
					testFood8 = key;
				}
		}
		assertEquals("Get checked food ttem by name failure", testFood8, testS.getCheckedFoodItem());
	}

	public void testSetItemMapValue() {
		Map<FoodItem, Boolean> itemz = testS.getItems();
		Date myDate = new Date();
		FoodItem testFood9 = new StandardFoodItem("name", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
		itemz.put(testFood9, true);
		assertNotNull("Set item map value failure",itemz.get(testFood9));
	}

	public void testIsInShoppingList(){
		Map<FoodItem, Boolean> itemz = testS.getItems();
		Date myDate = new Date();
		FoodItem testFood0 = new StandardFoodItem("name", 0,myDate,"1 pound","pic",FoodItemCategory.OTHER);
		testS.addItem(testFood0);
		assertTrue("Is in shopping list failure", testS.isInShoppingList(testFood0));
	}
	
	
	
}

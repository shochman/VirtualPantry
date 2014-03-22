package testingStuff;

import java.util.Date;
import com.thundersnacks.virtualpantrymodel.*;
import junit.framework.TestCase;

public class PantryTest extends TestCase {
	
	 private Pantry tester = new Pantry("TP", 69);
	 
	public void testGetName() {
		assertEquals("Name must be TP", "TP", tester.getName());
	}

	public void testSetName() {
		tester.setName("TP1");
		assertEquals("Name must be TP1", "TP1", tester.getName());
	}

	public void testGetDatabaseId() {
		assertEquals("DatabaseId must equal 69", 69, tester.getDatabaseId());
	}

	public void testGetShoppingList() {
		assertNotNull("Shopping list must not be Null", tester.getShoppingList());
	}

	public void testAddItem() {
		Date myDate = new Date();
		FoodItem testFood = new StandardFoodItem("name", 0,myDate,1, FoodItemUnit.POUNDS,"pic",FoodItemCategory.OTHER);
		assertTrue("The add function must return true", tester.addItem(testFood));
	}

	public void testRemoveItem() {
		Date myDate = new Date();
		FoodItem testFood1 = new StandardFoodItem("name1", 1,myDate, 2, FoodItemUnit.POUNDS,"pic1",FoodItemCategory.OTHER);
		tester.addItem(testFood1);
		assertTrue("The remove function must return true", tester.removeItem(testFood1));
	}

}

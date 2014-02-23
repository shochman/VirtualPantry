package testingStuff;

import java.util.Date;
import com.thundersnacks.virtualpantrymodel.*;

import junit.framework.TestCase;

public class FoodItemTest extends TestCase {
	private Date myDate=new Date();
	private FoodItem testFood = new StandardFoodItem("food", 42,myDate,"1 pound","pic",FoodItemCategory.OTHER);
	 
	/*public void testFoodItem() {
		fail("Not yet implemented");
	}*/

	public void testGetName() {
		assertEquals("Name must be food", "food", testFood.getName());
	}

	public void testSetName() {
		testFood.setName("food1");
		assertEquals("Name must be food1", "food1", testFood.getName());
	}

	public void testGetDatabaseId() {
		assertEquals("DatabadeId must equal 42", 42, testFood.getDatabaseId());
	}
/*
	public void testGetExperiationDate() {
		fail("Not yet implemented");
	}

	public void testSetExperiationDate() {
		fail("Not yet implemented");
	}

	public void testGetAmount() {
		fail("Not yet implemented");
	}

	public void testSetAmount() {
		fail("Not yet implemented");
	}

	public void testGetPicture() {
		fail("Not yet implemented");
	}

	public void testSetPicture() {
		fail("Not yet implemented");
	}

	public void testGetCategory() {
		fail("Not yet implemented");
	}

	public void testSetCategory() {
		fail("Not yet implemented");
	}
*/
}

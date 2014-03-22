package testingStuff;

import java.util.Date;
import com.thundersnacks.virtualpantrymodel.*;

import junit.framework.TestCase;

public class FoodItemTest extends TestCase {
	private Date myDate=new Date();
	private FoodItem testFood = new StandardFoodItem("food", 42,myDate,1, FoodItemUnit.UNITLESS,"pic",FoodItemCategory.OTHER);

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

	public void testGetExperiationDate() {
		assertEquals("Get experiation date failure", myDate, testFood.getExperiationDate());
	}

	public void testSetExperiationDate() {
		Date myDate1 = new Date();
		testFood.setExperiationDate(myDate1);
		assertEquals("Change experiation date failure", myDate1, testFood.getExperiationDate());
	}

	public void testGetAmount() {
		assertEquals("Get amount failure", 1, testFood.getAmount());
	}

	public void testSetAmount() {
		testFood.setAmount(2);
		assertEquals("Change amount failure", 2, testFood.getAmount());
	}

	public void testGetPicture() {
		assertEquals("Get pic failure", "pic", testFood.getPicture());
	}

	public void testSetPicture() {
		testFood.setPicture("picture");
		assertEquals("Set pic failure", "picture", testFood.getPicture());
	}

	public void testGetCategory() {
		assertEquals("Get  category failure", FoodItemCategory.OTHER, testFood.getCategory());
	}

	public void testSetCategory() {
		testFood.setCategory(FoodItemCategory.BEVERAGE);
		assertEquals("Set category failure", FoodItemCategory.BEVERAGE, testFood.getCategory());
	}

}

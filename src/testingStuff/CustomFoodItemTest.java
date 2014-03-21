package testingStuff;

import java.util.Date;

import com.thundersnacks.virtualpantrymodel.CustomFoodItem;
import com.thundersnacks.virtualpantrymodel.FoodItemCategory;

import junit.framework.TestCase;

public class CustomFoodItemTest extends TestCase {
	private Date myDate=new Date();
	private CustomFoodItem testI = new CustomFoodItem("test food", 42, myDate, "1 gallon", 
			"picture", FoodItemCategory.OTHER, 0 );
	
	public void testGetAssociatedUserId() {
		assertEquals("Get associate used ID failure", 0, testI.getAssociatedUserId());
	}

}

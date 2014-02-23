package testingStuff;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestingSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(TestingSuite.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CustomFoodItemTest.class);
		suite.addTestSuite(FoodItemTest.class);
		suite.addTestSuite(PantryTest.class);
		suite.addTestSuite(RegistrationTest.class);
		suite.addTestSuite(ShoppingListTest.class);
		suite.addTestSuite(StandardFoodItemTest.class);
		//$JUnit-END$
		return suite;
	}

}

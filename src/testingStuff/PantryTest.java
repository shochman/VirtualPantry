package testingStuff;

import com.thundersnacks.virtualpantrymodel.Pantry;

import junit.framework.TestCase;

public class PantryTest extends TestCase {
	 private Pantry tester = new Pantry("TestPantry", 69);
	public void testPantry() {
		fail("Not yet implemented");
	}

	public void testGetName() {
		assertEquals("Name must be TestPantry", "TestPantry", tester.getName());
	}

	public void testSetName() {
		fail("Not yet implemented");
	}

	public void testGetDatabaseId() {
		fail("Not yet implemented");
	}

	public void testGetShoppingList() {
		fail("Not yet implemented");
	}

	public void testAddItem() {
		fail("Not yet implemented");
	}

	public void testRemoveItem() {
		fail("Not yet implemented");
	}

	public void testIterator() {
		fail("Not yet implemented");
	}

	public void testGetItemsByCategory() {
		fail("Not yet implemented");
	}

}

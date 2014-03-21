package testingStuff;

import com.thundersnacks.virtualpantrymodel.User;

import junit.framework.TestCase;

public class UserTest extends TestCase {

	private User testU = new User("name","test@test.com","password","password");
	
	public void testGetUsername() {
		assertEquals("Get username malfunction","name",testU.getUsername());
	}

	public void testGetEmail() {
		assertEquals("Get email malfunction","test@test.com",testU.getEmail());
	}

	public void testGetPassword() {
		assertEquals("Get password malfunction","password",testU.getPassword());
	}

	public void testGetConfirmPassword() {
		assertEquals("Get confirm password malfunction","password",testU.getConfirmPassword());
	}

	public void testValidEmail() {
		assertTrue("Valid email error", testU.validEmail());
	}

	public void testMatchingPasswords() {
		assertTrue("Check password error", testU.matchingPasswords());
	}

	public void testValidPassword() {
		assertTrue("Valid password error", testU.validPassword());
	}

}

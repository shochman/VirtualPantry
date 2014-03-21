<<<<<<< HEAD
package com.thundersnacks.virtualpantrymodel;

/**
 * Created with IntelliJ IDEA.
 * User: Owner
 * Date: 2/5/14
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomFoodItem extends FoodItem {
    private int associatedUserId;
    private int fake;
}
=======
package com.thundersnacks.virtualpantrymodel;

import java.util.Date;

public class CustomFoodItem extends FoodItem {
	
	private int associatedUserId;
	
	public CustomFoodItem( String name, int databaseId, Date expDate, String amount, 
			String pic, FoodItemCategory cat, int associatedUserId ) {
				
		super( name, databaseId, expDate, amount, pic, cat );
		this.associatedUserId = associatedUserId;
		
	}
	
	public int getAssociatedUserId() {
		
		return this.associatedUserId;
		
	}

}
>>>>>>> d8427cccbe098bc19e19031876695251422b26bc

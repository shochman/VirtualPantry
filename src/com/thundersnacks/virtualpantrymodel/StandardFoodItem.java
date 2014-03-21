<<<<<<< HEAD
package com.thundersnacks.virtualpantrymodel;

/**
 * Created with IntelliJ IDEA.
 * User: Owner
 * Date: 2/5/14
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class StandardFoodItem extends FoodItem {
}
=======
package com.thundersnacks.virtualpantrymodel;

import java.util.Date;

public class StandardFoodItem extends FoodItem {
	
	public StandardFoodItem ( String name, int databaseId, Date expDate, String amount, 
			String pic, FoodItemCategory cat ) {
		
		super( name, databaseId, expDate, amount, pic, cat );
		
	}

}
>>>>>>> d8427cccbe098bc19e19031876695251422b26bc

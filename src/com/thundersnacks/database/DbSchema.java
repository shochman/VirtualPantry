package com.thundersnacks.database;

import android.provider.BaseColumns;

public final class DbSchema {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DbSchema() {}
	
    /* Inner class that defines the table contents */
    public static abstract class UserTable implements BaseColumns {
        public static final String TABLE = "users";
        public static final String _ID = "uid";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class PantryTable implements BaseColumns {
        public static final String TABLE = "pantry";
        public static final String _ID = "id";
        public static final String COLUMN_PANTRY_NAME = "name";
        public static final String COLUMN_ASSOCIATED_USER = "userid";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class ShoppingListTable implements BaseColumns {
        public static final String TABLE = "shoppinglist";
        public static final String _ID = "id";
        public static final String COLUMN_ASSOCIATED_PANTRY = "pantryid";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FoodItemTable implements BaseColumns {
        public static final String TABLE = "fooditem";
        public static final String _ID = "id";
        public static final String COLUMN_ASSOCIATED_PANTRY = "pantryid";
        public static final String COLUMN_ASSOCIATED_SHOPPING_LIST = "shoppinglistid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EXPIRATION_DATE = "expdate";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PICTURE = "picture";
    }
    
}
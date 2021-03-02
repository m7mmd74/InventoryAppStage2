package com.example.mohamed.inventoryappstage2.data;

/**
 * Created by Mohamed on 29/08/2018.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {

    //URI components
    public static final String CONTENT_AUTHORITY = "com.example.android.products";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    //Constructor
    ProductContract() {
    }

    //Nested class
    public static final class ProductEntry implements BaseColumns {

        //URI components
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //Table description
        public final static String TABLE_NAME = "products";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_NUMBER_OF_PRODUCTS = "number_of_products";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_NAME_OF_THE_SUPPLIER = "supplier_name";
        public final static String COLUMN_PHONE_OF_THE_SUPPLIER = "supplier_phone";

        //Projection of the entire table
        public final static String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_NUMBER_OF_PRODUCTS,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_NAME_OF_THE_SUPPLIER,
                ProductEntry.COLUMN_PHONE_OF_THE_SUPPLIER};
    }
}
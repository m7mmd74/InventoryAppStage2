package com.example.mohamed.inventoryappstage2.data;

/**
 * Created by Mohamed on 29/08/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mohamed.inventoryappstage2.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {

    //Database details
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create the SQL command which will make the products table in the database
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_NUMBER_OF_PRODUCTS + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_NAME_OF_THE_SUPPLIER + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PHONE_OF_THE_SUPPLIER + " TEXT NOT NULL);";

        //Run the SQL command
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Do not upgrade this project's database version
    }
}
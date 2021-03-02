package com.example.mohamed.inventoryappstage2.data;

/**
 * Created by Mohamed on 29/08/2018.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.mohamed.inventoryappstage2.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    //Branches for UriResolver
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //Create URIs patterns for comparing
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor cursor;
        //Set database to Read mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Check all URIs patterns for a match
        int match = sUriMatcher.match(uri);

        //Matched with the database URI
        if (match == PRODUCTS) {
            //Query the whole database
            cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);
        }
        //Matched with a product from the database
        else if (match == PRODUCT_ID) {
            //To query a specific row in the database
            selection = ProductEntry._ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

            cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);
        } else {
            //Should never reach this point. There are only 2 branches that URI can match to
            throw new IllegalArgumentException("Invalid URI " + uri);
        }

        //Notify when a change occur
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        if (match == PRODUCTS)
            return insertProduct(uri, contentValues);
        //the insert can be done only for the whole table URI, so throw error in any other case
        throw new IllegalArgumentException("Invalid insertion: " + uri);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        if (match == PRODUCTS) {
            //Update in all database where possible
            return updateProduct(uri, contentValues, selection, selectionArgs);
        } else if (match == PRODUCT_ID) {
            //Update the current product
            selection = ProductEntry._ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            return updateProduct(uri, contentValues, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Invalid update: " + uri);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        if (match == PRODUCTS) {
            // Delete all rows that match the selection and selection args
            rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
        } else if (match == PRODUCT_ID) {
            // Delete a single row given by the ID in the URI
            selection = ProductEntry._ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Invalid deletion: " + uri);
        }

        if (rowsDeleted != 0) {
            //at least one row deleted, notify
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        if (match == PRODUCTS) {
            return ProductEntry.CONTENT_LIST_TYPE;
        } else if (match == PRODUCT_ID) {
            return ProductEntry.CONTENT_ITEM_TYPE;
        } else {
            throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0) {
            //Nothing to update
            return 0;
        }

        //For all input values, check if they are present in the values and are valid
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null)
                throw new IllegalArgumentException("Product requires a name");
        }

        if (values.containsKey(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS)) {
            Integer numberOfProducts = values.getAsInteger(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS);
            if (numberOfProducts == null || numberOfProducts < 0)
                throw new IllegalArgumentException("The number of products is not valid");
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0)
                throw new IllegalArgumentException("The price is not valid");
        }

        if (values.containsKey(ProductEntry.COLUMN_NAME_OF_THE_SUPPLIER)) {
            String nameSupplier = values.getAsString(ProductEntry.COLUMN_NAME_OF_THE_SUPPLIER);
            if (nameSupplier == null)
                throw new IllegalArgumentException("Name of the supplier cannot be null");
        }

        if (values.containsKey(ProductEntry.COLUMN_PHONE_OF_THE_SUPPLIER)) {
            String phoneSupplier = values.getAsString(ProductEntry.COLUMN_PHONE_OF_THE_SUPPLIER);
            if (phoneSupplier == null)
                throw new IllegalArgumentException("Phone of the supplier cannot be null");
        }


        //There are valid values to update, set database in write mode
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Do the update
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            //If an update occurred, notify
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        //No values to be inserted
        if (values.size() == 0)
            return null;

        //Get all values
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        Integer numberOfProducts = values.getAsInteger(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS);
        Integer productPrice = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        String nameSupplier = values.getAsString(ProductEntry.COLUMN_NAME_OF_THE_SUPPLIER);
        String phoneSupplier = values.getAsString(ProductEntry.COLUMN_PHONE_OF_THE_SUPPLIER);

        //Check values
        if (name == null)
            throw new IllegalArgumentException("Name field is mandatory!");
        if (numberOfProducts == null || numberOfProducts < 0)
            throw new IllegalArgumentException("The number of products is not valid!");
        if (productPrice == null || productPrice < 0)
            throw new IllegalArgumentException("The product price is not valid!");
        if (nameSupplier == null)
            throw new IllegalArgumentException("Name of the supplier field is mandatory!");
        if (phoneSupplier == null)
            throw new IllegalArgumentException("Phone of the supplier field is mandatory!");

        // Set database in write mode
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Do the insertion
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            // Insertion failed
            return null;
        }

        // Notify when data change
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the uri with id of the current row
        return ContentUris.withAppendedId(uri, id);
    }
}
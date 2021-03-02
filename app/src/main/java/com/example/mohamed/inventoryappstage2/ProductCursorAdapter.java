package com.example.mohamed.inventoryappstage2;

/**
 * Created by Mohamed on 29/08/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.mohamed.inventoryappstage2.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        final String mID = cursor.getString(cursor.getColumnIndex(ProductEntry._ID));

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.item_product_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_price);

        // Get the index of each column from the cursor
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        // Get tge actual values
        final String productName = cursor.getString(nameColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);

        // Set the values on the screen
        nameTextView.setText(productName);
        quantityTextView.setText(productQuantity);
        priceTextView.setText(productPrice);

        //Add a button listener to the sale button
        TextView btnSale = (TextView) view.findViewById(R.id.btn_sale);
        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create args and values for the query
                String[] tempArgs = {mID};
                int val = Integer.parseInt(productQuantity) - 1;
                if (val >= 0) {
                    //Create the query
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS, val);
                    //Update the database
                    context.getContentResolver().update(ProductEntry.CONTENT_URI, values, ProductEntry._ID + "=?", tempArgs);
                }
            }
        });
    }
}

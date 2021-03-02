package com.example.mohamed.inventoryappstage2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mohamed.inventoryappstage2.data.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;

    private ProductCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create the list view
        ListView productListView = (ListView) findViewById(R.id.database_display);
        //Create the empty view
        View emptyView = findViewById(R.id.empty_view);
        //Attach the empty view to the list view
        productListView.setEmptyView(emptyView);
        //Create a new ProductCursorAdapter
        mAdapter = new ProductCursorAdapter(this, null);
        //Attach the adapter to the list view
        productListView.setAdapter(mAdapter);

        //Set clicks for all items in the list view
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Destination of intent
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                //Uri to the current object
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                //Add the uri to intent
                intent.setData(currentProductUri);
                //Start intent
                startActivity(intent);
            }
        });
        //initialize the loader manager
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void insertDummyProduct() {
        //Create the structure of the dummy element
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Electric Devices");
        values.put(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS, 10);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 50);
        values.put(ProductEntry.COLUMN_NAME_OF_THE_SUPPLIER, "Toshiba");
        values.put(ProductEntry.COLUMN_PHONE_OF_THE_SUPPLIER, "+201007029829");

        //Insert into the dataabse
        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check item selected
        switch (item.getItemId()) {
            case R.id.add_new_product:
                Intent intent = new Intent(this, EditorActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_insert_dummy_data:
                insertDummyProduct();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ProductEntry.CONTENT_URI, ProductEntry.projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void deleteAllProducts() {
        getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
    }
}
package com.example.mohamed.inventoryappstage2;

/**
 * Created by Mohamed on 29/08/2018.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.inventoryappstage2.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;
    //Keeps track if a change occurred
    boolean productChanged;
    //All fields in the Editor Activity
    private EditText mProductName;
    private EditText mNumberOfProducts;
    private EditText mProductPrice;
    private EditText mNameOfSupplier;
    private EditText mPhoneOfSupplier;
    private TextView mOrderButton;
    private TextView mAddButton;
    private TextView mSubtractButton;
    private TextView mDeleteProductButton;
    //The URI of the current product. Null for an insertion
    private Uri mCurrentProductUri;
    //General touch listener
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);

        //Retrieve data from intent if a product was passed
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //Find all fields
        mProductName = (EditText) findViewById(R.id.product_name);
        mNumberOfProducts = (EditText) findViewById(R.id.product_quantity);
        mProductPrice = (EditText) findViewById(R.id.product_price);
        mNameOfSupplier = (EditText) findViewById(R.id.supplier_name);
        mPhoneOfSupplier = (EditText) findViewById(R.id.supplier_phone_number);
        mOrderButton = (TextView) findViewById(R.id.order_btn);
        mAddButton = (TextView) findViewById(R.id.product_quantity_add);
        mSubtractButton = (TextView) findViewById(R.id.product_quantity_substract);
        mDeleteProductButton = (TextView) findViewById(R.id.action_delete);

        //There is a product passed through URI
        if (mCurrentProductUri != null) {
            //Show buttons
            mOrderButton.setVisibility(View.VISIBLE);
            mAddButton.setVisibility(View.VISIBLE);
            mSubtractButton.setVisibility(View.VISIBLE);
            mDeleteProductButton.setVisibility(View.VISIBLE);

            setTitle(getString(R.string.editor_activity_title_edit_product));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            //Hide buttons
            mOrderButton.setVisibility(View.GONE);
            mAddButton.setVisibility(View.GONE);
            mSubtractButton.setVisibility(View.GONE);
            mDeleteProductButton.setVisibility(View.GONE);

            setTitle(getString(R.string.editor_activity_title_new_product));
        }

        //Add touch listeners to all fields
        mProductName.setOnTouchListener(mTouchListener);
        mNumberOfProducts.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mNameOfSupplier.setOnTouchListener(mTouchListener);
        mPhoneOfSupplier.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // Save product to database
                if (saveProduct() == 0) {
                    //Finish the activity
                    finish();
                } else {
                    Toast.makeText(this, R.string.change_unsaved,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                if (productChanged) {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Return to the parent activity
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };
                    discardDialog(discardButtonClickListener);
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!productChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Use finish instead of UP button because if you come from another app you must go back to it.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        discardDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mCurrentProductUri, ProductEntry.projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }

        if (cursor.moveToFirst()) {
            //get all values indexes
            final String mID = cursor.getString(cursor.getColumnIndex(ProductEntry._ID));
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int supplierNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME_OF_THE_SUPPLIER);
            int supplierPhoneIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PHONE_OF_THE_SUPPLIER);

            //get the actual values
            final String productName = cursor.getString(nameColumnIndex);
            final String productQuantity = cursor.getString(quantityColumnIndex);
            String productPrice = cursor.getString(priceColumnIndex);
            String supplierName = cursor.getString(supplierNameIndex);
            final String supplierPhone = cursor.getString(supplierPhoneIndex);

            //set the values
            mProductName.setText(productName);
            mNumberOfProducts.setText(productQuantity);
            mProductPrice.setText(productPrice);
            mNameOfSupplier.setText(supplierName);
            mPhoneOfSupplier.setText(supplierPhone);

            //click listener for order button
            mOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + supplierPhone));

                    if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditorActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        startActivity(intent);
                    }
                }
            });

            //click listener for adding button
            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create the values and args
                    int val = Integer.parseInt(productQuantity) + 1;
                    String[] tempArgs = {mID};

                    //create the actual query
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS, val);

                    //make an update
                    getContentResolver().update(mCurrentProductUri, values, ProductEntry._ID, tempArgs);

                    //mark a change
                    productChanged = true;
                }
            });

            //click listener for subtracting button
            mSubtractButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int val = Integer.parseInt(productQuantity) - 1;
                    String[] aux = {mID};

                    if (val >= 0) {
                        //create the actual query
                        ContentValues values = new ContentValues();
                        values.put(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS, val);

                        //make an update
                        getContentResolver().update(mCurrentProductUri, values, ProductEntry._ID, aux);

                        //mark a change
                        productChanged = true;
                    }
                }
            });

            //click listener for delete
            mDeleteProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mNumberOfProducts.setText("");
        mProductPrice.setText("");
        mNameOfSupplier.setText("");
        mPhoneOfSupplier.setText("");
    }

    private int saveProduct() {
        //Get the input data
        String itemName = ((EditText) findViewById(R.id.product_name)).getText().toString().trim();
        String itemPrice = ((EditText) findViewById(R.id.product_price)).getText().toString().trim();
        String itemQuantity = ((EditText) findViewById(R.id.product_quantity)).getText().toString().trim();
        String itemSupplierName = ((EditText) findViewById(R.id.supplier_name)).getText().toString().trim();
        String itemSupplierPhone = ((EditText) findViewById(R.id.supplier_phone_number)).getText().toString().trim();

        //Check the input data
        if (itemName.equals("") || itemPrice.equals("") || itemQuantity.equals("") || itemSupplierName.equals("") || itemSupplierPhone.equals(""))
            return -1;        //a field is empty

        //create key-value pairs
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, itemName);
        values.put(ProductEntry.COLUMN_NUMBER_OF_PRODUCTS, itemQuantity);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, itemPrice);
        values.put(ProductEntry.COLUMN_NAME_OF_THE_SUPPLIER, itemSupplierName);
        values.put(ProductEntry.COLUMN_PHONE_OF_THE_SUPPLIER, itemSupplierPhone);

        //update the product from mCurrentProductUri
        if (mCurrentProductUri != null) {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0)
                Toast.makeText(this, getString(R.string.update_product_failed), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.update_product_successful), Toast.LENGTH_SHORT).show();

        }
        //insert a new product
        else {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null)
                Toast.makeText(this, getString(R.string.insert_product_failed), Toast.LENGTH_SHORT).show();
            else
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.insert_product_successful), Toast.LENGTH_SHORT).show();

        }
        return 0;
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0)
                Toast.makeText(this, getString(R.string.delete_product_failed), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.delete_product_successful), Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();
    }

    private void discardDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create the discard dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_msg);
        builder.setPositiveButton(R.string.leave, discardButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteDialog() {
        //Create the delete dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
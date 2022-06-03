package com.example.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Allows user to create a new inventory item or edit an existing one.
 */
public class InventoryEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the inventory data loader */
    private static final int EXISTING_INVENTORY_LOADER = 0;

    /** Content URI for the existing inventory item (null if it's a new inventory item) */
    private Uri mCurrentInventoryItemUri;

    /** EditText field to enter the inventory items name */
    private EditText mNameEditText;

    /** EditText field to enter the inventory items quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the inventory items price */
    private EditText mPriceEditText;

    /** Boolean flag that keeps track of whether the inventory item has been edited (true) or not (false) */
    private boolean mInventoryItemHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mInventoryItemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new inventroy item or editing an existing one.
        Intent intent = getIntent();
        mCurrentInventoryItemUri = intent.getData();

        // If the intent DOES NOT contain an inventory item content URI, then we know that we are
        // creating a new inventory item.
        if (mCurrentInventoryItemUri == null) {
            // This is a new inventory item, so change the app bar to say "Add an inventory item"
            setTitle(getString(R.string.editor_activity_title_new_inventory_item));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an inventory item that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing inventory item, so change app bar to say "Edit Inventory Item"
            setTitle(getString(R.string.editor_activity_title_edit_inventory_item));

            // Initialize a loader to read the inventory data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_inventory_item_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_inventory_item_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_inventory_item_price);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
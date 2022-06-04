package com.example.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventoryapp.data.InventoryContract;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of inventory item data as its data source. This adapter knows
 * how to create list items for each row of inventory data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context   app context
     * @param cursor    The cursor from which to get the data. The cursor is already
     *                  moved to the correct position.
     * @param viewGroup The parent to which the new view is attached to
     * @return the newly created list item view.
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    /**
     * This method binds the inventory item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the inventory item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);

        // Find the columns of inventory item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);

        final int rowId = cursor.getInt(idColumnIndex);

        // Read the inventory item attributes from the Cursor for the current item
        String name = cursor.getString(nameColumnIndex);
        final int itemQuantity = cursor.getInt(quantityColumnIndex);
        String price = cursor.getString(priceColumnIndex);

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(name);
        quantityTextView.setText(String.valueOf(itemQuantity));
        priceTextView.setText(price);

        String quantityDisplay;

        if (itemQuantity <= 1) {
            quantityDisplay = itemQuantity + " " + context.getResources().getString(R.string.unit);
        } else {
            quantityDisplay = itemQuantity + " " + context.getResources().getString(R.string.units);
        }

        quantityTextView.setText(quantityDisplay);

        LinearLayout parentView = view.findViewById(R.id.parent);
        parentView.setOnClickListener(view1 -> {
            //Open editor activity
            Intent intent = new Intent(context, InventoryEditorActivity.class);

            //Form the content URI that represents click item.
            Uri currentInventoryUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, rowId);

            //set the URI on the data field of the intent
            intent.setData(currentInventoryUri);
            context.startActivity(intent);
        });

        ImageView saleButton = view.findViewById(R.id.sale_button);
        saleButton.setClickable(true);
        saleButton.setEnabled(true);
        saleButton.setOnClickListener(view12 -> {
            String text = quantityTextView.getText().toString();
            String[] splitText = text.split(" ");
            int quantity = Integer.parseInt(splitText[0]);

            if (quantity == 0) {
                Toast.makeText(context, R.string.no_more_stock, Toast.LENGTH_SHORT).show();
            } else if (quantity > 0) {
                quantity = quantity - 1;
                String quantityString = Integer.toString(quantity);
                ContentValues values = new ContentValues();
                values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, quantityString);

                //Form the content URI that represents click item.
                Uri currentInventoryUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, rowId);

                int rowsAffected = context.getContentResolver().update(currentInventoryUri, values, null, null);

                if (rowsAffected != 0) {
                    //update text view if database update is successful
                    quantityTextView.setText(quantityDisplay);
                } else {
                    Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
package com.example.android.inventory_part1.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory_part1.CatalogActivity;
import com.example.android.inventory_part1.data.InventoryContract.InventoryEntry;

import com.example.android.inventory_part1.R;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context,Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent, false);
        }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        String typeString;

        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView phoneTextView = view.findViewById(R.id.phone);
        Button sellButton = view.findViewById(R.id.action_sale);
        Button supplierButton = view.findViewById(R.id.action_phone);
        //TextView itemTypeTextView = view.findViewById(R.id.item_type);
        //TextView summaryTextView = view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);
        //int typeColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_TYPE);
        //int summaryColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);

        String inventoryName = cursor.getString(nameColumnIndex);
        String inventoryPrice = cursor.getString(priceColumnIndex);
        String inventoryQuantity = cursor.getString(quantityColumnIndex);
        String inventoryPhone = cursor.getString(phoneColumnIndex);

        /*if (TextUtils.isEmpty(inventoryQuanity)){
            inventoryQuanity = context.getString(R.string.unknown_supplier);
        }*/

        /* Revisores: acho que deve ter uma maneira melhor de fazer esta parte
        if (inventoryType == "0"){
            inventoryType = "Book";
        } else if (inventoryType == "1"){
            inventoryName = "Magazine";
        } else {
            inventoryType = "Other";
        }*/

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idIndex = cursor.getColumnIndex(InventoryEntry._ID);
                int quantityIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
                String inventoryId = cursor.getString(idIndex);
                String inventoryQtde = cursor.getString(quantityIndex);
                CatalogActivity catalogActivity = (CatalogActivity) context;
                catalogActivity.decreaseItem(Integer.valueOf(inventoryId), Integer.valueOf(inventoryQtde));
            }
        });

        supplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idIndex = cursor.getColumnIndex(InventoryEntry._ID);
                int phoneIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);
                String inventoryId = cursor.getString(idIndex);
                String inventoryPhone = cursor.getString(phoneIndex);
                CatalogActivity catalogActivity = (CatalogActivity) context;
                catalogActivity.contactSupplier(Integer.valueOf(inventoryId), String.valueOf(inventoryPhone));
            }
        });

        nameTextView.setText(inventoryName);
        priceTextView.setText(inventoryPrice);
        quantityTextView.setText(inventoryQuantity);
        phoneTextView.setText(inventoryPhone);
    }
}

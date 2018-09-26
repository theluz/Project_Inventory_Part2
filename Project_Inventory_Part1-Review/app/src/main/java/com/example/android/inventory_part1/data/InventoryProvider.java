package com.example.android.inventory_part1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.android.inventory_part1.data.InventoryContract.InventoryEntry;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryProvider extends ContentProvider {
    public InventoryDbHelper mInventoryDbHelper;
    static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);

    }

    @Override
    public boolean onCreate() {
        mInventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){
        SQLiteDatabase database = mInventoryDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                cursor = database.query(InventoryEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;

            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InventoryEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null, null, sortOrder);
                break;

                default:
                    throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
            }
    }

    private Uri insertInventory(Uri uri, ContentValues values){
        //Data validation Check
        String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (TextUtils.isEmpty(name)){
            //throw new IllegalArgumentException ("Item requires a Name");
            Toast.makeText(getContext(),"Item Name is required", Toast.LENGTH_SHORT).show();
            return null;
        }

        Integer type = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_TYPE);
        if (type == null || !InventoryEntry.isValidType(type)){
            //throw new IllegalArgumentException("Item requires a valid Type");
            Toast.makeText(getContext(),"Type is required", Toast.LENGTH_SHORT).show();
            return null;
        }

        Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0){
            //throw new IllegalArgumentException("Item requires a valid Price");
            Toast.makeText(getContext(),"Price is required", Toast.LENGTH_SHORT).show();
            return null;
        }

        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity < 0){
            //throw new IllegalArgumentException("Item requires a valid Quantity");
            Toast.makeText(getContext(),"Quantity is required", Toast.LENGTH_SHORT).show();
            return null;
        }

        String supplier = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
        if (supplier == null || TextUtils.isEmpty(supplier)){
            //throw new IllegalArgumentException("Item requires a valid Supplier Name");
            Toast.makeText(getContext(),"Supplier is required", Toast.LENGTH_SHORT).show();
            return null;
        }

        Integer phone = values.getAsInteger(InventoryEntry.COLUMN_SUPPLIER_PHONE);
        if (phone == null || phone <= 0){
            //throw new IllegalArgumentException("Item requires a valid Phone Number");
            Toast.makeText(getContext(),"Phone is required", Toast.LENGTH_SHORT).show();
            return null;
        }
        //Data validation Check END

        SQLiteDatabase database = mInventoryDbHelper.getWritableDatabase();
        long id = database.insert(InventoryEntry.TABLE_NAME, null,values);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return updateInventory(uri, contentValues,selection,selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri,ContentValues values, String selection, String[] selectionArgs){
        if (values.size() == 0){return 0;}

        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_NAME)){
            String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
            if (name == null || TextUtils.isEmpty(name)){
                //throw new IllegalArgumentException("Item Name is required");
                Toast.makeText(getContext(),"Name is required", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_TYPE)){
            Integer type = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_TYPE);
            if (type == null){
                throw new IllegalArgumentException("Item requires a Type");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_PRICE)){
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0){
                //throw new IllegalArgumentException("Item requires a valid Price");
                Toast.makeText(getContext(),"Price is required", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_PRODUCT_QUANTITY)){
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0){
                //throw new IllegalArgumentException("Item requires a valid Quantity");
                Toast.makeText(getContext(),"Quantity is required", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_NAME)){
            String supplier = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null || TextUtils.isEmpty(supplier)){
                //throw new IllegalArgumentException("Item requires a Supplier");
                Toast.makeText(getContext(),"Supplier is required", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_PHONE)){
            Integer phone = values.getAsInteger(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            if (phone == null){
                //throw new IllegalArgumentException("Item requires a valid Phone");
                Toast.makeText(getContext(),"Name is required", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        SQLiteDatabase database = mInventoryDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        int rowsDeleted;

        SQLiteDatabase database = mInventoryDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
                default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new  IllegalArgumentException("Unknown Uri " + uri + "with match " + match);
        }
    }
}


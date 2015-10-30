package com.uwp.kelly.productdatabase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Kelly on 10/28/2015.
 */
public class ProductProvider extends ContentProvider {

    private static final String AUTHORITY = "com.uwp.kelly.productdatabase.productprovider";
    private static final String BASE_PATH = "product";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    SQLiteDatabase database;
    // Constant to identify the requested operation

    private static final int PRODUCTS = 1;
    private static final int PRODUCTS_ID = 2;

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY,BASE_PATH,PRODUCTS);
        uriMatcher.addURI(AUTHORITY,BASE_PATH+"/#",PRODUCTS_ID);

    }

    public static final String CONTENT_ITEM_URI = "product_uri";

    @Override
    public boolean onCreate() {
        ProductOpenHelper helper = new ProductOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return database.query(ProductOpenHelper.PRODUCT_TABLE,ProductOpenHelper
                .ALL_COLS,selection,null,null,null,sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(ProductOpenHelper.PRODUCT_TABLE,null, values);
        return Uri.parse(BASE_PATH+"/"+id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(ProductOpenHelper.PRODUCT_TABLE,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return  database.update(ProductOpenHelper.PRODUCT_TABLE,values,selection,selectionArgs);
    }
}

package com.uwp.kelly.productdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kelly on 10/28/2015.
 */
public class ProductOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "product.db";

    public static final String PRODUCT_TABLE = "Products";
    public static final String PRODUCT_ID = "_id";
    public static final String PRODUCT_NAME= "productName";
    public static final String PRODUCT_PRICE= "productPrice";
    public static final String PRODUCT_QUANTITY= "productQuantity";
    public static final String PRODUCT_ADDED = "productAdded";
    public static final String PRODUCT_IMAGE = "productImage";
    public static final String[]ALL_COLS = {PRODUCT_ID,PRODUCT_NAME,PRODUCT_PRICE,PRODUCT_QUANTITY,PRODUCT_ADDED};

    private static final String TABLE_CREATE_UPDATE =
            "CREATE TABLE " + PRODUCT_TABLE + " (" +
                    PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PRODUCT_NAME + " TEXT COLLATE NOCASE, " +
                    PRODUCT_PRICE + " INTEGER, "+
                    PRODUCT_QUANTITY + " FLOAT, "+
                    PRODUCT_ADDED + " TEXT default CURRENT_TIMESTAMP" +
                    PRODUCT_IMAGE + " BLOB"+
                    ")";

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + PRODUCT_TABLE + " (" +
                    PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PRODUCT_NAME + " TEXT COLLATE NOCASE, " +
                    PRODUCT_PRICE + " INTEGER, "+
                    PRODUCT_QUANTITY + " FLOAT, "+
                    PRODUCT_ADDED + " TEXT default CURRENT_TIMESTAMP" +

                    ")";

    public ProductOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ PRODUCT_TABLE);
        onCreate(db);

    }
}

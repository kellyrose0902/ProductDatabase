package com.uwp.kelly.productdatabase;

import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Kelly on 10/29/2015.
 */
public class CustomViewBinder implements SimpleCursorAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if(columnIndex == cursor.getColumnIndex(ProductOpenHelper.PRODUCT_QUANTITY)){
            TextView tv = (TextView)view;
            int quantity = cursor.getInt(columnIndex);
            String edit = "Quantity: "+String.valueOf(quantity);
            tv.setText(edit);
            return true;
        }

        if(columnIndex == cursor.getColumnIndex(ProductOpenHelper.PRODUCT_PRICE)){
            TextView tv = (TextView)view;
            Float price = cursor.getFloat(columnIndex);
            String edit;
            int a;
            if (price==Math.ceil(price)){
                a = Math.round(price);
                edit = "$ "+String.valueOf(a);
            }
            else {
                 edit = "$ "+String.valueOf(price);
            }
            tv.setText(edit);
            return true;
        }

        /*if(columnIndex == cursor.getColumnIndex(ProductOpenHelper.PRODUCT_IMAGE)){
            ImageView iv = (ImageView)view;
            byte[] bb = cursor.getBlob(cursor.getColumnIndex(ProductOpenHelper.PRODUCT_IMAGE));
            if (bb!=null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bb, 0, bb.length);
                iv.setImageBitmap(bitmap);
            }
                return true;

        }*/

        return false;
    }
}

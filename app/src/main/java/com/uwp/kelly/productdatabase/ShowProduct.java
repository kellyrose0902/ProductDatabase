package com.uwp.kelly.productdatabase;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.desmond.squarecamera.CameraActivity;
import com.desmond.squarecamera.ImageUtility;

public class ShowProduct extends AppCompatActivity {
        private String action;
        private EditText input_name;
        private EditText input_quantity;
        private EditText input_price;
        private Button addB;
        private ImageView productImage;
        private String oldName;
        private String oldQuantity;
        private String oldPrice;
        private String filter;
        static final int REQUEST_IMAGE_CAPTURE = 1;
        private static final int REQUEST_CAMERA = 0;
        private static final int REQUEST_CAMERA_PERMISSION = 1;
        private Point mSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(ProductProvider.CONTENT_ITEM_URI);

        Display display = getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);

        addB = (Button)findViewById(R.id.add_update_button);
        input_name = (EditText)findViewById(R.id.input_name);
        input_quantity = (EditText)findViewById(R.id.input_quantity);
        input_price = (EditText) findViewById(R.id.input_price);

        if(uri == null){
            setTitle("New Product");
            action = Intent.ACTION_INSERT;
            addB.setText("ADD");
        }
        else {
            filter = ProductOpenHelper.PRODUCT_ID + " = " + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(ProductProvider.CONTENT_URI,ProductOpenHelper.ALL_COLS,filter,null,null);
            cursor.moveToFirst();
            oldName = cursor.getString(cursor.getColumnIndex(ProductOpenHelper.PRODUCT_NAME));
            oldQuantity = cursor.getString(cursor.getColumnIndex(ProductOpenHelper.PRODUCT_QUANTITY));
            oldPrice = cursor.getString(cursor.getColumnIndex(ProductOpenHelper.PRODUCT_PRICE));
            input_name.setText(oldName);
            input_quantity.setText(oldQuantity);
            input_price.setText(oldPrice);
            input_name.requestFocus();
            setTitle("Edit Product");
            action = Intent.ACTION_EDIT;
            addB.setText("UPDATE");

        }
        productImage = (ImageView)findViewById(R.id.input_image);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action.equals(Intent.ACTION_EDIT)){
            getMenuInflater().inflate(R.menu.menu_edit, menu);}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:
                deleteNote();
                finish();
        }


        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(ProductProvider.CONTENT_URI,filter,null);
        Toast.makeText(this,"Product deleted",Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertProduct(String productName,int productQuantity,float productPrice) {
        ContentValues values = new ContentValues();
        values.put(ProductOpenHelper.PRODUCT_NAME, productName);
        values.put(ProductOpenHelper.PRODUCT_QUANTITY, productQuantity);
        values.put(ProductOpenHelper.PRODUCT_PRICE, productPrice);
        Uri uri = getContentResolver().insert(ProductProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
        Log.d("MainActivity", "Insert note" + uri.getLastPathSegment());
    }
    private void onFinishEditing(){


        String name = input_name.getText().toString().trim();
        String quantity = input_quantity.getText().toString().trim();
        String price = input_price.getText().toString().trim();

        input_name = (EditText)findViewById(R.id.input_name);
        input_quantity = (EditText)findViewById(R.id.input_quantity);
        input_price = (EditText)findViewById(R.id.input_price);

        if (name.length()>0){
            switch (action){
                case Intent.ACTION_INSERT:
                    insertProduct(name,Integer.valueOf(quantity),Float.valueOf(price));
                    break;
                case Intent.ACTION_EDIT:
                    updateProduct(name, Integer.valueOf(quantity), Float.valueOf(price));

            }
            finish();
        }
        else {
            Toast toast = Toast.makeText(this, "Please input product name", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 70);
            toast.show();
        }

    }

    private void updateProduct(String name, Integer quantity, Float price) {
        ContentValues values= new ContentValues();
        values.put(ProductOpenHelper.PRODUCT_NAME, name);
        values.put(ProductOpenHelper.PRODUCT_QUANTITY, quantity);
        values.put(ProductOpenHelper.PRODUCT_PRICE, price);
        getContentResolver().update(ProductProvider.CONTENT_URI, values, filter, null);
        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }
    // Function to take picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    public void AddToDatabase(View v){
            onFinishEditing();
    }
    public void TakePhoto(View v){
        dispatchTakePictureIntent();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            productImage.setImageBitmap(imageBitmap);
        }*/
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CAMERA) {
            Uri photoUri = data.getData();
            // Get the bitmap in according to the width of the device
            Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), mSize.y, mSize.x);
            ((ImageView) findViewById(R.id.input_image)).setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void requestForCameraPermission(View view) {
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(ShowProduct.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ShowProduct.this, permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);
            }
        } else {
            launch();
        }
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        new AlertDialog.Builder(ShowProduct.this)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowProduct.this.requestForPermission(permission);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(ShowProduct.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
    }

    private void launch() {
        Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                final int numOfRequest = grantResults.length;
                final boolean isGranted = numOfRequest == 1
                        && PackageManager.PERMISSION_GRANTED == grantResults[numOfRequest - 1];
                if (isGranted) {
                    launch();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}


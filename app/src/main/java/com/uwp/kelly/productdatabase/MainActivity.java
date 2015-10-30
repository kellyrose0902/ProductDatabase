package com.uwp.kelly.productdatabase;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter cursorAdapter;
    private EditText search;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    public static final int REQUEST_CODE = 100;
    private String searchString = "";
    private String sortString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set up product list
        ListView list = (ListView)findViewById(android.R.id.list);
        CustomViewBinder binder = new CustomViewBinder();
        String from[] = {ProductOpenHelper.PRODUCT_NAME,ProductOpenHelper.PRODUCT_QUANTITY,ProductOpenHelper.PRODUCT_PRICE};
        int to [] = {R.id.product,R.id.quantity,R.id.price};
        cursorAdapter = new SimpleCursorAdapter(this,R.layout.product_list_item,null,from,to,0);
        cursorAdapter.setViewBinder(binder);
        list.setAdapter(cursorAdapter);
        getLoaderManager().initLoader(0, null, this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ShowProduct.class);
                Uri uri = Uri.parse(ProductProvider.CONTENT_URI + "/" + id);
                intent.putExtra(ProductProvider.CONTENT_ITEM_URI, uri);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        //Set up spinner
        spinner = (Spinner)findViewById(R.id.spinner);
        List<String> sort_list = new ArrayList<>();
        sort_list.add("Sort by");
        sort_list.add("Price");
        sort_list.add("Quantity");
        sort_list.add("Recently Added");

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sort_list){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = null;

                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {
                    // Pass convertView as null to prevent reuse of special case views
                    v = super.getDropDownView(position, null, parent);
                }

                // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        sortString = ProductOpenHelper.PRODUCT_PRICE + " DESC";
                        restartLoader();
                        Toast.makeText(getBaseContext(),"price",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:

                        sortString = ProductOpenHelper.PRODUCT_QUANTITY + " DESC";
                        restartLoader();
                        Toast.makeText(getBaseContext(),"quantity",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        sortString = ProductOpenHelper.PRODUCT_ADDED + " DESC";
                        restartLoader();
                        Toast.makeText(getBaseContext(),"latest",Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        search = (EditText)findViewById(R.id.SearchBar);



    }
    private void deleteAllProduct(){
        getContentResolver().delete(ProductProvider.CONTENT_URI, null, null);
    }
    private void insertSampleProduct() {
        insertProduct("Green Shoes",17, (float) 13.49);
        insertProduct("Green Shoes", 5, (float) 13.49);
        insertProduct("Red Hat", 10, (float) 6.49);
        insertProduct("Blue Shirt", 23, (float) 10.49);
        insertProduct("Green Shoes", 5, (float) 13.49);


    }

    public void insertProduct(String productName,int productQuantity,float productPrice) {
        ContentValues values = new ContentValues();
        values.put(ProductOpenHelper.PRODUCT_NAME, productName);
        values.put(ProductOpenHelper.PRODUCT_QUANTITY, productQuantity);
        values.put(ProductOpenHelper.PRODUCT_PRICE, productPrice);
        Uri uri = getContentResolver().insert(ProductProvider.CONTENT_URI, values);
        Log.d("MainActivity", "Insert note" + uri.getLastPathSegment());
    }
    public void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteAllProduct();
            restartLoader();
            return true;
        }
        if (id == R.id.action_sample){
            insertSampleProduct();
            restartLoader();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //String selection = ProductOpenHelper.PRODUCT_NAME + " COLLATE NOCASE "+" = 'green shoes'" ;

        return new CursorLoader(this,ProductProvider.CONTENT_URI,null,searchString,null,sortString);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void FindProduct(View v){



        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(search.getWindowToken(), 0);
        find();
        //Toast.makeText(this,searchText,Toast.LENGTH_SHORT).show();;

    }

    private void find() {
        String searchText = search.getText().toString();
        if(searchText.length()>0){
            searchString = ProductOpenHelper.PRODUCT_NAME + " COLLATE NOCASE "+" = '"+searchText+"'";
            restartLoader();
        }
        else{
            searchString="";
            restartLoader();
        }
    }


    public void AddNewProduct(View view) {
        Intent intent = new Intent(this,ShowProduct.class);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }
}

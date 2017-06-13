package com.example.vahan.simplecursonadapter;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener{

    SimpleCursorAdapter scAdapter;
    SearchView searchView;
    DB db;
    int cnt = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchview);
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    return false;
                }
            });
        }
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);


        db = new DB(this);
        db.open();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Cursor cursor = null;
        if (searchView != null) {
            cursor = db.getSearchedData(searchView.getQuery().toString());
        }
        startManagingCursor(cursor);

        final String[] from = new String[] { DB.COLUMN_TXT };
        int[] to = new int[] { R.id.tvText};

        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        ListView lvSearch = (ListView) findViewById(R.id.lvsearch);
        lvSearch.setAdapter(scAdapter);
        registerForContextMenu(lvSearch);


        return false;
    }

    @Override
    public void onBackPressed() {
        SearchActivity.this.finish();
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", cnt);
        Log.d(" xc", "onSaveInstanceState");
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cnt = savedInstanceState.getInt("count");
        Log.d(" xc", "onRestoreInstanceState");
    }
}

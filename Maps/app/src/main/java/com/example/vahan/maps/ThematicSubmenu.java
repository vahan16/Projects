package com.example.vahan.maps;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ThematicSubmenu extends AppCompatActivity implements View.OnClickListener {

    Button b;
    ListView lvMain,lvMain1;
    String[] names,distances;
    DB db;
    boolean atLeastOneChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thematic_submenu);

        b=(Button)findViewById(R.id.button4);
        b.setOnClickListener(this);

        db = new DB(this);
        db.open();

        lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.names,
                android.R.layout.simple_list_item_multiple_choice);
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        names = getResources().getStringArray(R.array.names);

        lvMain1 = (ListView) findViewById(R.id.lvMain1);
        lvMain1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this, R.array.distances,
                android.R.layout.simple_list_item_single_choice);
        lvMain1.setAdapter(adapter1);
        distances = getResources().getStringArray(R.array.distances);
    }

    @Override
    public void onClick(View v) {
        SparseBooleanArray sbArray = lvMain.getCheckedItemPositions();
        Intent i1 = new Intent(this, MapsActivity.class);
        String count = "";


        for (int i = 0; i < sbArray.size(); i++) {
            int key = sbArray.keyAt(i);

            if (sbArray.get(key) && lvMain1.isItemChecked(lvMain1.getCheckedItemPosition())) {
                count += lvMain.getItemAtPosition(sbArray.keyAt(i)).toString() + " ";
                i1.putExtra("checked", count);
                i1.putExtra("radio", distances[lvMain1.getCheckedItemPosition()]);
                startActivity(i1);
                atLeastOneChecked = true;
            }
        }
            if (!atLeastOneChecked) {
                Toast.makeText(getApplicationContext(), "Please choose at least 1 topping!", Toast.LENGTH_LONG).show();
            }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Start.class);
        startActivity(intent);
        finish();
    }
}

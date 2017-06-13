package com.example.vahan.simplecursonadapter;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Date;
import java.util.concurrent.locks.Lock;

public class AddNote extends BaseActivity implements View.OnClickListener {

    Button next;
    EditText e, e1;
    DB db;
    ToggleButton tg;
    int image;
    int cnt = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        next = (Button) findViewById(R.id.button);
        next.setOnClickListener(this);

        tg = (ToggleButton) findViewById(R.id.toggleButton);
        tg.setOnClickListener(this);

        e = (EditText) findViewById(R.id.editText);
        e1 = (EditText) findViewById(R.id.editText2);

        db= new DB(this);
        db.open();


    }

    @Override
    public void onClick(View v) {



        switch (v.getId()) {
            case R.id.button:
                if (e.getText().toString().equals("") && e1.getText().toString().equals("")) {
                    Snackbar.make(v, R.string.Nothing_to_save, Snackbar.LENGTH_LONG).show();
                } else {
                    android.text.format.DateFormat df = new android.text.format.DateFormat();
                    String date = getString(R.string.Created) + String.valueOf(df.format("dd.MM.yyyy,hh:mm /", new Date()));
                    Intent i1 = new Intent(this,EditNote.class);
                    i1.putExtra("created",date);


                    db.addRec(e.getText().toString() + "\n" + e1.getText().toString(),
                            date, image);


//                    Intent i = new Intent(this, MainActivity.class);
//                    startActivity(i);
                    AddNote.this.finish();
                    overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

                    Toast.makeText(getApplicationContext(), R.string.Note_added, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.toggleButton:
                if((tg.isChecked())) {
                    image = R.drawable.lock;
                    Snackbar.make(v, R.string.Password_set, Snackbar.LENGTH_LONG).show();

                }
                else {
                    image = R.drawable.blank;
                }



                break;
        }
    }

    @Override
    public void onBackPressed() {
        AddNote.this.finish();
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }

//    @Override
//    public void updateTexts() {
//
//    }

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


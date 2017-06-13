package com.example.vahan.simplecursonadapter;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Date;

public class EditNote extends BaseActivity implements View.OnClickListener {

    EditText e,e1;
    Button next;
    DB db;
    int image,image1;
    ToggleButton tg;
    String id,created;
    Cursor cursor;
    int cnt = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        next = (Button)findViewById(R.id.button4);
        next.setOnClickListener(this);

        tg = (ToggleButton) findViewById(R.id.toggleButton);
        tg.setOnClickListener(this);

        e=(EditText)findViewById(R.id.editText3);
        e1=(EditText)findViewById(R.id.editText4);

        Intent intent = getIntent();
        String text = intent.getStringExtra("key3");
        String text1 = intent.getStringExtra("key4");
        e.setText(text);
        e1.setText(text1);
        id = intent.getStringExtra("key0");
        created = intent.getStringExtra("key5");
        image1 = intent.getIntExtra("key6",0);

        db= new DB(this);
        db.open();

        if (image1 == R.drawable.lock) {
            tg.setChecked(true);
            image = R.drawable.lock;
        }
        else {
            tg.setChecked(false);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button4:

                if (e.getText().toString().equals("") && e1.getText().toString().equals("")) {
                    Snackbar.make(v, R.string.Nothing_to_edit, Snackbar.LENGTH_LONG).show();
                } else {

                    String[] arr =created.split("/");
                    String firstWord = arr[0];
                    String date = getString(R.string.Edited) + String.valueOf(DateFormat.format("dd.MM.yyyy,hh:mm", new Date()));

                    db.editRec(e.getText().toString() + "\n"+ e1.getText().toString(),firstWord + date,id,image);
                    EditNote.this.finish();
                    overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

                    Toast.makeText(getApplicationContext(), R.string.Note_edited, Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.toggleButton:
                if((tg.isChecked())) {
                    image = R.drawable.lock;
                    Snackbar.make(v,R.string.Password_set, Snackbar.LENGTH_LONG).show();
                }
                else {
                    image = R.drawable.blank;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
        finish();


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

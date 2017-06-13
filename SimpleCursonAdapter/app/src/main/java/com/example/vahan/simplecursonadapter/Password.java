package com.example.vahan.simplecursonadapter;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class Password extends BaseActivity implements View.OnClickListener {

    EditText etPas;
    Button reg;
    DB db;
    TextView pass;
    int cnt = 0;
    ToggleButton tg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        etPas = (EditText) findViewById(R.id.editPassword);
        pass = (TextView) findViewById(R.id.textView2);
        reg = (Button) findViewById(R.id.home);
        reg.setOnClickListener(this);

        tg = (ToggleButton)findViewById(R.id.toggleButton2);
        tg.setOnClickListener(this);

        db = new DB(this);
        db.open();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:

                Log.d("pas",etPas.getText().toString());
                db.editPas(etPas.getText().toString(),"1");
                onBackPressed();
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

                break;
            case R.id.toggleButton2:
                if((tg.isChecked())) {
                    etPas.setTransformationMethod(null);
                    etPas.setSelection(etPas.getText().length());
                }
                else {
                    etPas.setTransformationMethod(new PasswordTransformationMethod());
                    etPas.setSelection(etPas.getText().length());
                }
        }
    }

    @Override
    public void onBackPressed() {
        Password.this.finish();
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

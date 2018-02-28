package com.example.vahan.simplecursonadapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.startapp.android.publish.adsCommon.StartAppAd;

import java.util.Locale;


public class Settings1 extends PreferenceActivity implements View.OnClickListener {

    String devID = "106816910";
    String appID = "206898084";
    TextView settings,setPassword,theme,language1;
    Button add,langHy,langRs,langEn;
    RelativeLayout relativeLayout;
    int cnt=0;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    String lang="en";
    private AppCompatDelegate mDelegate;
    Intent intent;
    EditText pass,old;
    AlertDialog.Builder adb;
    LinearLayout view;
    final int DIALOG = 1;
    ToggleButton toggleButton;
    final int DIALOG2 = 2;
    DB db;
    AlertDialog dialog;
    String str ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.applyTheme(this);
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings1);
        addPreferencesFromResource(R.xml.preferences);
        Preferences.sync(getPreferenceManager());
        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Preferences.sync(getPreferenceManager(), key);
                if (key.equals(getString(R.string.pref_theme))) {
                    finish();
                    final Intent intent = IntentCompat.makeMainActivity(new ComponentName(
                            Settings1.this, NoteActivity.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    Settings1.this.finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

                }
            }
        };
        StartAppAd.init(this, devID, appID);
        StartAppAd.disableSplash();

        langHy=(Button)findViewById(R.id.button14);
        langRs=(Button)findViewById(R.id.button15);
        langEn=(Button)findViewById(R.id.button16);
        add=(Button)findViewById(R.id.button3);

        add.setOnClickListener(this);
        langHy.setOnClickListener(this);
        langRs.setOnClickListener(this);
        langEn.setOnClickListener(this);

        settings=(TextView) findViewById(R.id.textView2);
        setPassword=(TextView) findViewById(R.id.textView4);
        theme=(TextView) findViewById(R.id.textView5);
        language1=(TextView) findViewById(R.id.textView7);

        db = new DB(this);
        db.open();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }



    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }



    @Override
    public void onBackPressed() {
        Settings1.this.finish();
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }


    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale locale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();

    }

    public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.apply();
    }

    public void loadLocale()
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }


    public void updateTexts() {
        settings.setText(R.string.settings);
        setPassword.setText(R.string.set_password);
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

    @Override
    public void onClick(View v) {


        intent = new Intent(this, NoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        switch (v.getId()){
            case R.id.button3:
                Cursor c = db.getPassData();
                startManagingCursor(c);
                str = "";
                if (c.moveToFirst()) {
                    str = c.getString(c.getColumnIndex("password"));
                }
                if(str.equals("")) {
                   //empty
                    showDialog(DIALOG);
                }else{
                    //not empty
                    showDialog(DIALOG2);
                }

                break;

            case R.id.button14:
                loadLocale();
                lang="hy";
                changeLang(lang);
                finish();
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                startActivity(intent);

                break;

            case R.id.button15:
                loadLocale();
                lang="ru";
                changeLang(lang);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                break;

            case R.id.button16:
                loadLocale();
                lang="en";
                changeLang(lang);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                break;
        }

    }



    @Override
    protected Dialog onCreateDialog(int id) {
        adb = new AlertDialog.Builder(this);
        view = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.activity_password, null);
        if(id==DIALOG)
        adb.setTitle(R.string.create_password);
        if(id==DIALOG2)
            adb.setTitle(R.string.edit_password);

        adb.setView(view);
        dialog = adb.create();

        return dialog;
    }

    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        relativeLayout=(RelativeLayout)dialog.findViewById(R.id.dgdg);
        toggleButton=(ToggleButton)dialog.findViewById(R.id.toggleButton0);
        pass = (EditText) dialog.findViewById(
                R.id.editPassword);
        old=(EditText)dialog.findViewById(R.id.editText5);
        if (id == DIALOG) {
            relativeLayout.setVisibility(View.GONE);
            toggleButton.setVisibility(View.GONE);
            old.setVisibility(View.GONE);
            final ToggleButton tg = (ToggleButton)dialog.getWindow().findViewById(R.id.toggleButton2);
            tg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((tg.isChecked())) {
                        pass.setTransformationMethod(null);
                        pass.setSelection(pass.getText().length());
                    }
                    else {
                        pass.setTransformationMethod(new PasswordTransformationMethod());
                        pass.setSelection(pass.getText().length());
                    }
                }
            });
            Button but = (Button) dialog.getWindow().findViewById(R.id.button6);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      if(pass.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_LONG).show();
                    }
                    else {
                          db.editPas(pass.getText().toString(), "1");
                          onBackPressed();
                          overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                      }
                }
            });
        }
        if(id == DIALOG2){
            relativeLayout.setVisibility(View.VISIBLE);
            toggleButton.setVisibility(View.VISIBLE);
            old.setVisibility(View.VISIBLE);
            final ToggleButton tg1 = (ToggleButton)dialog.getWindow().findViewById(R.id.toggleButton0);
            tg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((tg1.isChecked())) {
                        old.setTransformationMethod(null);
                        old.setSelection(old.getText().length());
                    }
                    else {
                        old.setTransformationMethod(new PasswordTransformationMethod());
                        old.setSelection(old.getText().length());
                    }
                }
            });
            Button but = (Button) dialog.getWindow().findViewById(R.id.button6);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
            if(old.getText().toString().equals(str)&& !(pass.getText().toString().equals(""))){
                db.editPas(pass.getText().toString(),"1");
                onBackPressed();
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            }
            else if(pass.getText().toString().equals("")||(old.getText().toString().equals(str)&& pass.getText().toString().equals(""))) {
                Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_LONG).show();

            }

            else if(!(old.getText().toString().equals(str))){
                Toast.makeText(getApplicationContext(), "Illegal password", Toast.LENGTH_LONG).show();
            }
                }
            });
        }
    }
}

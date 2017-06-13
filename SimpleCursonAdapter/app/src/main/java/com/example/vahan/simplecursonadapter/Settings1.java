package com.example.vahan.simplecursonadapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;


public class Settings1 extends PreferenceActivity implements View.OnClickListener {


    TextView settings,setPassword,theme,language1;
    Button add,langHy,langRs,langEn;
    int cnt=0;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    private AppCompatDelegate mDelegate;
    Intent intent;
    String lang="en";

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
                            Settings1.this, MainActivity.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    Settings1.this.finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.from_middle, R.anim.to_middle);

                }
            }
        };

        add=(Button)findViewById(R.id.button7);
        langHy=(Button)findViewById(R.id.button14);
        langRs=(Button)findViewById(R.id.button15);
        langEn=(Button)findViewById(R.id.button16);


        add.setOnClickListener(this);
        langHy.setOnClickListener(this);
        langRs.setOnClickListener(this);
        langEn.setOnClickListener(this);


        settings=(TextView) findViewById(R.id.textView2);
        setPassword=(TextView) findViewById(R.id.textView4);
        theme=(TextView) findViewById(R.id.textView5);
        language1=(TextView) findViewById(R.id.textView7);




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


        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        switch (v.getId()){

            case R.id.button7:
                Intent i = new Intent(this,Password.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
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
}

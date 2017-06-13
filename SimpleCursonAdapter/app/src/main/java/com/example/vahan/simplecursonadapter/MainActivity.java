package com.example.vahan.simplecursonadapter;


// •••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    TextView notes;
    int cnt = 0;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_DELETE_ALL = 2;
    ListView lvData;
    DB db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;
    final int DIALOG = 1;
    final int DIALOG2 = 2;
    final int DIALOG3 = 3;
    LinearLayout view;
    Intent intent1;
    EditText pass;
    String str;
    AdapterContextMenuInfo acmi;
    AlertDialog.Builder adb;
    AlertDialog dialog;
    boolean isFABOpen = false;
    FloatingActionButton fab,fab1,fab2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        db = new DB(this);
        db.open();


        cursor = db.getAllData();
        startManagingCursor(cursor);



        final String[] from = new String[]{DB.COLUMN_IMG, DB.COLUMN_TXT, DB.COLUMN_DATE};
        int[] to = new int[]{R.id.ivImg, R.id.tvText, R.id.textView6};

        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);
        registerForContextMenu(lvData);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2 = (FloatingActionButton) findViewById(R.id.fab3);


        notes = (TextView) findViewById(R.id.textView);


        loadLocale();

        fab1.setVisibility(View.GONE);
        fab2.setVisibility(View.GONE);
        fab1.hide();
        fab2.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,AddNote.class);
                startActivity(i);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                cursor.requery();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Drawing.class);
                startActivity(i);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                cursor.requery();
            }
        });


        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    intent1 = new Intent(MainActivity.this, EditNote.class);
                    intent1.putExtra("key0", String.valueOf(id));
                    TextView textView = (TextView) view.findViewById(R.id.tvText);
                    String[] parts = textView.getText().toString().split("\n", 2);
                    String part1 = parts[0];
                    String part2 = parts[1];
                    intent1.putExtra("key3", part1);
                    intent1.putExtra("key4", part2);
                    TextView textView1 = (TextView) view.findViewById(R.id.textView6);
                    intent1.putExtra("key5", textView1.getText().toString());

                    if (cursor.getInt(cursor.getColumnIndex("img")) == R.drawable.lock) {
                        intent1.putExtra("key6",cursor.getInt(cursor.getColumnIndex("img")));

                        showDialog(DIALOG);
                    } else {
                        startActivity(intent1);
                        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                    }

            }
        });

        lvData.setOnTouchListener(new View.OnTouchListener() {
            float height;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float height = event.getY();
                if (action == MotionEvent.ACTION_DOWN) {
                    this.height = height;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (this.height > height) {
                           fab.hide();
                        if( isFABOpen)
                        closeFABMenu();
                        }
                    else if (this.height < height) {
                        fab.show();
                        if( isFABOpen)
                        showFABMenu();
                    }
                }
                return false;
            }
        });




    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.write_your_password);
        view = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.dialog, null);
        adb.setView(view);
        dialog = adb.create();
        return dialog;

    }

    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG) {
            pass = (EditText) dialog.getWindow().findViewById(
                    R.id.pass);

            final ToggleButton tg = (ToggleButton)dialog.getWindow().findViewById(R.id.toggleButton3);
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



                    Button but = (Button) dialog.getWindow().findViewById(R.id.but);
            but.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Cursor c = db.getPassData();
                    startManagingCursor(c);
                    str = "";
                    if (c.moveToFirst()) {
                        str = c.getString(c.getColumnIndex("password"));
                    }

                    Log.d("log", str);

                    if (pass.getText().toString().equals(str)) {
                        startActivity(intent1);
                        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                    } else {
                        Toast.makeText(getApplicationContext(), "Illegal password", Toast.LENGTH_LONG).show();
                    }
                    pass.setText("");
                }
            });
        }
            if (id == DIALOG2) {
                pass  = (EditText) dialog.getWindow().findViewById(
                        R.id.pass);



                Button but1 = (Button) dialog.getWindow().findViewById(R.id.but);
                but1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Cursor c = db.getPassData();
                        startManagingCursor(c);
                        str="";
                        if (c.moveToFirst()) {
                            str = c.getString(c.getColumnIndex("password"));
                        }

                        Log.d("log" ,str);

                        if (pass.getText().toString().equals(str)) {
                                db.delRec(acmi.id);
                            cursor.requery();

                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Illegal password",Toast.LENGTH_LONG).show();
                        }
                        pass.setText("");
                    }
                });

        }
        if (id == DIALOG3) {
            pass  = (EditText) dialog.getWindow().findViewById(
                    R.id.pass);

            Button but1 = (Button) dialog.getWindow().findViewById(R.id.but);
            but1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Cursor c = db.getPassData();
                    startManagingCursor(c);
                    str="";
                    if (c.moveToFirst()) {
                        str = c.getString(c.getColumnIndex("password"));
                    }

                    Log.d("log" ,str);

                    if (pass.getText().toString().equals(str)) {
                        db.delAll();
                        cursor.requery();

                        dialog.dismiss();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Illegal password",Toast.LENGTH_LONG).show();
                    }
                    pass.setText("");
                }
            });

        }
    }


    public void onButtonClick(View view) {



        switch(view.getId()) {
            case R.id.search:

                Intent i1 = new Intent(this,SearchActivity.class);
                startActivity(i1);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                break;
            case R.id.button5:
                Intent i2 = new Intent(this,Settings1.class);
                startActivity(i2);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                break;
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(0,CM_DELETE_ALL,0, R.string.deleteAll);
    }

    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == CM_DELETE_ID) {
            acmi = (AdapterContextMenuInfo) item.getMenuInfo();
            if(cursor.getInt(cursor.getColumnIndex("img")) == R.drawable.lock) {
                showDialog(DIALOG2);
            }
            else{
                db.delRec(acmi.id);
            }
            cursor.requery();
            return true;
        }
        if (item.getItemId() == CM_DELETE_ALL) {

                showDialog(DIALOG3);

            cursor.requery();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private void showFABMenu() {
        isFABOpen=true;
        fab1.setVisibility(View.VISIBLE);
        fab2.setVisibility(View.VISIBLE);
        fab1.animate().rotationBy(360);
        fab2.animate().rotationBy(360);
        fab1.show();
        fab2.show();
        fab.animate().rotationBy(45);
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu() {
        isFABOpen=false;
        fab1.hide();
        fab2.hide();
        fab.animate().rotationBy(-45);


        fab1.animate().rotationBy(-360);
        fab2.animate().rotationBy(-360);
        fab1.animate().translationY(0).setListener(new Animator.AnimatorListener()  {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    fab1.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        fab2.animate().translationY(0).setListener(new Animator.AnimatorListener()  {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    fab2.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.sure)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

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

    public void updateTexts(){
        notes.setText(R.string.Notes);
    }
}
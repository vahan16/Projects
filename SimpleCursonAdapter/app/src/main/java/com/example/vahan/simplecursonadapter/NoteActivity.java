package com.example.vahan.simplecursonadapter;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
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

import com.startapp.android.publish.adsCommon.StartAppAd;

import java.util.Locale;

public class NoteActivity extends BaseActivity {

    private TextView notes, textview;
    private int cnt = 0;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_DELETE_ALL = 2;
    private static final int CM_SHARE = 3;
    private ListView lvData;
    private DB db;
    private SimpleCursorAdapter scAdapter;
    private Cursor cursor;
    private final int DIALOG = 1;
    private final int DIALOG2 = 2;
    private final int DIALOG3 = 3;
    LinearLayout view;
    private Intent intent1;
    private EditText pass;
    private String str;
    private AdapterContextMenuInfo acmi;
    private boolean isFABOpen = false;
    private Toolbar toolbar;
    private FloatingActionButton fab,fab1,fab2;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ask for permission for external storage
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //find toolbar and set text
        toolbar = (Toolbar)findViewById(R.id.toolbar5);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //set selected item
        NavigationView navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.notes1);

        //access db
        db = new DB(this);
        db.open();
        cursor = db.getAllData();
        startManagingCursor(cursor);

        //simple cursor adapter for listview items
        final String[] from = new String[]{DB.COLUMN_IMG,DB.COLUMN_IMG1,DB.COLUMN_TXT, DB.COLUMN_DATE};
        int[] to = new int[]{R.id.ivImg,R.id.imageView2, R.id.tvText, R.id.textView6};

        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);
        db.getData();
        registerForContextMenu(lvData);

        //startapp ad
        String devID = "106816910";
        String appID = "206898084";
        StartAppAd.init(this, devID, appID);
            StartAppAd.disableSplash();

        lvData.setEmptyView(findViewById(R.id.relaticel));

        notes = (TextView) findViewById(R.id.textView8);

        //load locale for language
        loadLocale();

        //floating action buttons for scroll to hifde and show
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2 = (FloatingActionButton) findViewById(R.id.fab3);

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
                Intent i = new Intent(NoteActivity.this,AddNote.class);
                startActivity(i);
                cursor.requery();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NoteActivity.this,Drawing.class);
                startActivity(i);
                cursor.requery();
            }
        });

        lvData.setDivider(null);
        lvData.setDividerHeight(0);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    intent1 = new Intent(NoteActivity.this, EditNote.class);
                    intent1.putExtra("key0", String.valueOf(id));
                    textview= (TextView) view.findViewById(R.id.tvText);
                    String[] parts = textview.getText().toString().split("\n", 2);
                    String part1 = parts[0];
                    String part2 = parts[1];
                    intent1.putExtra("key3", part1);
                    intent1.putExtra("key4", part2);
                    TextView textView1 = (TextView) view.findViewById(R.id.textView6);
                    intent1.putExtra("key5", textView1.getText().toString());

                    if (cursor.getInt(cursor.getColumnIndex("img")) == R.drawable.ic_lock_outline_black_24d) {
                        intent1.putExtra("key6", cursor.getInt(cursor.getColumnIndex("img")));
                        showDialog(DIALOG);
                    } else {
                        startActivity(intent1);
                    }
                if (cursor.getInt(cursor.getColumnIndex("img1")) == R.drawable.ic_alarm_black_24d) {
                    intent1.putExtra("key7", cursor.getInt(cursor.getColumnIndex("img1")));
                }
            }
        });

        lvData.setOnTouchListener(new View.OnTouchListener() {
            float height;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(scAdapter.getCount()>8) {
                    int action = event.getAction();
                    float height = event.getY();
                    if (action == MotionEvent.ACTION_DOWN) {
                        this.height = height;
                    } else if (action == MotionEvent.ACTION_UP) {
                        if (this.height > height) {
                            fab.hide();
                            if (isFABOpen)
                                closeFABMenu();
                        } else if (this.height < height) {
                            fab.show();
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent i1 = new Intent(this, SearchActivity.class);
                startActivity(i1);
                break;
            case R.id.button5:
                Intent i2 = new Intent(this, Settings1.class);
                startActivity(i2);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.write_your_password);
        view = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.dialog, null);
        adb.setView(view);
        return adb.create();
    }

    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog) {
        super.onPrepareDialog(id, dialog);

        pass  = (EditText) dialog.findViewById(R.id.pass);
        final ToggleButton tg = (ToggleButton)dialog.findViewById(R.id.toggleButton3);
        Button but1 = (Button) dialog.findViewById(R.id.but);

        if (id == DIALOG) {
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

            but1.setOnClickListener(new View.OnClickListener() {

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
                    } else {
                        Toast.makeText(getApplicationContext(), "Illegal password", Toast.LENGTH_LONG).show();
                    }
                    pass.setText("");
                }
            });
        }
            if (id == DIALOG2) {
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
                            fab.show();
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
                        fab.show();

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

    public void myClickHandler(View v) {
        lvData.showContextMenu();
    }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(0,CM_DELETE_ALL,0, R.string.deleteAll);
        menu.add(0,CM_SHARE,0, getString(R.string.share_note));
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            acmi = (AdapterContextMenuInfo) item.getMenuInfo();
            if(cursor.getInt(cursor.getColumnIndex("img")) == R.drawable.ic_lock_outline_black_24d) {
                showDialog(DIALOG2);
            }
            else{
                db.delRec(acmi.id);
                fab.show();
            }
            cursor.requery();
            return true;
        }
        if (item.getItemId() == CM_DELETE_ALL) {
                showDialog(DIALOG3);
            cursor.requery();
            return true;
        }
        if (item.getItemId() == CM_SHARE) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "I send you one of my notes\n'"+cursor.getString(cursor.getColumnIndex("name"))+"'");
            startActivity(Intent.createChooser(i, "Choose one"));
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
                        finish();
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
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
        notes.setText(R.string.no_notes);
        toolbar.setTitle(R.string.app_name);

    }

    private void askForPermission(String permission) {
        if (ContextCompat.checkSelfPermission(NoteActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NoteActivity.this, permission)) {
                ActivityCompat.requestPermissions(NoteActivity.this, new String[]{permission}, 1);
            } else {
                ActivityCompat.requestPermissions(NoteActivity.this, new String[]{permission}, 1);
            }
        }
    }


}
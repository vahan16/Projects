package com.example.vahan.simplecursonadapter;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.startapp.android.publish.adsCommon.StartAppAd;

import java.io.File;
import java.io.IOException;

public class DrawingsActivity extends BaseActivity {

    String devID = "106816910";
    String appID = "206898084";
    private static final int CM_DELETE_ID = 1;
    private static final int CM_SHARE_ID = 2;
    private String[] FilePathStrings;
    private File[] listFile;
    GridView grid;
    GridViewAdapter adapter;
    File file;
    FloatingActionButton fab,fab1,fab2;
    AdapterView.AdapterContextMenuInfo acmi;
    boolean isFABOpen = false;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        StartAppAd.init(this, devID, appID);
        StartAppAd.disableSplash();

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Drawings");
        }

        if (file.isDirectory())
        {
            listFile = file.listFiles();
            FilePathStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++)
            {
                FilePathStrings[i] = listFile[i].getAbsolutePath();
            }
            if (listFile == null) {
                Intent i = new Intent(this,Drawing.class);
                startActivity(i);
            }
            if (listFile.length == 0) {
                Toast.makeText(this, "No picture found!",
                        Toast.LENGTH_LONG).show();
                Intent i = new Intent(this,Drawing.class);
                startActivity(i);
            }else {
                grid = (GridView) findViewById(R.id.gridview);
                adapter = new GridViewAdapter(this, FilePathStrings);
                grid.setAdapter(adapter);
                registerForContextMenu(grid);
                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick (AdapterView<?> parent, View view,
                                             int position, long id)
                    {
                        Intent i = new Intent(DrawingsActivity.this,Drawing.class);
                        i.putExtra("kev",listFile[position].getAbsolutePath());
                        Log.d("gsdgsgd",listFile[position].getAbsolutePath());
                        startActivity(i);
                    }
                });
            }
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar75);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.drawings);
        setSupportActionBar(toolbar);
        NavigationView navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.drawings);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab1 = (FloatingActionButton) findViewById(R.id.fab4);
        fab2 = (FloatingActionButton) findViewById(R.id.fab5);

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
                Intent i = new Intent(DrawingsActivity.this,AddNote.class);
                startActivity(i);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DrawingsActivity.this,Drawing.class);
                startActivity(i);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            }
        });
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

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_drawing);
        menu.add(0, CM_SHARE_ID, 0, R.string.share_drawing);
    }

    public boolean onContextItemSelected(MenuItem item) {
        acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        File file = new File(listFile[(int) acmi.id].getAbsolutePath());
        if (item.getItemId() == CM_DELETE_ID) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);

            return true;
        }
        if (item.getItemId() == CM_SHARE_ID) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            Uri bmpUri = Uri.fromFile(file);
            Log.d("skfgb", listFile[(int) acmi.id].getAbsolutePath());
            share.putExtra(Intent.EXTRA_STREAM, bmpUri);
            share.putExtra(Intent.EXTRA_TEXT, "I recommend you this application\n\nhttps://play.google.com/store/apps/details?id=vahan.simplecursonadapter \n\n");
            startActivity(Intent.createChooser(share, "Share Image"));
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.draws, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button92:
                Intent i2 = new Intent(this, Settings1.class);
                startActivity(i2);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}

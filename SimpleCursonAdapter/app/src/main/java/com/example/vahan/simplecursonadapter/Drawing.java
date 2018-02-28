package com.example.vahan.simplecursonadapter;

/**
 * Created by vahan on 6/7/17.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Drawing extends BaseActivity implements  View.OnClickListener{

    boolean doubleBackToExitPressedOnce = false;
    private DrawingView drawView;
    private ImageButton currPaint;
    private float smallBrush, mediumBrush, largeBrush;
    private final int RESULT_LOAD_IMAGE = 20;
    private String imagePath = "";
    public Bitmap PaintBitmap;
    int cnt=0;
    String intValue1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_view);

        drawView = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        toolbar.setTitle(R.string.drawing);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        ImageButton drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        ImageButton upload = (ImageButton) findViewById(R.id.upload);
        upload.setOnClickListener(this);

        ImageButton newBtn = (ImageButton) findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        ImageButton saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        ImageButton undo = (ImageButton) findViewById(R.id.undo);
        undo.setOnClickListener(this);

        ImageButton redo = (ImageButton) findViewById(R.id.redo);
        redo.setOnClickListener(this);

        drawView.setBrushSize(smallBrush);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            Intent mIntent = getIntent();
            intValue1 = mIntent.getStringExtra("kev");
            Bitmap myBitmap = BitmapFactory.decodeFile(intValue1);
            BitmapDrawable ob1 = new BitmapDrawable(getResources(), myBitmap);
            drawView.setBackgroundDrawable(ob1);

        }else {
            drawView.setBackgroundColor(Color.WHITE);
        }
    }

    public void paintClicked(View view){
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
            drawView.setBrushSize(drawView.getLastBrushSize());
        }
    }

    private void askForPermission(String permission) {
        if (ContextCompat.checkSelfPermission(Drawing.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Drawing.this, permission)) {
                ActivityCompat.requestPermissions(Drawing.this, new String[]{permission}, 1);
            } else {
                ActivityCompat.requestPermissions(Drawing.this, new String[]{permission}, 1);
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.draw_btn:
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                Button smallBtn = (Button)brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                Button mediumBtn = (Button)brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });

                Button largeBtn = (Button)brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();
//                drawView.setBrushSize(largeBrush);
                break;

            case R.id.new_btn:
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle(R.string.new_drawing);
                newDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();
                drawView.setBackgroundColor(Color.WHITE);
                break;

            case R.id.save_btn:
                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle(R.string.save_drawing);
                saveDialog.setMessage(R.string.save_drawing_to_deviceGallery);
                saveDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        drawView.setDrawingCacheEnabled(true);
                        OutputStream fOutStream;
                        Bitmap drawingBM = drawView.getDrawingCache();
                        try {
                            if (drawingBM != null) {
                                File mediaStorageDir = new File(
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Drawings");
                                mediaStorageDir.mkdirs();
                                if (!mediaStorageDir.exists()) {
                                    if (!mediaStorageDir.mkdirs()) {
                                        Log.d("App", "failed to create directory");
                                    }
                                }
                                File file = new File(mediaStorageDir, UUID.randomUUID().toString() + ".png");
                                fOutStream = new FileOutputStream(file);
                                BufferedOutputStream bos = new BufferedOutputStream(fOutStream);
                                Boolean a = drawingBM.compress(Bitmap.CompressFormat.PNG, 100, bos);
                                bos.flush();
                                bos.close();
                                if(a){
                                   Toast.makeText(getApplicationContext(), R.string.Image_saved_to_gallery, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Drawing.this,DrawingsActivity.class);
                                    startActivity(intent);
                                }

                                MediaScannerConnection.scanFile(getApplicationContext(),
                                        new String[] { file.toString() }, null,
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            public void onScanCompleted(String path, Uri uri) {
                                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                                Log.i("ExternalStorage", "-> uri=" + uri);
                                            }
                                        });
                            }

                        } catch (Exception e) {
                            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                        drawView.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                break;

            case R.id.undo:
                drawView.undo();
                break;
            case R.id.redo:
                drawView.redo();
                break;
            case R.id.upload:
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            drawView.startNew();
            Uri pickedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            }
            if (imagePath != null) {
                PaintBitmap = getResizeBitmap(imagePath);
                BitmapDrawable ob = new BitmapDrawable(getResources(), PaintBitmap);
                drawView.setBackgroundDrawable(ob);
            }
        }
    }

    public static Bitmap getResizeBitmap(String path) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmap = BitmapFactory.decodeFile(path, op);
        int nh = (int) (bmap.getHeight() * (1024.0 / bmap.getWidth()));
        return Bitmap.createScaledBitmap(bmap, 1024, nh, true);
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
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            drawView.startNew();
            finish();
            Intent intent = new Intent(Drawing.this, NoteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.press_Again, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
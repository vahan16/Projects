package com.example.vahan.simplecursonadapter;

/**
 * Created by vahan on 6/7/17.
 */


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Drawing extends BaseActivity implements  View.OnClickListener{

    private DrawingView drawView;
    private ImageButton currPaint,drawBtn,eraseBtn,newBtn, saveBtn,undo,redo;
    private float smallBrush, mediumBrush, largeBrush;
    int cnt=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_view);

        drawView = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        undo = (ImageButton)findViewById(R.id.undo);
        undo.setOnClickListener(this);

        redo = (ImageButton)findViewById(R.id.redo);
        redo.setOnClickListener(this);

        drawView.setBrushSize(smallBrush);
    }

    public void paintClicked(View view){
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
            drawView.setErase(false);
            drawView.setBrushSize(drawView.getLastBrushSize());
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

            case R.id.erase_btn:
                final Dialog brushDialog1 = new Dialog(this);
                brushDialog1.setTitle("Eraser size:");
                brushDialog1.setContentView(R.layout.brush_chooser);
                Button smallBtn1 = (Button)brushDialog1.findViewById(R.id.small_brush);
                smallBtn1.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        brushDialog1.dismiss();
                    }
                });
                Button mediumBtn1 = (Button)brushDialog1.findViewById(R.id.medium_brush);
                mediumBtn1.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        brushDialog1.dismiss();
                    }
                });
                Button largeBtn1 = (Button)brushDialog1.findViewById(R.id.large_brush);
                largeBtn1.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        brushDialog1.dismiss();

                    }
                });
                brushDialog1.show();
                drawView.setErase(false);
                break;

            case R.id.new_btn:
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("New drawing");
                newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();
                break;

            case R.id.save_btn:
                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Save drawing");
                saveDialog.setMessage("Save drawing to device Gallery?");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                                    Snackbar.make(v, R.string.Image_saved_to_gallery, Snackbar.LENGTH_LONG).show();
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
                            Toast.makeText(getApplicationContext(),
                                    "You don't have permission to save file.\nChange app permissions for saving it.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            e.printStackTrace();
                        }


                        drawView.destroyDrawingCache();

                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
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

        }
    }

//    @Override
//    public void updateTexts() {
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

    @Override
    public void onBackPressed() {
        Drawing.this.finish();
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
    }

}
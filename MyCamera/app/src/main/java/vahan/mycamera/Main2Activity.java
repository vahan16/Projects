package vahan.mycamera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    ToggleButton flash,flipButton;
    Animation animation;
    Camera.PictureCallback mPicture;
    Bitmap mImageBitmap;
    Camera mCam;
    MirrorView mCamPreview;
    int mCameraId = 0;
    FrameLayout mPreviewLayout;
    ImageView whiteScreen;
    static final int REQUEST_CAMERA_PERMISSION = 200;
    Button b,res,effects;
    Button effectNone,effectMono,effectAqua,effectPosterize,effectNegative,effectSepia,effectBlackboard; //effects
    ImageView myImage;
    int brightness = 255;
    HorizontalScrollView horizontalScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();




        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main2);

        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.scroll);
        horizontalScrollView.setVisibility(View.GONE);

        mCameraId = findFirstFrontFacingCamera();

        res = (Button)findViewById(R.id.Resolution);
        res.setOnClickListener(this);

        effects=(Button)findViewById(R.id.button6);
        effects.setOnClickListener(this);

        effectNone=(Button)findViewById(R.id.button3);
        effectMono=(Button)findViewById(R.id.button4);
        effectAqua=(Button)findViewById(R.id.button5);
        effectPosterize=(Button)findViewById(R.id.button7);

        b = (Button) findViewById(R.id.capture);
        b.setOnClickListener(this);
        b.setBackgroundResource(R.drawable.shutter);

        flash = (ToggleButton)findViewById(R.id.toggleButton);
        flipButton = (ToggleButton)findViewById(R.id.toggleButton2);

        mPreviewLayout = (FrameLayout) findViewById(R.id.camPreview);
        mPreviewLayout.removeAllViews();

        whiteScreen = (ImageView)findViewById(R.id.white);
        whiteScreen.setVisibility(View.GONE);

        try {
            startCameraInLayout(mPreviewLayout, mCameraId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mPicture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    File mediaStorageDir = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "MyCamera");
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            Log.d("App", "failed to create directory");
                        }
                    }
                    File file = new File(mediaStorageDir, UUID.randomUUID().toString() + ".jpeg");
                    mImageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(mImageBitmap , 0, 0, mImageBitmap .getWidth(), mImageBitmap .getHeight(), matrix, true);
                    myImage = (ImageView) findViewById(R.id.photo);

                    OutputStream fOutStream = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fOutStream);
                    assert rotatedBitmap != null;
                    if(flipButton.isChecked()) {
                        myImage.setImageBitmap(flip(rotatedBitmap));
                        flip(rotatedBitmap).compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    }
                    else{
                        myImage.setImageBitmap(rotatedBitmap);
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    }

                        bos.flush();
                        bos.close();

                    MediaScannerConnection.scanFile(getApplicationContext(),
                            new String[]{file.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, final Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                    myImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

    }

    private int findFirstFrontFacingCamera() {
        int foundId = -1;
        int numCams = Camera.getNumberOfCameras();
        for (int camId = 0; camId < numCams; camId++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(camId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                foundId = camId;
                break;
            }
        }
        return foundId;
    }

    private void startCameraInLayout(FrameLayout layout, int cameraId) {
        mCam = Camera.open(cameraId);
        if (mCam != null) {
            mCamPreview = new MirrorView(this, mCam);

//            if(res.getText().equals("16:9")){
//                mCamPreview.setLayoutParams(new FrameLayout.LayoutParams(mCamPreview.getLayoutParams().width, mCamPreview.getLayoutParams().width*16/9));
//            }
//            else if(res.getText().equals("4:3")){
//                mCamPreview.setLayoutParams(new FrameLayout.LayoutParams(mCamPreview.getLayoutParams().width, mCamPreview.getLayoutParams().width*4/3));
//            }
//            else if(res.getText().equals("1:1")){
//                mCamPreview.setLayoutParams(new FrameLayout.LayoutParams(mCamPreview.getWidth(), mCamPreview.getWidth()));
//            }

            effectNone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Camera.Parameters parameters = mCam.getParameters();
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                    mCam.setParameters(parameters);
                }
            });



            effectMono.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Camera.Parameters parameters = mCam.getParameters();
                    parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
                    mCam.setParameters(parameters);
                }
            });


            effectAqua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Camera.Parameters parameters = mCam.getParameters();
                    parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                    mCam.setParameters(parameters);
                }
            });

            effectPosterize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Camera.Parameters parameters = mCam.getParameters();
                    parameters.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
                    mCam.setParameters(parameters);
                }
            });


            layout.addView(mCamPreview);

        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture:
                if(flash.isChecked()) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCam.takePicture( new Camera.ShutterCallback() { @Override public void onShutter() { } },
                                    new Camera.PictureCallback() { @Override public void onPictureTaken(byte[] data, Camera camera) { } }, mPicture);
                            whiteScreen.setVisibility(View.GONE);
                            WindowManager.LayoutParams layoutpars = getWindow().getAttributes();
                            layoutpars.screenBrightness = brightness / (float)100;
                            getWindow().setAttributes(layoutpars);
                        }
                    }, 500);

                        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                        b.startAnimation(animation);



                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.screenBrightness = 1.0f;
                    getWindow().setAttributes(params);

                    whiteScreen.setVisibility(View.VISIBLE);

                }
                else{

                    mCam.takePicture( new Camera.ShutterCallback() { @Override public void onShutter() { } },
                            new Camera.PictureCallback() { @Override public void onPictureTaken(byte[] data, Camera camera) { } }, mPicture);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                            b.startAnimation(animation);
                        }
                    }, 0);
                }
                break;


            case R.id.Resolution:

                if(res.getText().equals("16:9")) {
                    res.setText("4:3");
                }
                else if(res.getText().equals("4:3")) {
                    res.setText("1:1");
                }
                else if(res.getText().equals("1:1")) {
                    res.setText("16:9");
                }
                break;

            case R.id.button6:
                if(horizontalScrollView.getVisibility() == View.VISIBLE) {
                    horizontalScrollView.setVisibility(View.GONE);
                }
                else{
                    horizontalScrollView.setVisibility(View.VISIBLE);
                }
                break;
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(getApplicationContext(), "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public static Bitmap flip(Bitmap src) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();

        matrix.preScale(-1.0f, 1.0f);

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

}

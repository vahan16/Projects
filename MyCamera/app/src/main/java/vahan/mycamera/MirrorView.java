package vahan.mycamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by vahan on 26/11/17.
 */
public class MirrorView extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    int cameraId = 0;

    public MirrorView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception error) {
//            Log.d(DEBUG_TAG,
//                    "Error starting mPreviewLayout: " + error.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w,
                               int h) {
        if (mHolder.getSurface() == null) {
            return;
        }
        setCameraDisplayOrientation(this.cameraId, this.mCamera);
        // can't make changes while mPreviewLayout is active
        try {

            mCamera.stopPreview();
        } catch (Exception e) {

        }

        try {

            // start up the mPreviewLayout
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception error) {
//            Log.d(DEBUG_TAG,
//                    "Error starting mPreviewLayout: " + error.getMessage());
        }
    }

    @SuppressLint("NewApi")
    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

            android.hardware.Camera.CameraInfo info =
                    new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
        }
        try {
            camera.setDisplayOrientation(result);
        } catch (Exception e) {
            // may fail on old OS versions. ignore it.
            e.printStackTrace();
        }
    }
}
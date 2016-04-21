package com.example.fmsuvm.surfacecamera;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.hardware.Camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH-mm-ss-SSS");
    final String ROOT_PATH =
            Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator;

    private Camera mCamera;
    private View mView;

    public CameraView(Context context, Camera mCamera) {
        super(context);

        this.mCamera = mCamera;
        mCamera.setDisplayOrientation(90);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e("cameraPreview", "The failure of the camera settings");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size optionalSize = getOptimalPreviewSize(sizes, width, height);
        params.setPreviewSize(optionalSize.width, optionalSize.height);
        mCamera.setParameters(params);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(null, "take a picture");

        final String path = ROOT_PATH + dateFormat.format(new Date());

        FileOutputStream fos = null;

        try{
            fos = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            Log.d("cameraview", e.getMessage());
        }

        if(fos != null){
            try{
                fos.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                fos.close();
                fos = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mCamera.startPreview();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(mCamera != null){
                mCamera.takePicture(null, null, this);//classで実装したので, インスタンスでthis
            }
        }
        return true;
    }
}

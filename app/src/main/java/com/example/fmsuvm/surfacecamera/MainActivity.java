package com.example.fmsuvm.surfacecamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }


    @Override
    protected void onResume(){
        super.onResume();

        try{
            mCamera = Camera.open();
            mCameraView = new CameraView(this, mCamera);
            setContentView(mCameraView);
        } catch (Exception e){
            finish();
        }
    }

    @Override
    protected void onPause(){
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }

}

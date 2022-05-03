package com.example.android.mycoolfilter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CannyEffectActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    private static final int cameraID = 00001111;
    int counter=0;

    boolean showCanny=true;
    boolean showBlur=false;

    Button toggleEffect;
    Button blurEffect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_canny_effect);
        getSupportActionBar().hide();



        cameraBridgeViewBase= (JavaCameraView)findViewById(R.id.cannyCameraView);
        cameraBridgeViewBase.setCameraPermissionGranted();
        cameraBridgeViewBase.setVisibility(View.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        toggleEffect=(Button) findViewById(R.id.toggle_canny_effect);
        blurEffect=(Button) findViewById(R.id.toggle_blur_effect);

        toggleEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCannyFilter();
            }
        });
        blurEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBlurFilter();
            }
        });

        baseLoaderCallback=new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    private void changeBlurFilter() {
        if(showBlur) showBlur=false;
        else showBlur=true;
    }

    public void changeCannyFilter(){
        showCanny=!showCanny;
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.v("CameraCheck","camera started");
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame=inputFrame.rgba();

        if(showBlur){
            Imgproc.blur(frame,frame,new Size(10,10));
        }
        if(showCanny) {
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);
            Imgproc.Canny(frame, frame, 100, 80);
        }

        return frame;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"Error occured on resuming",Toast.LENGTH_SHORT).show();
        }else{
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
}
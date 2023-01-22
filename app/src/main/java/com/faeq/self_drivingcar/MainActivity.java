package com.faeq.self_drivingcar;
//general app imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//Android imports
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
//OpenCV imports
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

//----------------------------------------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    //------------------------------------------------------------------------------------------------------
    //Tag for activity (So we know where logs are coming from)
    private static final String TAG = "MainActivity";
    //For Camera View
    JavaCameraView CameraView;
    //specifying that we are using the back camera (unable to specify wide-lens camera - maybe look into later)
    int activeCamera = CameraBridgeViewBase.CAMERA_ID_BACK;
    Mat mRGBA, mGray;
    //status
    private boolean sending = false;
    //Code for camera permissions
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    //------------------------------------------------------------------------------------------------------
    //initialises camera when app is first launched or when onResume is called from phone sleep
    private void initializeCamera(JavaCameraView CameraView, int activeCamera){
        CameraView.setCameraPermissionGranted();
        CameraView.setCameraIndex(activeCamera);
        CameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        CameraView.setCvCameraViewListener(this);
    }
    //------------------------------------------------------------------------------------------------------
    //Enables Camera when OpenCV loads correctly (see onResume())
    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(MainActivity.this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS) CameraView.enableView();
            else super.onManagerConnected(status);
        }
    };
    //------------------------------------------------------------------------------------------------------
    //Helps with the app starting up fast (instead of waiting for onResume())
    static{
        if (OpenCVLoader.initDebug()) Log.d(TAG, "OpenCV is configured correctly");
        else Log.d(TAG, "OpenCV is NOT configured correctly");
    }
    //------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //show Main Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //show Camera View
        CameraView = findViewById(R.id.CameraView);
        //need permission checking above Android 5 (Our phone runs Android 9)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions granted");
            initializeCamera(CameraView, activeCamera);
        } else {
            Log.d(TAG, "Permission prompt");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        //fullscreen Camera view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Start bluetooth connection with robot here
        //Set status to sending here, once bluetooth connection is established
    }
    //------------------------------------------------------------------------------------------------------
    //creates matrix for frame processing
    @Override
    public void onCameraViewStarted(int width, int height) { mRGBA = new Mat(height, width, CvType.CV_8UC4); }
    //------------------------------------------------------------------------------------------------------
    @Override
    public void onCameraViewStopped() { try {mRGBA.release();} catch(NullPointerException e){Log.d(TAG, "No frame to release");}}
    //------------------------------------------------------------------------------------------------------
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //the frame's matrices
        mRGBA = inputFrame.rgba();
        // if (sending) ...
        //process frame here - to find track

        //edge detection to find lines
        Mat edges = new Mat();
        //using Canny algorithm from OpenCV for edge detection
        Imgproc.Canny(mRGBA, edges, 80, 200);

        //using edges to find lines
        Mat lines = new Mat();
        //using Hough algorithm from OpenCV for line detection (still need to adjust these values)
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180,100,100);
        //loop through for each line
        for (int i = 0; i < lines.rows(); i++) {
            double[] vec = lines.get(i, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            //draw line on original frame            (color of line)
            Imgproc.line(mRGBA, start, end, new Scalar(0, 255, 0), 5);
        }
        //then send movement info. to robot
        //send to view
        return mRGBA;
    }
    //------------------------------------------------------------------------------------------------------
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { super.onPointerCaptureChanged(hasCapture); }
    //------------------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() { super.onDestroy(); if (CameraView != null) CameraView.disableView(); }
    //------------------------------------------------------------------------------------------------------
    @Override
    protected void onPause() { super.onPause(); if (CameraView != null) CameraView.disableView(); }
    //------------------------------------------------------------------------------------------------------
    @Override
    protected void onResume(){
        super.onResume();
        //Check if OpenCV has loaded correctly
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV is configured correctly");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV is NOT configured correctly");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, MainActivity.this, baseLoaderCallback);
        }
    }
    //------------------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            //Camera can be used
            Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            initializeCamera(CameraView, activeCamera);
        } else Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
    }
}
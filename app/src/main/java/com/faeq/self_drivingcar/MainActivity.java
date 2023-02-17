package com.faeq.self_drivingcar;
//general app imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//Android imports
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
//OpenCV imports
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
//Java imports; OpenCV methods require data to be given in lists of this type
import java.util.List;
//----------------------------------------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    //------------------------------------------------------------------------------------------------------
    //Tag for activity (we only have one so this is the MainActivity)
    private static final String TAG = "MainActivity";
    //For Camera View
    JavaCameraView CameraView;
    private int[] CameraViewlocation = new int[2];
    private int Camerax;
    private int Cameray;
    //specifying that we are using the back camera (unable to specify wide-lens camera - maybe look into later)
    int activeCamera = CameraBridgeViewBase.CAMERA_ID_BACK;
    //Code for camera permissions
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    //Color detection
    private boolean IsColorSelected = false;
    private Mat mRgba;
    private Scalar BlobColorRgba;
    private Scalar BlobColorHsv;
    private ColorBlobDetector Detector;
    private Mat Spectrum;
    private Size SpectrumSize;
    private Scalar ContourColor;
    private Scalar BoundingBoxColor;

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
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS){
                CameraView.enableView();
                CameraView.setOnTouchListener(MainActivity.this);
            }
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

        CameraView.getLocationOnScreen(CameraViewlocation);
        Camerax = CameraViewlocation[0];
        Cameray = CameraViewlocation[1];
    }
    //------------------------------------------------------------------------------------------------------
    //No need to manipulate frames - due to using back camera
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        Detector = new ColorBlobDetector();
        Spectrum = new Mat();
        BlobColorRgba = new Scalar(255);
        BlobColorHsv = new Scalar(255);
        SpectrumSize = new Size(200, 64);
        ContourColor = new Scalar(0,255,0,255);
        BoundingBoxColor = new Scalar(0,255,255,255);
    }
    //------------------------------------------------------------------------------------------------------
    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
    //------------------------------------------------------------------------------------------------------
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (IsColorSelected) {
            Detector.process(mRgba);
            List<MatOfPoint> contours = Detector.getContours();
            //draw contours found
            Imgproc.drawContours(mRgba, contours, -1, ContourColor, 5);
            int numBoxes = 0;
            //Draw a bounding box around all contours
            for (MatOfPoint c : contours){
                // Make sure contour area is large enough
                if (Imgproc.contourArea(c) > 5000){
                    //Drawing rectangle over found track
                    Rect boundingRect = Imgproc.boundingRect(c);
                    Imgproc.rectangle(mRgba,boundingRect, BoundingBoxColor, 5);
                    //check if box is outside of track guide
                    if (boundingRect.y < 73){
                        turnRight();
                    } else if (boundingRect.y > (Cameray+CameraView.getHeight())-73){
                        turnLeft();
                    } else {
                        inCenter();
                    }
                    numBoxes++;
                }
            }
            Log.d(TAG, "Robot; Number of boxes on screen: "+numBoxes);
            //Color being searched for
            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(BlobColorRgba);
        }

        return mRgba;
    }
    //------------------------------------------------------------------------------------------------------
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (CameraView.getWidth() - cols) / 2;
        int yOffset = (CameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        BlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < BlobColorHsv.val.length; i++)
            BlobColorHsv.val[i] /= pointCount;

        BlobColorRgba = convertScalarHsv2Rgba(BlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + BlobColorRgba.val[0] + ", " + BlobColorRgba.val[1] +
                ", " + BlobColorRgba.val[2] + ", " + BlobColorRgba.val[3] + ")");

        Detector.setHsvColor(BlobColorHsv);

        Imgproc.resize(Detector.getSpectrum(), Spectrum, SpectrumSize);

        IsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }
    //------------------------------------------------------------------------------------------------------
    public void inCenter(){
        //tell EV3 to stay in center
        Log.d(TAG, "Robot is to be kept in the center");
    }
    //------------------------------------------------------------------------------------------------------
    public void turnLeft(){
        //tell EV3 to turn left by given amount
        //findViewById(R.id.turningLeft).setVisibility(View.VISIBLE);
        Log.d(TAG, "Robot is turning left now");
    }
    //------------------------------------------------------------------------------------------------------
    public void turnRight(){
        //tell EV3 to turn right by given amount
        //findViewById(R.id.turningRight).setVisibility(View.VISIBLE);
        Log.d(TAG, "Robot is turning right now");
    }
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
    public void onCameraViewStopped() { try {mRgba.release();} catch(NullPointerException e){Log.d(TAG, "No frame to release");}}
}
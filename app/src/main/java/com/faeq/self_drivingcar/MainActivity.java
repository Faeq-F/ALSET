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
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
//Java imports
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";

    //OpenCV Camera View - please see the source file of the class for modifications made
    JavaCameraView CameraView;
    //specifying that we are using the back camera
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

    //Buffer for sides of guide - activity_main layout
    int TrackCenterBuffer = 14;
    //Track guide bounds for detecting rotation
    int TrackGuideLeft;
    int TrackGuideRight;

    //Bluetooth connection with EV3
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] { Manifest.permission.CAMERA, Manifest.permission.INTERNET };
    private final ServerHandler mHandler = new ServerHandler(this);
    private Server server;

    private void initializeCamera(JavaCameraView CameraView, int activeCamera){
        CameraView.setCameraPermissionGranted();
        CameraView.setCameraIndex(activeCamera);
        CameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        CameraView.setCvCameraViewListener(this);
        mHandler.init();
        checkBTpermissions();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //show Main Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //show Camera View
        CameraView = findViewById(R.id.CameraView);
        //checks camera permissions first then moves to checkBTpermissions()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions granted");
            initializeCamera(CameraView, activeCamera);
        } else {
            Log.d(TAG, "Permission prompt");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        //handle light button
        Button button = (Button) findViewById(R.id.bLight);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CameraView.toggleLight();
            }
        });
        //fullscreen Camera view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //keep the screen on
        CameraView.setKeepScreenOn(true);
        //setting the thresholds
        TrackGuideLeft = TrackCenterBuffer;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        TrackGuideRight = screenHeight - TrackCenterBuffer; //assuming navigation bar is hidden
    }

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

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);
        return new Scalar(pointMatRgba.get(0, 0));
    }

    public static String message=null;
    private class sendMessages extends Thread{
        sendMessages(){}

        public void run(){
            while(true){
                try {
                    Log.d("Send to EV3", MainActivity.message);
                    if (server.getState() != Server.STATE_CONNECTED) continue;
                    // Get the message bytes and tell the BluetoothChatService to write
                    server.write((MainActivity.message + "\n").getBytes());
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Log.d("Send to EV3", "could not sleep");
                    }
                } catch(Exception e){
                    Log.d("Send to EV3", "App is not calibrated");
                }
            }
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        if (IsColorSelected) {
            Detector.process(mRgba);
            List<MatOfPoint> contours = Detector.getContours();
            //draw contours found (green outline)
            Imgproc.drawContours(mRgba, contours, -1, ContourColor, 5);
            if (contours.size() == 0) MainActivity.message = "no_track_found";
            //Draw a bounding box around all contours
            for (MatOfPoint c : contours){
                //draw light blue rectangle around detected track
                Rect boundingRect = Imgproc.boundingRect(c);
                Imgproc.rectangle(mRgba,boundingRect, BoundingBoxColor, 5);
                //send message to EV3
                //(0,0) is top left corner
                if (boundingRect.y < TrackGuideLeft) MainActivity.message = "rotate_left";
                else if (boundingRect.y + boundingRect.height > TrackGuideRight) MainActivity.message = "rotate_right";
                else MainActivity.message = "forward";//robot is in the center of the track
            }
            //Color being searched for displayed in corner (helps calibrating)
            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(BlobColorRgba);
        }
        return mRgba;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();
        //touch area
        int xOffset = (CameraView.getWidth() - cols) / 2;
        int yOffset = (CameraView.getHeight() - rows) / 2;
        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;
        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");
        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;
        //rectangle around touch to average color of
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
        //get color
        BlobColorRgba = convertScalarHsv2Rgba(BlobColorHsv);
        Detector.setHsvColor(BlobColorHsv);
        Imgproc.resize(Detector.getSpectrum(), Spectrum, SpectrumSize);
        //The color of the track to follow has been chosen and detected
        IsColorSelected = true;
        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    private static class ServerHandler extends Handler {

        private final WeakReference<MainActivity> mActivity;
        private String mConnMsg = null;

        ServerHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        void init() {
            mConnMsg = mActivity.get().getString(R.string.title_not_connected);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) return;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 == Server.STATE_CONNECTED)
                        Toast.makeText(activity, mConnMsg, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_READ:
                    try {
                        // construct a string from the valid bytes in the buffer
                        new String((byte[]) msg.obj, 0, msg.arg1);
                    } catch (StringIndexOutOfBoundsException e) {
                        //EV3 is no longer connected
                        System.exit(-1);
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    String device = msg.getData().getString(Constants.DEVICE_NAME);
                    mConnMsg = activity.getString(R.string.title_connected_to, device);
                    Toast.makeText(activity, mConnMsg, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(activity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

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
        server.start();
    }

    private void checkBTpermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED)  missingPermissions.add(permission);
        }
        if (!missingPermissions.isEmpty()) {
            requestPermissions(missingPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            // all permissions granted
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(PERMISSION_REQUEST_CODE, REQUIRED_SDK_PERMISSIONS, grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //initial request
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            initializeCamera(CameraView, activeCamera);
        //later request from checkBTpermissions()
        } else if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int index = permissions.length - 1; index >= 0; --index) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[index] + " not granted", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            server = new Server(mHandler);
            server.start();
            new sendMessages().start();
        } else Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
    }

    //Enables Camera when OpenCV loads correctly
    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS){
                CameraView.enableView();
                CameraView.setOnTouchListener(MainActivity.this);
            } else super.onManagerConnected(status);
        }
    };

    //Helps with the app starting up fast (instead of waiting for onResume())
    static{
        if (OpenCVLoader.initDebug()) Log.d(TAG, "OpenCV is configured correctly");
        else Log.d(TAG, "OpenCV is NOT configured correctly");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { super.onPointerCaptureChanged(hasCapture); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CameraView != null) CameraView.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CameraView != null) CameraView.disableView();
        server.stop();
    }

    @Override
    public void onCameraViewStopped() {
        try {
            mRgba.release();
        } catch(NullPointerException e){
            Log.d(TAG, "No frame to release");
        }
    }
}
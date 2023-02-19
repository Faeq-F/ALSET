package com.faeq.self_drivingcar;
//OpenCV Imports
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
//Java imports - OpenCV methods require data to be given in lists of this type
import java.util.ArrayList;
import java.util.List;
//----------------------------------------------------------------------------------------------------------
public class ColorBlobDetector {
    // Lower and Upper bounds for range
    private final Scalar mLowerBound = new Scalar(0);
    private final Scalar mUpperBound = new Scalar(0);
    double minH;
    double maxH;
    // Minimum %area for filtering contours
    private final static double mMinContourArea = 0.1;
    // Color radius for checking range
    private final Scalar mColorRadius = new Scalar(25,50,50,0);
    private final Mat mSpectrum = new Mat();
    private final List<MatOfPoint> mContours = new ArrayList<>();
    //Matrices for processing
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();
    //------------------------------------------------------------------------------------------------------
    public Mat getSpectrum() {return mSpectrum;}
    //------------------------------------------------------------------------------------------------------
    public List<MatOfPoint> getContours() {return mContours;}
    //------------------------------------------------------------------------------------------------------
    public void setHsvColor(Scalar hsvColor) {
         if (hsvColor.val[0] >= mColorRadius.val[0])
             minH = hsvColor.val[0]-mColorRadius.val[0];
         else minH = 0;
        if (hsvColor.val[0]+mColorRadius.val[0] <= 255)
            maxH = hsvColor.val[0]+mColorRadius.val[0];
        else maxH = 255;
        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;
        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];
        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];
        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;
        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);
        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }
        //Converting spectrum from HSV to RGB
        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }
    //------------------------------------------------------------------------------------------------------
    public void process(Mat rgbaImage) {
        //Blur & down-sample
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
        //Converting down-sampled image to HSV
        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        //Dilating image
        Imgproc.dilate(mMask, mDilatedMask, new Mat());
        List<MatOfPoint> contours = new ArrayList<>();
        //finding contours
        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = 0;
        for (MatOfPoint wrapper : contours) {
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }
        // Filter contours by area and resizes them to fit the original image size
        mContours.clear();
        for (MatOfPoint contour : contours){
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }
}
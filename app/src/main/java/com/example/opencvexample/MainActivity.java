package com.example.opencvexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    JavaCameraView javaCameraView;
    Mat mRGBA, mRGBAT;

    float frame_width, frame_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        javaCameraView = (JavaCameraView) findViewById(R.id.cameraView);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
    }

    static
    {
        if(OpenCVLoader.initDebug()){
            Log.d("AAAAAAAAAA","AAAAAAAAAAAAAAAAA");
        }
        else
        {
            Log.d("BBBBBBBBBBBBBBBBBBBBB","BBBBBBBBBBBBBBBBBBBB");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(OpenCVLoader.initDebug()){
            Log.d("AAAAAAAAAA","AAAAAAAAAAAAAAAAA");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
        else
        {
            Log.d("BBBBBBBBBBBBBBBBBBBBB","BBBBBBBBBBBBBBBBBBBB");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height)
    {
        mRGBA = new Mat(height, width, CvType.CV_8UC4);
        //mat2 = new Mat(width, height, CvType.CV_16UC4);

        frame_width = width;
        frame_height = height;
    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();
    }

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        //Imgproc.GaussianBlur(mRGBA, mRGBAT, new Size(100, 100), 1.0);
        /**
        mRGBA = inputFrame.rgba();
        mRGBAT = mRGBA.t();
        Core.flip(mRGBA.t(), mRGBAT, 1);
        Imgproc.resize(mRGBAT, mRGBAT, mRGBA.size());
        return mRGBAT;
         **/
        Mat input = inputFrame.gray();
        Mat circles = new Mat();
        Imgproc.blur(input, input, new Size(7, 7), new Point(2, 2));
        Imgproc.HoughCircles(input, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 100, 100, 90, 0, 1000);

        Log.i("SSSSSSSSSSSSSSSSSSSSSS", String.valueOf("size: " + circles.cols()) + ", " + String.valueOf(circles.rows()));

        Point rect_sPoint = new Point((frame_width * 3) / 8, (frame_height * 1) / 5);
        Point rect_ePoint = new Point((frame_width * 5) / 8, (frame_height * 4) / 5);
        Imgproc.rectangle(input, rect_sPoint, rect_ePoint, new Scalar(50, 205, 50), 3);

        //Line 1
        Point line_sPoint = new Point((frame_width * 3) / 8, (frame_height * 2) / 5);
        Point line_ePoint = new Point((frame_width * 5) / 8, (frame_height * 2) / 5);
        Imgproc.line(input, line_sPoint, line_ePoint, new Scalar(50, 205, 50), 3);
        //Line 2
        line_sPoint = new Point((frame_width * 3) / 8, (frame_height * 3) / 5);
        line_ePoint = new Point((frame_width * 5) / 8, (frame_height * 3) / 5);
        Imgproc.line(input, line_sPoint, line_ePoint, new Scalar(50, 205, 50), 3);

        if (circles.cols() > 0) {
            for (int x=0; x < Math.min(circles.cols(), 2); x++ ) {
                double circleVec[] = circles.get(0, x);

                if (circleVec == null) {
                    break;
                }

                Point center = new Point((int) circleVec[0], (int) circleVec[1]);
                int radius = (int) circleVec[2];

                Imgproc.circle(input, center, 3, new Scalar(50, 205, 50), 5);
                Imgproc.circle(input, center, radius, new Scalar(50, 205, 50), 2);
            }
        }

        circles.release();
        input.release();

        mRGBA = inputFrame.rgba();
        mRGBAT = mRGBA.t();
        Core.flip(mRGBA.t(), mRGBAT, 1);
        Imgproc.resize(mRGBAT, mRGBAT, mRGBA.size());
        return mRGBAT;
    }
}

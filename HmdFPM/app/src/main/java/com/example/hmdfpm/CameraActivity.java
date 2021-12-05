package com.example.hmdfpm;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.stealthcopter.networktools.subnet.*;

import static android.Manifest.permission.CAMERA;

public class CameraActivity  extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private class PingCheckRunnable implements Runnable{
        private final AtomicBoolean running = new AtomicBoolean(false);

        @Override
        public void run() {
            running.set(true);
            while (running.get()) {
                Map<String, Device> devices = TCPLocalDeviceLoader.getInstance().devices;

                if(devices.isEmpty()) continue;

                Message message = new Message();
                message.what = 3;
                message.obj = devices;
                handler.sendMessage(message);
                running.set(false);
            }
        }

        public void stopThread(){
            running.set(false);
        }
    }

    private final Matcher matcher = new Matcher();
    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;

    private Button mButtonConnect = null;
    private Button mButtonUpdate = null;

    private ConstraintLayout mLayout = null;

    private float matchedPosX, matchedPosY;
    private TextView textView;

    private Thread netThread;

    private PingCheckRunnable pingCheckRunnable = new PingCheckRunnable();

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == 1){
                mButtonConnect.setVisibility(View.VISIBLE);
                return true;
            }

            if(msg.what == 0)
            {
                mButtonConnect.setVisibility(View.INVISIBLE);
            }

            if(msg.what == 3){
                if(textView.length() > 0) return false;

                Map<String, Device> devices = TCPLocalDeviceLoader.getInstance().devices;

                for (String ip : devices.keySet()) {
                    Device device = devices.get(ip);
                    textView.append("Device " + device.hostname+"\n");
                    textView.append("IP : " + device.ip + "\n");
                    textView.append("Mack : " + device.mac + "\n");
                    textView.append("\n");
                }
            }
            return false;
        }
    });

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;

                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.camera_activity);

        mLayout = (ConstraintLayout)findViewById(R.id.camera_layout);
        mButtonConnect = findViewById(R.id.camera_matched_button);
        mButtonUpdate = findViewById(R.id.update_button);
        textView = findViewById(R.id.textView);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0);


        mButtonConnect.setVisibility(View.INVISIBLE);
        mButtonUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickUpdate();
            }
        });

        netThread = new Thread(pingCheckRunnable);

        netThread.start();

        try{
            InputStream is = getAssets().open("TheLastNight.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            Mat image = new Mat();
            Utils.bitmapToMat(bitmap, image);
            matcher.setOriginImg(image);

        } catch (IOException e){
            Log.e("IO Error", "Cannot found file");
            e.printStackTrace();
        }

    }

    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat matInput = inputFrame.rgba();
        Mat img = matcher.run(matInput);

        if(img != null) {
            matInput = img;

            matchedPosX = matcher.centerOfMatches[0];
            matchedPosY = matcher.centerOfMatches[1];

            Message message = Message.obtain();
            message.what = 1;

            handler.sendMessage(message);
        }

        else{
            Message message = Message.obtain();
            message.what = 0;
            handler.sendMessage(message);
        }
        return matInput;
    }

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }else{
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( CameraActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    private void onClickUpdate() {
        textView.setText("");
        TCPLocalDeviceLoader.getInstance().clear();
        TCPLocalDeviceLoader.getInstance().findSubnetDevices();

        pingCheckRunnable.run();
    }

    public void clickConnectCallback(String sourceIp) {
        if (TCPLocalDeviceLoader.getInstance().devices.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Device in your local network", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TCPLocalDeviceLoader.getInstance().devices.containsKey(sourceIp)){
            Toast.makeText(getApplicationContext(), "Your device is not member of local network", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}

package com.example.hmdfpm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    static{
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void onClicked(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickNetTest(View view)
    {
        Intent intent = new Intent(this, NetworkTestActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickClientTest(View view)
    {
        Intent intent = new Intent(this, NetworkTestClientActivity.class);
        startActivity(intent);
        finish();
    }
}
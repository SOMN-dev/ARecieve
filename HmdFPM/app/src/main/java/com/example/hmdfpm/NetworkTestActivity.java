package com.example.hmdfpm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.hmdfpm.netservice.HMDRtpManager;
import com.example.hmdfpm.netservice.HMDServerRunnable;
import com.nist.jrtp.RtpException;

import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkTestActivity extends AppCompatActivity {

    private HMDRtpManager hmdRtpManager;
    private HMDServerRunnable hmdServerRunnable;

    private final int port = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_test);

        try
        {
            hmdServerRunnable = new HMDServerRunnable(hmdRtpManager);

            hmdRtpManager = HMDRtpManager.createRtpManager(port,1);
            hmdRtpManager.Initialize();
            hmdRtpManager.run();
        }
        catch (UnknownHostException | SocketException | RtpException e)
        {
            e.printStackTrace();
        }
    }
}
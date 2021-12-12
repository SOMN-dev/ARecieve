package com.example.hmdfpm.netservice;

import androidx.annotation.NonNull;

import com.nist.jrtp.RtpException;

import java.net.SocketException;

public class HMDServerRunnable implements Runnable {
    public HMDRtpManager getManager() {
        return manager;
    }

    public void setManager(HMDRtpManager manager) {
        this.manager = manager;
    }

    private HMDRtpManager manager;

    public HMDServerRunnable(HMDRtpManager manager)
    {
        this.manager = manager;
    }

    public void disconnect()
    {
        manager = null;
    }

    @Override
    public void run() {
        if(manager == null)
            return;

        try
        {
            manager.run();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        catch (RtpException e)
        {
            e.printStackTrace();
        }
    }
}

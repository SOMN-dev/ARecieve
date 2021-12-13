package com.example.hmdfpm.netservice;

import com.nist.*;
import com.nist.jrtp.RtpException;
import com.nist.jrtp.RtpManager;
import com.nist.jrtp.RtpSession;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Queue;

public class HMDRtpManager {
    private int port;

    private RtpManager rtpManager;
    private RtpSession session;

    private Queue<Thread> threadPool;

    private HMDRtpManager(int port) throws UnknownHostException
    {
        this.port = port;
        rtpManager = new RtpManager();
    }

    public static HMDRtpManager createRtpManager(int port) throws UnknownHostException
    {
        HMDRtpManager manager = new HMDRtpManager(port);
        return manager;
    }

    public static HMDRtpManager createRtpManager(int port, int poolSize) throws UnknownHostException
    {
        HMDRtpManager manager = new HMDRtpManager(port);
        manager.threadPool = new ArrayDeque<>(poolSize);
        return manager;
    }

    public void Initialize() throws SocketException
    {
        session = rtpManager.createRtpSession(port);
        session.addRtpListener(new HMDRtpListener());
    }

    public void run() throws RtpException, SocketException
    {
        session.receiveRTPPackets();
    }

    public void runAsync(Runnable runnable)
    {
        Thread thread = threadPool.peek();

        thread = new Thread(runnable);
        thread.start();
    }
}

package com.example.hmdfpm.netservice;

import com.nist.jrtp.RtpErrorEvent;
import com.nist.jrtp.RtpListener;
import com.nist.jrtp.RtpPacket;
import com.nist.jrtp.RtpPacketEvent;
import com.nist.jrtp.RtpStatusEvent;
import com.nist.jrtp.RtpTimeoutEvent;

public class HMDRtpListener implements RtpListener {
    @Override
    public void handleRtpPacketEvent(RtpPacketEvent rtpEvent) {
        RtpPacket packet = rtpEvent.getRtpPacket();

        byte[] bytes = packet.getPayload();
    }

    @Override
    public void handleRtpStatusEvent(RtpStatusEvent rtpEvent) {

    }

    @Override
    public void handleRtpTimeoutEvent(RtpTimeoutEvent rtpEvent) {

    }

    @Override
    public void handleRtpErrorEvent(RtpErrorEvent rtpEvent) {

    }
}

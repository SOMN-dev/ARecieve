package com.example.hmdfpm;

import android.media.audiofx.AudioEffect;

import com.example.hmdfpm.netservice.IWrapper;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfKeyPoint;

import java.io.Serializable;

public class KeypointWrapper implements IWrapper, Serializable {

    private String ipAddress;
    private String macAddress;
    private float row;
    private float column;

    private MatOfKeyPoint matOfKeyPoint;
    private Mat descriptor;

    public KeypointWrapper(float row, float column, MatOfKeyPoint matOfKeyPoint, Mat descriptor){
        this.row = row;
        this.column = column;
        this.matOfKeyPoint = matOfKeyPoint;
        this.descriptor = descriptor;

    }

    public MatOfKeyPoint getMatOfKeyPoint() {
        return matOfKeyPoint;
    }

    public void setMatOfKeyPoint(MatOfKeyPoint matOfKeyPoint) {
        this.matOfKeyPoint = matOfKeyPoint;
    }

    public Mat getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Mat descriptor) {
        this.descriptor = descriptor;
    }

    public float getRow() {
        return row;
    }

    public void setRow(float row) {
        this.row = row;
    }

    public float getColumn() {
        return column;
    }

    public void setColumn(float column) {
        this.column = column;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}

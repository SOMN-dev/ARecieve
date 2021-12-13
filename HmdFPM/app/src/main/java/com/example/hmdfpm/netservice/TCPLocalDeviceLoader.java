package com.example.hmdfpm.netservice;

import com.stealthcopter.networktools.*;
import com.stealthcopter.networktools.subnet.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TCPLocalDeviceLoader {

    private TCPLocalDeviceLoader(){
        findSubnetDevices();
    }

    private static class InnerInstanceClass{
        private static final TCPLocalDeviceLoader instance = new TCPLocalDeviceLoader();
    }

    public static TCPLocalDeviceLoader getInstance()
    {
        return InnerInstanceClass.instance;
    }

    private static TCPLocalDeviceLoader instance = null;

    public Map<String, Device> devices = new HashMap<>();

    public void findSubnetDevices() {
        final long startTimeMillis = System.currentTimeMillis();
        SubnetDevices subnetDevices = SubnetDevices.fromLocalAddress().findDevices(
                new SubnetDevices.OnSubnetDeviceFound() {

            @Override
            public void onDeviceFound(Device device) {
            }

            public void onFinished(ArrayList<Device> devicesFound) {
                float timeTaken = (System.currentTimeMillis() - startTimeMillis) / 1000.0f;
                for (Device device : devicesFound) {
                    if (!devices.containsKey(device.ip))
                        devices.put(device.ip, device);
                }
            }
        });
    };

    public void clear() {
        devices.clear();
    }
}


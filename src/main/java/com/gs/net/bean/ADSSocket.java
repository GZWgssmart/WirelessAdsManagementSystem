package com.gs.net.bean;

import com.gs.net.server.ADSServerV2;

import java.net.Socket;

/**
 * Created by WangGenshen on 7/27/16.
 */
public class ADSSocket {

    private String deviceId;
    private String deviceCode;
    private String deviceIP;
    private Socket socket;
    private ADSServerV2.ReadThread readThread;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceIP() {
        return deviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ADSServerV2.ReadThread getReadThread() {
        return readThread;
    }

    public void setReadThread(ADSServerV2.ReadThread readThread) {
        this.readThread = readThread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ADSSocket adsSocket = (ADSSocket) o;

        return deviceCode.equals(adsSocket.deviceCode);

    }

    @Override
    public int hashCode() {
        return deviceCode.hashCode();
    }
}

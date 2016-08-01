package com.gs.net.bean;

import java.net.Socket;

/**
 * Created by WangGenshen on 7/27/16.
 */
public class ADSSocket {

    private String deviceId;
    private String deviceCode;
    private String deviceIP;
    private Socket socket;

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

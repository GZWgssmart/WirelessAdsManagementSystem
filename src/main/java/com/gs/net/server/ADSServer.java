package com.gs.net.server;

import com.alibaba.fastjson.JSON;
import com.gs.bean.Device;
import com.gs.common.Constants;
import com.gs.common.util.Config;
import com.gs.common.util.DateFormatUtil;
import com.gs.net.bean.ADSSocket;
import com.gs.net.parser.*;
import com.gs.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 * Created by WangGenshen on 7/27/16.
 */
@Component("adsServer")
public class ADSServer {

    private static final Logger logger = LoggerFactory.getLogger(ADSServer.class);

    public static final String ONLINE = "Y";
    public static final String OFFLINE = "N";

    private static int port;
    private ServerSocket serverSocket;
    private Map<String, ADSSocket> adsSockets;

    private List<String> handlingDevices;

    private boolean serverStarted;

    @Resource
    private DeviceService deviceService;

    static {
        Config config = new Config();
        config.build("classpath:/conf/port.properties");
        port = config.getInt("port");
    }

    public ADSServer() {
        adsSockets = new HashMap<String, ADSSocket>();
        handlingDevices = new ArrayList<String>();
    }

    public void startServer() {
        serverStarted = true;
        new Thread(new ConnectThread()).start();
        logger.info("ADSServer服务器已经启动......");
    }

    public void stopServer() {
        serverStarted = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                adsSockets = null;
                logger.info("ADSServer服务器已经被停止......");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread implements Runnable {

        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                while (serverStarted) {
                    logger.info("等待客户端的连接...");
                    Socket socket = serverSocket.accept();
                    logger.info("一个客户端连接上......");
                    InetAddress inetAddress = socket.getInetAddress();
                    ADSSocket adsSocket = new ADSSocket();
                    adsSocket.setDeviceIP(inetAddress.getHostAddress());
                    adsSocket.setSocket(socket);
                    startRead(adsSocket);
                }
            } catch (SocketException e) {
                stopServer();
                logger.info("服务器连接被关闭......");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadThread implements Runnable {

        private ADSSocket adsSocket;
        private boolean needRunning = true;

        public ReadThread(ADSSocket adsSocket) {
            this.adsSocket = adsSocket;
        }

        public void run() {

            while (needRunning) {
                try {
                    InputStream in = adsSocket.getSocket().getInputStream();
                    int dataLength = in.available();
                    if (dataLength > 0) {
                        byte[] bytes = new byte[in.available()];
                        in.read(bytes);
                        String msg = StringUnicodeUtil.unicodeToString(new String(bytes, Constants.DEFAULT_ENCODING));
                        logger.info("接收来自客户端的信息: " + msg);
                        if (msg.contains("\"" + Common.TYPE_CHECK + "\"")) {
                            logger.info("读取到客户端心跳包信息.....");
                            // 接收客户端心跳包并解析
                            HeartBeatClient heartBeatClient = JSON.parseObject(msg, HeartBeatClient.class);
                            adsSocket.setDeviceCode(heartBeatClient.getDevcode());
                            adsSockets.put(heartBeatClient.getDevcode(), adsSocket);
                            updateDeviceStatus(adsSocket, ONLINE);
                            // 服务端反馈到客户端
                            HeartBeatServer heartBeatServer = new HeartBeatServer();
                            heartBeatServer.setDevcode(heartBeatClient.getDevcode());
                            heartBeatServer.setType(Common.TYPE_CHECK);
                            heartBeatServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
                            writeHeartBeat(adsSocket, heartBeatServer);
                        } else if (msg.contains("\"" + Common.TYPE_DOWNLOAD + "\"")) {
                            logger.info("读取到客户端文件下载反馈......");
                            FileDownloadClient fileDownloadClient = JSON.parseObject(msg, FileDownloadClient.class);
                            handlingDevices.remove(fileDownloadClient.getDevcode());
                            System.out.println(fileDownloadClient);
                        } else if (msg.contains("\"" + Common.TYPE_PUBLISH + "\"")) {
                            logger.info("读取到客户端消息发布反馈......");
                            PublishClient publishClient = JSON.parseObject(msg, PublishClient.class);
                            handlingDevices.remove(publishClient.getDevcode());
                            System.out.println(publishClient);
                        } else if (msg.contains("\"" + Common.TYPE_DELETE + "\"")) {
                            logger.info("读取到客户端文件删除反馈......");
                            FileDeleteClient fileDeleteClient = JSON.parseObject(msg, FileDeleteClient.class);
                            handlingDevices.remove(fileDeleteClient.getDevcode());
                            System.out.println(fileDeleteClient);
                        } else {
                            logger.info("读取到其他消息......");
                        }
                    }
                } catch (SocketException e) {
                    needRunning = false;
                    logger.info("读取客户端信息:客户端与服务端失去连接......");
                    // 失去了连接，需要把此客户端从ADSSocket列表中移除
                    lostDeviceConnection(adsSocket);
                } catch (IOException e) {
                    needRunning = false;
                    e.printStackTrace();
                }

            }

        }
    }

    private void startRead(ADSSocket adsSocket) {
        ReadThread readThread = new ReadThread(adsSocket);
        new Thread(readThread).start();

    }

    private class WriteThread implements Runnable {

        private ADSSocket adsSocket;
        private String msg;

        public WriteThread(ADSSocket adsSocket) {
            this.adsSocket = adsSocket;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void run() {
            try {
                if (msg != null && msg.length() > 0) {
                    Socket socket = adsSocket.getSocket();
                    OutputStream out = socket.getOutputStream();
                    out.write(StringUnicodeUtil.stringToUnicode(msg).getBytes(Constants.DEFAULT_ENCODING));
                    logger.info("发送信息到客户端: " + msg);
                }
            } catch(SocketException e) {
                logger.info("写出信息到客户端:客户端与服务端失去连接......");
                // 失去了连接，需要把此客户端从ADSSocket列表中移除
                lostDeviceConnection(adsSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 开启写线程从服务端把消息写出到客户端
     * @param adsSocket
     * @param msg
     */
    private void startWrite(ADSSocket adsSocket, String msg) {
        WriteThread writeThread = new WriteThread(adsSocket);
        writeThread.setMsg(msg);
        new Thread(writeThread).start();
    }

    private void writeHeartBeat(ADSSocket adsSocket, HeartBeatServer server) {
        logger.info("发送心跳反馈到" + adsSocket.getDeviceCode() + "客户端");
        startWrite(adsSocket, JSON.toJSONString(server));
    }

    public String writeFileDownload(FileDownloadServer fileDownloadServer) {
        ADSSocket adsSocket = adsSockets.get(fileDownloadServer.getDevcode());
        if (adsSocket != null) {
            if (isDeviceWork(adsSocket)) {
                synchronized (Object.class) {
                    if (!handlingDevices.contains(fileDownloadServer.getDevcode())) { // 如果此时指定的客户端没有在处理,则可以下载通知
                        logger.info("发送文件下载通知到" + adsSocket.getDeviceCode() + "客户端");
                        startWrite(adsSocket, JSON.toJSONString(fileDownloadServer));
                        handlingDevices.add(fileDownloadServer.getDevcode());
                        return Common.DEVICE_WRITE_OUT;
                    } else {
                        return Common.DEVICE_IS_HANDLING;
                    }
                }
            } else {
                handlingDevices.remove(fileDownloadServer.getDevcode());
                return Common.DEVICE_NOT_CONNECT;
            }
        }
        return Common.DEVICE_NOT_CONNECT;
    }

    public String writePublish(PublishServer publishServer) {
        ADSSocket adsSocket = adsSockets.get(publishServer.getDevcode());
        if (adsSocket != null) {
            if (isDeviceWork(adsSocket)) {
                synchronized (Object.class) {
                    if (!handlingDevices.contains(publishServer.getDevcode())) {
                        logger.info("发送消息发布通知到" + adsSocket.getDeviceCode() + "客户端");
                        startWrite(adsSocket, JSON.toJSONString(publishServer));
                        handlingDevices.add(publishServer.getDevcode());
                        return Common.DEVICE_WRITE_OUT;
                    } else {
                        return Common.DEVICE_IS_HANDLING;
                    }
                }
            } else {
                handlingDevices.remove(publishServer.getDevcode());
                return Common.DEVICE_NOT_CONNECT;
            }
        }
        return Common.DEVICE_NOT_CONNECT;
    }

    public String writeFileDelete(FileDeleteServer fileDeleteServer) {
        ADSSocket adsSocket = adsSockets.get(fileDeleteServer.getDevcode());
        if (adsSocket != null) {
            if (isDeviceWork(adsSocket)) {
                synchronized (Object.class) {
                    if (!handlingDevices.contains(fileDeleteServer.getDevcode())) {
                        logger.info("发送文件删除通知到" + adsSocket.getDeviceCode() + "客户端");
                        startWrite(adsSocket, JSON.toJSONString(fileDeleteServer));
                        handlingDevices.add(fileDeleteServer.getDevcode());
                        return Common.DEVICE_WRITE_OUT;
                    } else {
                        return Common.DEVICE_IS_HANDLING;
                    }
                }
            } else {
                handlingDevices.remove(fileDeleteServer.getDevcode());
                return Common.DEVICE_NOT_CONNECT;
            }
        }
        return Common.DEVICE_NOT_CONNECT;
    }

    /**
     * 当设备不能与服务器连接时,更新设备的在线状态,status可选值为Y或N,也要更新上线或离线的时间
     * @param status
     */
    private void updateDeviceStatus(ADSSocket adsSocket, String status) {
        Device device = new Device();
        device.setCode(adsSocket.getDeviceCode());
        device.setOnline(status);
        Date time = Calendar.getInstance().getTime();
        if (status.equals("Y")) {
            device.setOnlineTime(time);
        } else if (status.equals("N")) {
            device.setOfflineTime(time);
        }
        logger.info("更新" +adsSocket.getDeviceCode() + "终端在线状态为:" + status);
        deviceService.updateStatus(device);
    }

    /**
     * 一旦丢失了连接，服务端需要把socket从ADSSocket中移除,并且更新设备在线状态
     * @param adsSocket
     */
    private void lostDeviceConnection(ADSSocket adsSocket) {
        updateDeviceStatus(adsSocket, OFFLINE);
        Socket socket = adsSocket.getSocket();
        if (socket.isConnected() && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        adsSockets.remove(adsSocket.getDeviceCode());
    }

    /**
     * 判断客户端是否正常连接，是否可以接收服务端消息
     * @param adsSocket
     * @return
     */
    private boolean isDeviceWork(ADSSocket adsSocket) {
        try {
            OutputStream out = adsSocket.getSocket().getOutputStream();
            out.write(0);
        } catch (SocketException e) {
            logger.info("在尝试发送消息到客户端时出现客户端不能连接的错误.....");
            lostDeviceConnection(adsSocket);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

package com.gs.net.server;

import com.alibaba.fastjson.JSON;
import com.gs.bean.Device;
import com.gs.bean.Publish;
import com.gs.bean.PublishLog;
import com.gs.bean.ResourceType;
import com.gs.common.Constants;
import com.gs.common.util.Config;
import com.gs.common.util.DateFormatUtil;
import com.gs.common.util.StringUnicodeUtil;
import com.gs.net.bean.ADSSocket;
import com.gs.net.parser.*;
import com.gs.service.DeviceService;
import com.gs.service.PublishPlanService;
import com.gs.service.PublishService;
import com.gs.service.ResourceTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.*;

/**
 * Created by WangGenshen on 7/27/16.
 */
//@Component("adsServer")
public class ADSServerV1 {

    private static final Logger logger = LoggerFactory.getLogger(ADSServerV1.class);

    private static int port;
    private static String siteDomain;
    private static int offlineTimeout;
    private static int sleepTime;
    private static int waitCount;
    private ServerSocket serverSocket;
    private Hashtable<String, ADSSocket> adsSockets;

    private Vector<String> handlingDevices;

    private boolean serverStarted;

    @Resource
    private DeviceService deviceService;
    @Resource
    private PublishService publishService;
    @Resource
    private PublishPlanService publishPlanService;
    @Resource
    private ResourceTypeService resourceTypeService;

    static {
        Config config = new Config();
        config.build("classpath:/conf/adsserver.properties");
        port = config.getInt(Common.PORT);
        siteDomain = config.getString(Common.SITE_DOMAIN);
        offlineTimeout = config.getInt(Common.HEART_BEAT_TIME) * 2 * 1000;
        sleepTime = config.getInt(Common.SLEEP_TIME) * 1000;
        waitCount = config.getInt(Common.WAIT_COUNT);
    }

    public ADSServerV1() {
        adsSockets = new Hashtable<String, ADSSocket>();
        handlingDevices = new Vector<String>();
    }

    public void startServer() {
        serverStarted = true;
        new Thread(new ConnectThread()).start(); // 服务器一旦启动，则等待终端设备的连接
        logger.info("ADSServer has been started......");
    }

    public void stopServer() {
        serverStarted = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                adsSockets = null;
                logger.info("ADSServer has been stopped......");
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
                    logger.info("the server is waiting connects from device...");
                    Socket socket = serverSocket.accept();
                    logger.info("one device has connected to the server......");
                    socket.setSoTimeout(offlineTimeout); // 如果三分种读不到信息，则认为终端下线
                    InetAddress inetAddress = socket.getInetAddress();
                    ADSSocket adsSocket = new ADSSocket();
                    adsSocket.setDeviceIP(inetAddress.getHostAddress());
                    adsSocket.setSocket(socket);
                    startRead(adsSocket); // 当设备连接上服务器后，服务器开始读设备发过来的信息
                }
            } catch (SocketException e) {
                stopServer();
                logger.info("SocketExcpeption occured when wait for devices, the server connection shutdown......");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startRead(ADSSocket adsSocket) {
        ReadThread readThread = new ReadThread(adsSocket);
        new Thread(readThread).start(); // 读取设备的反馈信息
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
                    Socket socket = adsSocket.getSocket();
                    InputStream in = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    int total = -1;
                    String message = "";
                    while ((total = in.read(bytes)) != -1) {
                        message += StringUnicodeUtil.unicodeToString(new String(bytes, 0, total, Constants.DEFAULT_ENCODING));
                        String[] msgs = getAllMsgs(message); // 获取所有的消息，包括最后可能的不完整的消息
                        int length = msgs.length; // 所有消息数
                        String lastMsg = msgs[length - 1]; // 最后一条消息
                        if (lastMsg.lastIndexOf("}") < 0) { // 如果最后一条消息不以}结尾，则这条消息应该与socket后面接收到的消息拼接到一起，真正需要读取的消息数就应该等于length - 1
                            message = lastMsg;
                            length -= 1;
                        } else {
                            message = ""; // 如果最后一条消息是以}结尾的，则不需要与socket后面接收到的消息拼接到一起，真正读取的消息数就等于length
                        }
                        if (length > 0) { // 需要被读取的消息数大于0
                            for (int i = 0; i < length; i++) {
                                String msg = msgs[i];
                                logger.info("read msg from device " + adsSocket.getDeviceCode() + ", the msg: " + msg);
                                if (msg.contains("\"" + Common.TYPE_CHECK + "\"")) {
                                    readHeartBeat(adsSocket, msg);
                                } else if (msg.contains("\"" + Common.TYPE_DOWNLOAD + "\"")) {
                                    readFileDownload(adsSocket, msg);
                                } else if (msg.contains("\"" + Common.TYPE_PUBLISH + "\"")) {
                                    readPublish(adsSocket, msg);
                                } else if (msg.contains("\"" + Common.TYPE_DELETE + "\"")) {
                                    readFileDelete(adsSocket, msg);
                                } else if (msg.contains("\"" + Common.TYPE_DELETE_ALL + "\"")) {
                                    readFileDelete(adsSocket, msg);
                                } else {
                                    logger.info("read other msg from device " + adsSocket.getDeviceCode() + ", the msg: " + msg);
                                }
                            }
                        }
                    }
                } catch (SocketTimeoutException e) {
                    needRunning = false;
                    logger.info("SocketTimeoutException occured when try to read msg from device " + adsSocket.getDeviceCode() + ", connection lost......");
                    lostDeviceConnection(adsSocket);
                } catch (SocketException e) {
                    needRunning = false;
                    logger.info("SocketException occured when try to read msg from device " + adsSocket.getDeviceCode() + ", connection lost......");
                    lostDeviceConnection(adsSocket);
                } catch (IOException e) {
                    needRunning = false;
                    logger.info("IOException occured when try to read msg from device " + adsSocket.getDeviceCode() + ", connection lost......");
                    lostDeviceConnection(adsSocket);
                }

            }

        }
    }

    private String[] getAllMsgs(String msg) {
        String[] msgs = msg.split("}");
        int len = msgs.length;
        if (msg.lastIndexOf("}") < msg.length() - 1) { // 说明整个字符串并没有以}结尾，最后一条消息是不完整的消息
            len -= 1; // 完整的消息条数是整个数组长度减去1
        }
        for (int i = 0; i < len; i++) {
            msgs[i] += "}"; // 所有完整的消息都需要添加}结尾
        }
        return msgs;
    }

    private void readHeartBeat(ADSSocket adsSocket, String msg) {
        // 接收客户端心跳包并解析
        HeartBeatClient heartBeatClient = JSON.parseObject(msg, HeartBeatClient.class);
        String deviceCode = heartBeatClient.getDevcode();
        logger.info("read the heart beat from device....." + deviceCode + ", the msg: " + msg);
        adsSocket.setDeviceCode(deviceCode);
        if (adsSockets.get(deviceCode) == null) { // 表示原先并没有连接上，是首次连接或重新连接，则需要把设备状态更新成在线状态
            logger.info(deviceCode + " connect to the server first time...");
            adsSockets.put(deviceCode, adsSocket);
            updateDeviceStatus(adsSocket, Common.DEVICE_ONLINE);
        }
        // 服务端反馈心跳到客户端
        writeHeartBeat(adsSocket);
        if (heartBeatClient.getFirstbeat().equals(Common.RESULT_Y)) {
            autoPublish(adsSocket, heartBeatClient);
        }
    }

    private void writeHeartBeat(ADSSocket adsSocket) {
        logger.info("send heart beat to " + adsSocket.getDeviceCode());
        HeartBeatServer heartBeatServer = new HeartBeatServer();
        heartBeatServer.setDevcode(adsSocket.getDeviceCode());
        heartBeatServer.setType(Common.TYPE_CHECK);
        heartBeatServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
        startWrite(null, heartBeatServer.getDevcode(), JSON.toJSONString(heartBeatServer));
    }

    private void readFileDownload(ADSSocket adsSocket, String msg) {
        logger.info("read file download msg from device " + adsSocket.getDeviceCode() + ", the msg: " + msg);
        FileDownloadClient fileDownloadClient = JSON.parseObject(msg, FileDownloadClient.class);
        handlingDevices.remove(fileDownloadClient.getDevcode());
        if (fileDownloadClient.getResult().equals(Common.RESULT_N)) {
            publishService.updatePublishLog(fileDownloadClient.getPubid(), PublishLog.FILE_NOT_DOWNLOADED);
        } else {
            // 如果客户端成功下载文件，则需要进行发布操作
            publishService.updatePublishLog(fileDownloadClient.getPubid(), PublishLog.FILE_DOWNLOADED);
            Publish publish = publishService.queryByDRId(fileDownloadClient.getPubid());
            writePublish(publish);
        }
    }

    private void readPublish(ADSSocket adsSocket, String msg) {
        logger.info("read publish msg from device " + adsSocket.getDeviceCode() + ", the msg: " + msg);
        PublishClient publishClient = JSON.parseObject(msg, PublishClient.class);
        handlingDevices.remove(publishClient.getDevcode());
        if (publishClient.getResult().equals(Common.RESULT_N)) {
            publishService.updatePublishLog(publishClient.getPubid(), PublishLog.NOT_PUBLISHED);
        } else {
            Publish publish = new Publish();
            publish.setPublishLog(PublishLog.PUBLISHED);
            publish.setId(publishClient.getPubid());
            Date time = Calendar.getInstance().getTime();
            publish.setPublishTime(time);
            publishService.updateWhenPublished(publish);
            Device device = new Device();
            device.setAdsUpdateTime(time);
            device.setCode(publishClient.getDevcode());
            deviceService.updatePublishTime(device);
            publishPlanService.updateCountByPubId(publishClient.getPubid());
            publishPlanService.finishByPubId(publishClient.getPubid());
        }
    }

    private void readFileDelete(ADSSocket adsSocket, String msg) {
        logger.info("read the file delete from device " + adsSocket.getDeviceCode() + ", the msg: " + msg);
        FileDeleteClient fileDeleteClient = JSON.parseObject(msg, FileDeleteClient.class);
        handlingDevices.remove(fileDeleteClient.getDevcode());
        if (fileDeleteClient.getType().equals(Common.TYPE_DELETE)) {
            if (fileDeleteClient.getResult().equals(Common.RESULT_N)) {
                publishService.updatePublishLog(fileDeleteClient.getPubid(), PublishLog.RESOURCE_NOT_DELETED);
            } else {
                publishService.updatePublishLog(fileDeleteClient.getPubid(), PublishLog.RESOURCE_DELETED);
            }
        } else if (fileDeleteClient.getType().equals(Common.TYPE_DELETE_ALL)) {
            if (fileDeleteClient.getResult().equals(Common.RESULT_N)) {
                publishService.updatePublishLogByDevCode(fileDeleteClient.getDevcode(), PublishLog.RESOURCE_NOT_DELETED);
            } else {
                publishService.updatePublishLogByDevCode(fileDeleteClient.getDevcode(), PublishLog.RESOURCE_DELETED);
            }
        }
    }

    /**
     * 开启写线程从服务端把消息写出到客户端
     * @param deviceCode
     * @param msg
     */
    private void startWrite(String publishId, String deviceCode, String msg) {
        WriteThread writeThread = new WriteThread(publishId, deviceCode);
        writeThread.setMsg(msg);
        new Thread(writeThread).start();
    }

    public void writeFileDownload(Publish publish) {
        String deviceCode = publish.getDevice().getCode();
        com.gs.bean.Resource resource = publish.getResource();
        FileDownloadServer fileDownloadServer = new FileDownloadServer();
        fileDownloadServer.setDevcode(deviceCode);
        fileDownloadServer.setPubid(publish.getId());
        fileDownloadServer.setType(Common.TYPE_DOWNLOAD);
        fileDownloadServer.setFilename(resource.getOfileName());
        fileDownloadServer.setFilesize(resource.getFileSize());
        fileDownloadServer.setUrl(siteDomain + "/" + resource.getPath());
        fileDownloadServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
        startWrite(fileDownloadServer.getPubid(), deviceCode, JSON.toJSONString(fileDownloadServer));
    }

    public void writePublish(Publish publish) {
        String deviceCode = publish.getDevice().getCode();
        com.gs.bean.Resource resource = publish.getResource();
        PublishServer publishServer = new PublishServer();
        publishServer.setType(Common.TYPE_PUBLISH);
        publishServer.setPubid(publish.getId());
        publishServer.setArea(publish.getArea());
        publishServer.setDevcode(deviceCode);
        if (publish.getEndTime() != null) {
            publishServer.setEnddate(DateFormatUtil.format(publish.getEndTime(), Common.DATE_PATTERN));
        }
        if (publish.getStartTime() != null) {
            publishServer.setStartdate(DateFormatUtil.format(publish.getStartTime(), Common.DATE_PATTERN));
        }
        publishServer.setFilename(resource.getOfileName());
        ResourceType resourceType = resourceTypeService.queryById(resource.getResourceTypeId());
        publishServer.setRestype(resourceType.getName());
        if (publish.getShowType() != null && publish.getShowType().equals("segment")) {
            String[] segments = publish.getSegments().split(",");
            publishServer.setSegcount(segments.length);
            publishServer.setSegments(publish.getSegments());
        } else {
            publishServer.setSegments("");
        }
        String showCount = publish.getShowCount();
        publishServer.setShowcount(showCount != null && !showCount.equals("") ? Integer.valueOf(showCount) : 0);
        publishServer.setShowtype(publish.getShowType());
        String stayTime = publish.getStayTime();
        publishServer.setStaytime(stayTime != null && !stayTime.equals("") ? Integer.valueOf(stayTime) : 0);
        publishServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
        startWrite(publishServer.getPubid(), deviceCode, JSON.toJSONString(publishServer));
    }

    public void writeFileDelete(Publish publish, int type) {
        String deviceCode = publish.getDevice().getCode();
        FileDeleteServer fileDeleteServer = new FileDeleteServer();
        fileDeleteServer.setDevcode(deviceCode);
        com.gs.bean.Resource resource = publish.getResource();
        fileDeleteServer.setPubid(publish.getId());
        if (type == DeleteType.DELETE_RES_FROM_DEVICE) {
            fileDeleteServer.setType(Common.TYPE_DELETE);
            fileDeleteServer.setFilename(resource.getOfileName());
        } else if (type == DeleteType.DELETE_ALL_RES_FROM_DEVICE) {
            fileDeleteServer.setType(Common.TYPE_DELETE_ALL);
            fileDeleteServer.setFilename("");
        }
        ResourceType resourceType = resourceTypeService.queryById(resource.getResourceTypeId());
        fileDeleteServer.setRestype(resourceType.getName());
        fileDeleteServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
        startWrite(fileDeleteServer.getPubid(), deviceCode, JSON.toJSONString(fileDeleteServer));
    }

    /**
     * 如果是连接后收到的第一个心跳包
     * 1)从文件下载开始，此时应该从正在处理的设备中移除,然后才能开始自动处理消息发布
     * 2)从消息发布开始，直接开始消息发布
     *
     * 如果是连接后收到第二个开始的心跳包
     *
     *
     * @param adsSocket
     */
    private void autoPublish(ADSSocket adsSocket, HeartBeatClient heartBeatClient) {
        String deviceCode = adsSocket.getDeviceCode();
        final List<Publish> publishes = publishService.queryByCode(deviceCode);
        if (heartBeatClient.getFirstbeat().equals(Common.RESULT_Y)) {
            handlingDevices.remove(deviceCode);
        }
        if (publishes != null && publishes.size() > 0) {
            // 有消息发布还未处理完,则需要顺序处理这些消息发布
            logger.info("the device " + deviceCode + " begin to handle the publishes automatically......");
            for (Publish publish : publishes) {
                String publishLog = publish.getPublishLog();
                if (publishLog.equals(PublishLog.SUBMIT_TO_CHECK) || publishLog.equals(PublishLog.FILE_DOWNLOADING) || publishLog.equals(PublishLog.FILE_NOT_DOWNLOADED)) {
                    logger.info("automatically file download publish id: " + publish.getId());
                    writeFileDownload(publish);
                } else if (publishLog.equals(PublishLog.FILE_DOWNLOADED) || publishLog.equals(PublishLog.PUBLISHING) || publishLog.equals(PublishLog.NOT_PUBLISHED)) {
                    logger.info("automatically publish handle publish id: " + publish.getId());
                    writePublish(publish);
                } else if (publishLog.equals(PublishLog.SUBMIT_TO_DELETE) || publishLog.equals(PublishLog.RESOURCE_DELETING) || publishLog.equals(PublishLog.RESOURCE_NOT_DELETED)) {
                    logger.info("automatically delete handle publish id: " + publish.getId());
                    writeFileDelete(publish, DeleteType.DELETE_RES_FROM_DEVICE);
                }
            }
        }
    }

    private class WriteThread implements Runnable {

        private String publishId;
        private String deviceCode;
        private String msg;
        private int count;

        public WriteThread(String publishId, String deviceCode) {
            this.publishId = publishId;
            this.deviceCode = deviceCode;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void run() {
            write();
        }

        /**
         * 从服务端写出消息
         * 海波包括心跳，通知
         * 如果是心跳消息，则只要设备不在使用中，则发送
         * 如果是通知消息，文件下载消息时，设置设备正在使用中，当接收到终端文件下载反馈时，设备不在使用中，可以进行消息发布消息，此时设备
         * 又进入使用状态，当接收到消息发布反馈时，设备不在使用中
         *
         * 写出消息时，如果发现设备在使用中，则此消息需要等待，一直等到设备不在使用中时才能够发送出去
         *
         * 如果消息已经写出到缓冲区，但是此时终端断开连接，则会失去连接，此时整个循环应该停止。当终端重新连接上后，需要自动发布。因为是重新创建新的socket
         * 所以可以直接发布
         *
         */
        private void write() {
            logger.info("begin to send msg to device " + deviceCode + ", the msg: " + msg);
            ADSSocket adsSocket = adsSockets.get(deviceCode);
            if (adsSocket != null) {
                Socket socket = adsSocket.getSocket();
                if (msg != null && msg.length() > 0) {
                    if (msg.contains("\"" + Common.TYPE_DOWNLOAD + "\"")) {
                        publishService.updatePublishLog(publishId, PublishLog.FILE_DOWNLOADING);
                    } else if (msg.contains("\"" + Common.TYPE_PUBLISH + "\"")) {
                        publishService.updatePublishLog(publishId, PublishLog.PUBLISHING);
                    } else if (msg.contains("\"" + Common.TYPE_DELETE + "\"")) {
                        publishService.updatePublishLog(publishId, PublishLog.RESOURCE_DELETING);
                    } else if (msg.contains("\"" + Common.TYPE_DELETE_ALL + "\"")) {
                        publishService.updatePublishLogByDevCode(deviceCode, PublishLog.RESOURCE_DELETING);
                    }
                    boolean needRun = true;
                    OutputStream out = null;
                    try {
                        out = socket.getOutputStream();
                    } catch (IOException e) {
                        needRun = false;
                        logger.info("IOException occured when try to get the OutputStream for " + deviceCode + ", connection lost......");
                        lostDeviceConnection(adsSocket);
                    }
                    while (needRun) {
                        try {
                            if (msg.contains("\"" + Common.TYPE_CHECK + "\"")) { // 如果是心跳消息，则直接及时回馈，不要判断当前终端是否在使用中
                                handlingDevices.add(deviceCode);
                                out.write(StringUnicodeUtil.stringToUnicode(msg).getBytes(Constants.DEFAULT_ENCODING));
                                out.flush();
                                logger.info("send msg to device " + deviceCode + ", the msg: " + msg);
                                if (msg.contains("\"" + Common.TYPE_CHECK + "\"")) {
                                    handlingDevices.remove(deviceCode);
                                }
                                needRun = false;
                            } else { // 如果不是心跳消息，则需要等待终端有反馈后才能从服务端把消息发出去
                                if (!handlingDevices.contains(deviceCode)) {
                                    handlingDevices.add(deviceCode);
                                    out.write(StringUnicodeUtil.stringToUnicode(msg).getBytes(Constants.DEFAULT_ENCODING));
                                    out.flush();
                                    logger.info("send msg to device " + deviceCode + ", the msg: " + msg);
                                    needRun = false;
                                } else { // 如果设备在处理中，则等待指定时间后继续执行此线程，等待指定的次数
                                    logger.info("waiting the device " + deviceCode + " for " + sleepTime + " seconds, the " + count + "th time....");
                                    if (++count > waitCount) {
                                        needRun = false;
                                        logger.info("waiting the device " + deviceCode + " over " + count + " times, no need to run the write thread any more....");
                                    }
                                    try {
                                        Thread.sleep(sleepTime);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (SocketTimeoutException e) {
                            needRun = false;
                            logger.info("SocketTimeoutException occured when send msg to device " + deviceCode + ", connection lost......");
                            lostDeviceConnection(adsSocket);
                        } catch (SocketException e) {
                            needRun = false;
                            logger.info("SocketException occured when send msg to device " + deviceCode + ", connection lost......");
                            lostDeviceConnection(adsSocket);
                        } catch (IOException e) {
                            needRun = false;
                            logger.info("IOException occured when send msg to device " + deviceCode + ", connection lost......");
                            lostDeviceConnection(adsSocket);
                        }
                    }
                }
            } else { // 没有连接，则不需要写出消息
                logger.info("do not write cause the device " + deviceCode + " is not connected...");
            }
        }
    }

    /**
     * 当设备不能与服务器连接时,更新设备的在线状态,status可选值为Y或N,也要更新上线或离线的时间
     * @param status
     */
    private void updateDeviceStatus(ADSSocket adsSocket, String status) {
        logger.info("update " +adsSocket.getDeviceCode() + " to " + status);
        deviceService.updateDeviceStatus(adsSocket.getDeviceCode(), status);
    }

    /**
     * 一旦丢失了连接，服务端需要把socket从ADSSocket中移除,并且更新设备在线状态
     * @param adsSocket
     */
    private void lostDeviceConnection(ADSSocket adsSocket) {
        updateDeviceStatus(adsSocket, Common.DEVICE_OFFLINE);
        Socket socket = adsSocket.getSocket();
        if (socket.isConnected() && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        handlingDevices.remove(adsSocket.getDeviceCode());
        adsSockets.remove(adsSocket.getDeviceCode());
    }

}

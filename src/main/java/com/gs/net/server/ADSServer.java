package com.gs.net.server;

import com.alibaba.fastjson.JSON;
import com.gs.bean.*;
import com.gs.common.Constants;
import com.gs.common.util.Config;
import com.gs.common.util.DateFormatUtil;
import com.gs.net.bean.ADSSocket;
import com.gs.net.parser.*;
import com.gs.service.*;
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

    private static int port;
    private static long heartBeatTime;
    private static int heartBeatCheckCount;
    private static long heartBeatCheckExtraTime;
    private static String siteDomain;
    private static long autoRunDelay;
    private ServerSocket serverSocket;
    private Map<String, ADSSocket> adsSockets;

    private List<String> handlingDevices;

    private boolean serverStarted;

    private Map<String, Integer> deviceHeartBeatCount;

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
        heartBeatTime = config.getLong(Common.HEART_BEAT_TIME) * 1000;
        heartBeatCheckCount = config.getInt(Common.HEART_BEAT_CHECK_COUNT);
        heartBeatCheckExtraTime = config.getLong(Common.HEART_BEAT_CHECK_EXTRA_TIME) * 1000;
        siteDomain = config.getString(Common.SITE_DOMAIN);
        autoRunDelay = config.getLong(Common.AUTO_RUN_DELAY) * 1000;
    }

    public ADSServer() {
        adsSockets = new HashMap<String, ADSSocket>();
        handlingDevices = new ArrayList<String>();
        deviceHeartBeatCount = new HashMap<String, Integer>();
    }

    public void startServer() {
        serverStarted = true;
        new Thread(new ConnectThread()).start();
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
                    InetAddress inetAddress = socket.getInetAddress();
                    ADSSocket adsSocket = new ADSSocket();
                    adsSocket.setDeviceIP(inetAddress.getHostAddress());
                    adsSocket.setSocket(socket);
                    startRead(adsSocket);
                }
            } catch (SocketException e) {
                stopServer();
                logger.info("SocketExcpeption occured, the server connection shutdown......");
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
                        if (msg.contains("\"" + Common.TYPE_CHECK + "\"")) {
                            logger.info("read heart beat from device: " + msg);
                            readHeartBeat(adsSocket, msg);
                        } else if (msg.contains("\"" + Common.TYPE_DOWNLOAD + "\"")) {
                            logger.info("read file download msg from device: " + msg);
                            readFileDownload(msg);
                        } else if (msg.contains("\"" + Common.TYPE_PUBLISH + "\"")) {
                            logger.info("read publish msg from device: " + msg);
                            readPublish(msg);
                        } else if (msg.contains("\"" + Common.TYPE_DELETE + "\"")) {
                            logger.info("reead file delete msg from device: " + msg);
                            readFileDelete(msg);
                        } else {
                            logger.info("read other msg from device......");
                        }
                    }
                } catch (SocketException e) {
                    needRunning = false;
                    logger.info("SocketException occured when try to read msg from device, connection lost......");
                    // 失去了连接，需要把此客户端从ADSSocket列表中移除
                    lostDeviceConnection(adsSocket);
                } catch (IOException e) {
                    needRunning = false;
                }

            }

        }
    }

    private void startRead(ADSSocket adsSocket) {
        ReadThread readThread = new ReadThread(adsSocket);
        new Thread(readThread).start();
    }

    /**
     * 检测设备是否连接的线程,每隔一段时间去检测心跳包收到的次数
     * 比如以3个心跳包为基准,每个心跳包时间间隔为10s,则计算每隔30-50秒（需要确定一个确切的值）
     * 是否接收到至少3个心跳包,如果是,则说明连接无问题,否则,终端连接不上
     */
    private class DeviceCheckThread implements Runnable {

        private ADSSocket adsSocket;

        public DeviceCheckThread(ADSSocket adsSocket) {
            this.adsSocket = adsSocket;
        }

        public void run() {
            long time = heartBeatCheckCount * heartBeatTime + heartBeatCheckExtraTime;
            while (true) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("begin to check " + time + "ms time get " + heartBeatCheckCount + " heart beats???....");
                if (deviceHeartBeatCount.get(adsSocket.getDeviceCode()) < heartBeatCheckCount) {
                    logger.info("specified " + time + "ms do not get " + heartBeatCheckCount + " heart beats....");
                    lostDeviceConnection(adsSocket); // 如果在指定的时间后，收到的心跳包个数小于这个时间段内可接收到的心跳数,则说明终端连接有问题,应该关闭连接并修改在线状态
                    break;
                } else {
                    logger.info("specified " + time + "ms get" + heartBeatCheckCount + " heart beats, connection from device is OK!!!!....");
                }
                deviceHeartBeatCount.put(adsSocket.getDeviceCode(), 0); // 重新开始心跳包计数
            }
        }
    }

    private void startCheckDeviceConnection(ADSSocket adsSocket) {
        DeviceCheckThread deviceCheckThread = new DeviceCheckThread(adsSocket);
        new Thread(deviceCheckThread).start();
    }

    private class WriteThread implements Runnable {

        private ADSSocket adsSocket;
        private String msg;
        private long delay;

        public WriteThread(ADSSocket adsSocket) {
            this.adsSocket = adsSocket;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

        public void run() {
            try {
                Thread.sleep(delay);
                if (msg != null && msg.length() > 0) {
                    Socket socket = adsSocket.getSocket();
                    OutputStream out = socket.getOutputStream();
                    out.write(StringUnicodeUtil.stringToUnicode(msg).getBytes(Constants.DEFAULT_ENCODING));
                    logger.info("send msg to device: " + msg);
                }
            } catch(SocketException e) {
                logger.info("SocketException occured when send msg to deivce, connection lost......");
                // 失去了连接，需要把此客户端从ADSSocket列表中移除
                lostDeviceConnection(adsSocket);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 开启写线程从服务端把消息写出到客户端
     * @param adsSocket
     * @param msg
     */
    private void startWrite(ADSSocket adsSocket, String msg, long delay) {
        WriteThread writeThread = new WriteThread(adsSocket);
        writeThread.setMsg(msg);
        writeThread.setDelay(delay);
        new Thread(writeThread).start();
    }

    private String writeHeartBeat(ADSSocket adsSocket) {
        if (isDeviceWork(adsSocket)) {
            logger.info("send heart beat to " + adsSocket.getDeviceCode());
            HeartBeatServer heartBeatServer = new HeartBeatServer();
            heartBeatServer.setDevcode(adsSocket.getDeviceCode());
            heartBeatServer.setType(Common.TYPE_CHECK);
            heartBeatServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
            startWrite(adsSocket, JSON.toJSONString(heartBeatServer), 0);
            return Common.DEVICE_WRITE_OUT;
        } else {
            return Common.DEVICE_NOT_CONNECT;
        }
    }

    public String writeFileDownload(Publish publish, boolean autoRun) {
        String deviceCode = publish.getDevice().getCode();
        ADSSocket adsSocket = adsSockets.get(deviceCode);
        if (adsSocket != null) {
            if (isDeviceWork(adsSocket)) {
                synchronized (Object.class) {
                    if (!handlingDevices.contains(deviceCode)) { // 如果此时指定的客户端没有在处理,则可以下载通知
                        logger.info("send file download to " + adsSocket.getDeviceCode());
                        com.gs.bean.Resource resource = publish.getResource();
                        FileDownloadServer fileDownloadServer = new FileDownloadServer();
                        fileDownloadServer.setDevcode(deviceCode);
                        fileDownloadServer.setPubid(publish.getId());
                        fileDownloadServer.setType(Common.TYPE_DOWNLOAD);
                        fileDownloadServer.setFilename(resource.getOfileName());
                        fileDownloadServer.setFilesize(resource.getFileSize());
                        fileDownloadServer.setUrl(siteDomain + "/" + resource.getPath());
                        fileDownloadServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
                        long delay = 0;
                        if (autoRun) {
                            delay = autoRunDelay;
                        }
                        startWrite(adsSocket, JSON.toJSONString(fileDownloadServer), delay);
                        handlingDevices.add(deviceCode);
                        return Common.DEVICE_WRITE_OUT;
                    } else {
                        logger.info(adsSocket.getDeviceCode() + " is busy when send file download to it from server...");
                        return Common.DEVICE_IS_HANDLING;
                    }
                }
            } else {
                handlingDevices.remove(deviceCode);
                return Common.DEVICE_NOT_CONNECT;
            }
        }
        return Common.DEVICE_NOT_CONNECT;
    }

    public String writePublish(Publish publish, boolean autoRun) {
        String deviceCode = publish.getDevice().getCode();
        ADSSocket adsSocket = adsSockets.get(deviceCode);
        if (adsSocket != null) {
            if (isDeviceWork(adsSocket)) {
                synchronized (Object.class) {
                    if (autoRun && handlingDevices.contains(deviceCode)) {
                        logger.info(adsSocket.getDeviceCode() + " is busy when send publish msg to it from server....");
                        return Common.DEVICE_IS_HANDLING;
                    } else if (autoRun && !handlingDevices.contains(deviceCode)) {
                        handlingDevices.add(deviceCode);
                    }
                    if (handlingDevices.contains(deviceCode)) {
                        logger.info("send publish msg to " + adsSocket.getDeviceCode());
                        com.gs.bean.Resource resource = publish.getResource();
                        PublishServer publishServer = new PublishServer();
                        publishServer.setType(Common.TYPE_PUBLISH);
                        publishServer.setPubid(publish.getId());
                        publishServer.setArea(publish.getArea());
                        publishServer.setDevcode(deviceCode);
                        publishServer.setEnddate(DateFormatUtil.format(publish.getEndTime(), Common.DATE_PATTERN));
                        publishServer.setStartdate(DateFormatUtil.format(publish.getStartTime(), Common.DATE_PATTERN));
                        publishServer.setFilename(resource.getOfileName());
                        ResourceType resourceType = resourceTypeService.queryById(resource.getResourceTypeId());
                        publishServer.setRestype(resourceType.getName());
                        if (publish.getShowType().equals("segment")) {
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
                        long delay = 0;
                        if (autoRun) {
                            delay = autoRunDelay;
                        }
                        startWrite(adsSocket, JSON.toJSONString(publishServer), delay);
                        return Common.DEVICE_WRITE_OUT;
                    }
                }
            } else {
                handlingDevices.remove(deviceCode);
                return Common.DEVICE_NOT_CONNECT;
            }
        }
        return Common.DEVICE_NOT_CONNECT;
    }

    public String writeFileDelete(Publish publish, boolean autoRun) {
        String deviceCode = publish.getDevice().getCode();
        ADSSocket adsSocket = adsSockets.get(deviceCode);
        if (adsSocket != null) {
            if (isDeviceWork(adsSocket)) {
                synchronized (Object.class) {
                    if (!handlingDevices.contains(deviceCode)) {
                        logger.info("send file delete msg to " + adsSocket.getDeviceCode());
                        FileDeleteServer fileDeleteServer = new FileDeleteServer();
                        fileDeleteServer.setDevcode(deviceCode);
                        com.gs.bean.Resource resource = publish.getResource();
                        fileDeleteServer.setFilename(resource.getOfileName());
                        fileDeleteServer.setPubid(publish.getId());
                        fileDeleteServer.setType(Common.TYPE_DELETE);
                        ResourceType resourceType = resourceTypeService.queryById(resource.getResourceTypeId());
                        fileDeleteServer.setRestype(resourceType.getName());
                        fileDeleteServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
                        long delay = 0;
                        if (autoRun) {
                            delay = autoRunDelay;
                        }
                        startWrite(adsSocket, JSON.toJSONString(fileDeleteServer), delay);
                        handlingDevices.add(deviceCode);
                        return Common.DEVICE_WRITE_OUT;
                    } else {
                        logger.info(adsSocket.getDeviceCode() + " is busy when send file delete msg to it from server....");
                        return Common.DEVICE_IS_HANDLING;
                    }
                }
            } else {
                handlingDevices.remove(deviceCode);
                return Common.DEVICE_NOT_CONNECT;
            }
        }
        return Common.DEVICE_NOT_CONNECT;
    }

    private void readHeartBeat(ADSSocket adsSocket, String msg) {
        // 接收客户端心跳包并解析
        HeartBeatClient heartBeatClient = JSON.parseObject(msg, HeartBeatClient.class);
        String deviceCode = heartBeatClient.getDevcode();
        logger.info("read the heat beat from device....." + deviceCode);
        adsSocket.setDeviceCode(deviceCode);
        if (adsSockets.get(deviceCode) == null) { // 每次重新连接收到终端心跳包时,启动检测线程
            logger.info(deviceCode + " connect to the server first time...");
            updateDeviceStatus(adsSocket, Common.DEVICE_ONLINE);
            deviceHeartBeatCount.put(deviceCode, 1);
            startCheckDeviceConnection(adsSocket);
            // 从此时开始,每次收到一个心跳包都加1
        } else {
            deviceHeartBeatCount.put(deviceCode, deviceHeartBeatCount.get(deviceCode) + 1);
        }
        adsSockets.put(deviceCode, adsSocket);
        // 服务端反馈到客户端
        writeHeartBeat(adsSocket);
        autoPublishWhenDeviceWork(adsSocket, heartBeatClient); // 每接收到一个心跳包就开始自动处理消息发布
    }

    private void readFileDownload(String msg) {
        logger.info("read file download msg from device......");
        FileDownloadClient fileDownloadClient = JSON.parseObject(msg, FileDownloadClient.class);
        if (fileDownloadClient.getResult().equals(Common.RESULT_N)) {
            publishService.updatePublishLog(fileDownloadClient.getPubid(), PublishLog.FILE_NOT_DOWNLOADED);
            handlingDevices.remove(fileDownloadClient.getDevcode()); // 只有文件下载反馈结果为失败时,才需要从正在处理的设备中移除,否则直接做发布消息通知
        } else {
            // 如果客户端成功下载文件，则需要进行发布操作
            publishService.updatePublishLog(fileDownloadClient.getPubid(), PublishLog.FILE_DOWNLOADED);
            Publish publish = publishService.queryByDRId(fileDownloadClient.getPubid());
            PublishPlan publishPlan = publishPlanService.queryWithResourceById(publish.getPublishPlanId());
            String result = writePublish(publish, false);
            if (result.equals(Common.DEVICE_WRITE_OUT)) {
                publishService.updatePublishLog(publish.getId(), PublishLog.PUBLISHING);
            }
        }
    }

    private void readPublish(String msg) {
        logger.info("read publish msg from device......");
        PublishClient publishClient = JSON.parseObject(msg, PublishClient.class);
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
        handlingDevices.remove(publishClient.getDevcode());
        System.out.println(publishClient);
    }

    private void readFileDelete(String msg) {
        logger.info("read the file delete from device......");
        FileDeleteClient fileDeleteClient = JSON.parseObject(msg, FileDeleteClient.class);
        if (fileDeleteClient.getResult().equals(Common.RESULT_N)) {
            publishService.updatePublishLog(fileDeleteClient.getPubid(), PublishLog.RESOURCE_NOT_DELETED);
        } else {
            publishService.updatePublishLog(fileDeleteClient.getPubid(), PublishLog.RESOURCE_DELETED);
        }
        handlingDevices.remove(fileDeleteClient.getDevcode());
        System.out.println(fileDeleteClient);
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
    private void autoPublishWhenDeviceWork(ADSSocket adsSocket, HeartBeatClient heartBeatClient) {
        final List<Publish> publishs = publishService.queryByCode(adsSocket.getDeviceCode());
        String deviceCode = adsSocket.getDeviceCode();
        if (heartBeatClient.getFirstbeat().equals(Common.RESULT_Y)) {
            handlingDevices.remove(deviceCode);
        }
        if (publishs != null && publishs.size() > 0) {
            // 有消息发布还未处理完,则需要顺序处理这些消息发布
            logger.info("the device " + deviceCode + " begin to handle the publishes automatically......");
            for (Publish publish : publishs) {
                logger.info("automatically handle publish id: " + publish.getId());
                String publishLog = publish.getPublishLog();
                if (publishLog.equals(PublishLog.SUBMIT_TO_CHECK) || publishLog.equals(PublishLog.FILE_DOWNLOADING) || publishLog.equals(PublishLog.FILE_NOT_DOWNLOADED)) {
                    String result = writeFileDownload(publish, true);
                    if (result.equals(Common.DEVICE_WRITE_OUT)) {
                        publishService.updatePublishLog(publish.getId(), PublishLog.FILE_DOWNLOADING);
                    }
                } else if (publishLog.equals(PublishLog.FILE_DOWNLOADED) || publishLog.equals(PublishLog.PUBLISHING) || publishLog.equals(PublishLog.NOT_PUBLISHED)) {
                    String result = writePublish(publish, true);
                    if (result.equals(Common.DEVICE_WRITE_OUT)) {
                        publishService.updatePublishLog(publish.getId(), PublishLog.PUBLISHING);
                    }
                } else if (publishLog.equals(PublishLog.RESOURCE_DELETING) || publishLog.equals(PublishLog.RESOURCE_NOT_DELETED)) {
                    String result = writeFileDelete(publish, true);
                    if (result.equals(Common.DEVICE_WRITE_OUT)) {
                        publishService.updatePublishLog(publish.getId(), PublishLog.RESOURCE_DELETING);
                    }
                }
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

    /**
     * 判断客户端是否正常连接，是否可以接收服务端消息
     * @param adsSocket
     * @return
     */
    private boolean isDeviceWork(ADSSocket adsSocket) {
        try {
            logger.info("begin to check device " + adsSocket.getDeviceCode() + " is online");
            OutputStream out = adsSocket.getSocket().getOutputStream();
            out.write(0); // 发送空字符
        } catch (SocketException e) {
            logger.info("SocketException occured when try to connect to the device.....");
            lostDeviceConnection(adsSocket);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

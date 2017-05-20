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
import com.gs.net.parser.*;
import com.gs.service.DeviceService;
import com.gs.service.PublishPlanService;
import com.gs.service.PublishService;
import com.gs.service.ResourceTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by WangGenshen on 7/27/16.
 */
@Component("adsServer")
public class ADSServer {

    private static final Logger logger = LoggerFactory.getLogger(ADSServer.class);

    private static int port;
    private static String siteDomain;
    private static int heartBeatTime;
    private static int totalDevicePerCheckThread;
    private static int checkThreadSleepTime;

    private Charset charset = Charset.forName(Constants.DEFAULT_ENCODING);

    private boolean serverStarted;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    private Hashtable<String, SocketChannel> adsSockets;
    private Hashtable<String, Long> lastBeatTime;

    private Vector<Hashtable<String, SocketChannel>> socketChannelMaps;

    @Resource
    private DeviceService deviceService;
    @Resource
    private PublishService publishService;
    @Resource
    private PublishPlanService publishPlanService;
    @Resource
    private ResourceTypeService resourceTypeService;

    private ExecutorService checkCachedThreadPool; // 用于检测终端是否在线的线程

    static {
        Config config = new Config();
        config.build("classpath:/conf/adsserver.properties");
        port = config.getInt(Common.PORT);
        siteDomain = config.getString(Common.SITE_DOMAIN);
        heartBeatTime = config.getInt(Common.HEART_BEAT_TIME) * 2 * 1000;
        totalDevicePerCheckThread = config.getInt(Common.TOTAL_DEVICE_PER_CHECK_THREAD);
        checkThreadSleepTime = config.getInt(Common.CHECK_THREAD_SLEEP_TIME) * 1000;
    }

    public ADSServer() {
        adsSockets = new Hashtable<String, SocketChannel>();
        lastBeatTime = new Hashtable<String, Long>();
        socketChannelMaps = new Vector<Hashtable<String, SocketChannel>>();
        checkCachedThreadPool = Executors.newCachedThreadPool();
    }

    public void startServer() {
        serverStarted = true;
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        while (selector.select() > 0) {
                            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                            while (selectionKeyIterator.hasNext()) {
                                SelectionKey selectionKey = selectionKeyIterator.next();
                                selectionKeyIterator.remove();
                                if (selectionKey.isAcceptable()) {
                                    connect(selector, serverSocketChannel);
                                } else if (selectionKey.isReadable()) {
                                    read(selectionKey);
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.info("ADSServer IOException when try to select from selector.....");
                    }
                }
            }).start();
        } catch (IOException e) {
            logger.info("ADSServer IOException when try to receive connects.....");
        }
        logger.info("ADSServer has been started......");
    }

    public void stopServer() {
        serverStarted = false;
        lastBeatTime.clear();
        socketChannelMaps.clear();
        checkCachedThreadPool.shutdown();
        if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
            try {
                selector.close();
                serverSocketChannel.close();
                logger.info("ADSServer has been stopped......");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connect(Selector selector, ServerSocketChannel serverSocketChannel) {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            logger.info("one device has connected to the server......");
        } catch (IOException e) {
            logger.info("IOException occured when connect to the server....");
        }

    }

    private void read(SelectionKey selectionKey) {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buff = ByteBuffer.allocate(1024);
        String message = "";
        try {
            while (socketChannel.read(buff) > 0) {
                buff.flip();
                message += StringUnicodeUtil.unicodeToString("" + charset.decode(buff));
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
                        logger.info("read msg from device , the msg: " + msg);
                        if (msg.contains("\"" + Common.TYPE_CHECK + "\"")) {
                            readHeartBeat(socketChannel, msg);
                        } else if (msg.contains("\"" + Common.TYPE_DOWNLOAD + "\"")) {
                            readFileDownload(socketChannel, msg);
                        } else if (msg.contains("\"" + Common.TYPE_PUBLISH + "\"")) {
                            readPublish(socketChannel, msg);
                        } else if (msg.contains("\"" + Common.TYPE_DELETE + "\"")) {
                            readFileDelete(socketChannel, msg);
                        } else if (msg.contains("\"" + Common.TYPE_DELETE_ALL + "\"")) {
                            readFileDelete(socketChannel, msg);
                        } else {
                            logger.info("read other msg from device, the msg: " + msg);
                        }
                    }
                }
            }
            selectionKey.interestOps(SelectionKey.OP_READ);
        } catch (IOException io) {
            logger.info("IOException occured when reading msg from device");
            selectionKey.cancel();
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
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

    private void readHeartBeat(SocketChannel socketChannel, String msg) {
        // 接收客户端心跳包并解析
        HeartBeatClient heartBeatClient = JSON.parseObject(msg, HeartBeatClient.class);
        String deviceCode = heartBeatClient.getDevcode();
        logger.info("read the heart beat from device....." + deviceCode + ", the msg: " + msg);
        lastBeatTime.put(deviceCode, System.currentTimeMillis());
        if (heartBeatClient.getFirstbeat().equals(Common.RESULT_Y)) { // 表示原先并没有连接上，是首次连接或重新连接，则需要把设备状态更新成在线状态
            logger.info(deviceCode + " connect to the server first time...");
            adsSockets.put(deviceCode, socketChannel);
            updateDeviceStatus(deviceCode, Common.DEVICE_ONLINE);
            addToCheck(deviceCode, socketChannel);
        }
        // 服务端反馈心跳到客户端
        writeHeartBeat(socketChannel, deviceCode);
        if (heartBeatClient.getFirstbeat().equals(Common.RESULT_Y)) {
            autoPublish(socketChannel, deviceCode);
        }
    }

    private void writeHeartBeat(SocketChannel socketChannel, String deviceCode) {
        logger.info("send heart beat to " + deviceCode);
        HeartBeatServer heartBeatServer = new HeartBeatServer();
        heartBeatServer.setDevcode(deviceCode);
        heartBeatServer.setType(Common.TYPE_CHECK);
        heartBeatServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
        write(socketChannel, null, heartBeatServer.getDevcode(), JSON.toJSONString(heartBeatServer));
    }

    private void readFileDownload(SocketChannel socketChannel, String msg) {
        FileDownloadClient fileDownloadClient = JSON.parseObject(msg, FileDownloadClient.class);
        String deviceCode = fileDownloadClient.getDevcode();
        logger.info("read file download msg from device " + deviceCode + ", the msg: " + msg);
        if (fileDownloadClient.getResult().equals(Common.RESULT_N)) {
            publishService.updatePublishLog(fileDownloadClient.getPubid(), PublishLog.FILE_NOT_DOWNLOADED);
        } else {
            // 如果客户端成功下载文件，则需要进行发布操作
            publishService.updatePublishLog(fileDownloadClient.getPubid(), PublishLog.FILE_DOWNLOADED);
            Publish publish = publishService.queryByDRId(fileDownloadClient.getPubid());
            writePublish(socketChannel, publish);
        }
    }

    private void readPublish(SocketChannel socketChannel, String msg) {
        PublishClient publishClient = JSON.parseObject(msg, PublishClient.class);
        String deviceCode = publishClient.getDevcode();
        logger.info("read publish msg from device " + deviceCode + ", the msg: " + msg);
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
            device.setCode(deviceCode);
            deviceService.updatePublishTime(device);
            publishPlanService.updateCountByPubId(publishClient.getPubid());
            publishPlanService.finishByPubId(publishClient.getPubid());
        }
    }

    private void readFileDelete(SocketChannel socketChannel, String msg) {
        FileDeleteClient fileDeleteClient = JSON.parseObject(msg, FileDeleteClient.class);
        String deviceCode = fileDeleteClient.getDevcode();
        logger.info("read the file delete from device " + deviceCode + ", the msg: " + msg);
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

    public void write(SocketChannel socketChannel, String publishId, String deviceCode, String msg) {
        logger.info("begin to send msg to device " + deviceCode + ", the msg: " + msg);
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
            try {
                socketChannel.write(charset.encode(StringUnicodeUtil.stringToUnicode(msg)));
                logger.info("send msg to device " + deviceCode + ", the msg: " + msg);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
               lostDeviceConnection(deviceCode, socketChannel);
            }
        }
    }

    public void writeFileDownload(SocketChannel socketChannel, Publish publish) {
        String deviceCode = publish.getDevice().getCode();
        if (socketChannel == null) {
            socketChannel = adsSockets.get(deviceCode);
        }
        com.gs.bean.Resource resource = publish.getResource();
        FileDownloadServer fileDownloadServer = new FileDownloadServer();
        fileDownloadServer.setDevcode(deviceCode);
        fileDownloadServer.setPubid(publish.getId());
        fileDownloadServer.setType(Common.TYPE_DOWNLOAD);
        fileDownloadServer.setFilename(resource.getOfileName());
        fileDownloadServer.setFilesize(resource.getFileSize());
        fileDownloadServer.setUrl(siteDomain + "/" + resource.getPath());
        fileDownloadServer.setTime(DateFormatUtil.format(Calendar.getInstance(), Common.DATE_TIME_PATTERN));
        write(socketChannel, fileDownloadServer.getPubid(), deviceCode, JSON.toJSONString(fileDownloadServer));
    }

    public void writePublish(SocketChannel socketChannel, Publish publish) {
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
        write(socketChannel, publishServer.getPubid(), deviceCode, JSON.toJSONString(publishServer));
    }

    public void writeFileDelete(SocketChannel socketChannel, Publish publish, int type) {
        String deviceCode = publish.getDevice().getCode();
        if (socketChannel == null) {
            socketChannel = adsSockets.get(deviceCode);
        }
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
        write(socketChannel, fileDeleteServer.getPubid(), deviceCode, JSON.toJSONString(fileDeleteServer));
    }

    /**
     * 如果是连接后收到的第一个心跳包
     * 1)从文件下载开始，此时应该从正在处理的设备中移除,然后才能开始自动处理消息发布
     * 2)从消息发布开始，直接开始消息发布
     * <p>
     * 如果是连接后收到第二个开始的心跳包
     *
     * @param socketChannel
     * @param deviceCode
     */
    private void autoPublish(SocketChannel socketChannel, String deviceCode) {
        final List<Publish> publishes = publishService.queryByCode(deviceCode);
        if (publishes != null && publishes.size() > 0) {
            // 有消息发布还未处理完,则需要顺序处理这些消息发布
            logger.info("the device " + deviceCode + " begin to handle the publishes automatically......");
            for (Publish publish : publishes) {
                String publishLog = publish.getPublishLog();
                if (publishLog.equals(PublishLog.SUBMIT_TO_CHECK) || publishLog.equals(PublishLog.FILE_DOWNLOADING) || publishLog.equals(PublishLog.FILE_NOT_DOWNLOADED)) {
                    logger.info("automatically file download publish id: " + publish.getId());
                    writeFileDownload(socketChannel, publish);
                } else if (publishLog.equals(PublishLog.FILE_DOWNLOADED) || publishLog.equals(PublishLog.PUBLISHING) || publishLog.equals(PublishLog.NOT_PUBLISHED)) {
                    logger.info("automatically publish handle publish id: " + publish.getId());
                    writePublish(socketChannel, publish);
                } else if (publishLog.equals(PublishLog.SUBMIT_TO_DELETE) || publishLog.equals(PublishLog.RESOURCE_DELETING) || publishLog.equals(PublishLog.RESOURCE_NOT_DELETED)) {
                    logger.info("automatically delete handle publish id: " + publish.getId());
                    writeFileDelete(socketChannel, publish, DeleteType.DELETE_RES_FROM_DEVICE);
                }
            }
        }
    }

    private void addToCheck(String deviceCode, SocketChannel socketChannel) {
        boolean added = false;
        for (Hashtable<String, SocketChannel> socketChannelHashtable : socketChannelMaps) {
            if (socketChannelHashtable.size() < totalDevicePerCheckThread) {
                socketChannelHashtable.put(deviceCode, socketChannel);
                added = true;
                break;
            }
        }
        if (!added) {
            Hashtable<String, SocketChannel> socketChannelHashtable = new Hashtable<String, SocketChannel>();
            socketChannelHashtable.put(deviceCode, socketChannel);
            socketChannelMaps.add(socketChannelHashtable);
            checkCachedThreadPool.execute(new CheckThread(socketChannelHashtable));
        }
    }

    private class CheckThread implements Runnable {

        private Hashtable<String, SocketChannel> socketChannelMap;
        private boolean needRun = true;

        private CheckThread(Hashtable<String, SocketChannel> socketChannelMap) {
            this.socketChannelMap = socketChannelMap;
        }

        public void run() {
            while (needRun) {
                try {
                    Thread.sleep(checkThreadSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Iterator<Map.Entry<String, SocketChannel>> entryIterator = socketChannelMap.entrySet().iterator();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, SocketChannel> entry = entryIterator.next();
                    String deviceCode = entry.getKey();
                    SocketChannel socketChannel = entry.getValue();
                    if (System.currentTimeMillis() - lastBeatTime.get(deviceCode) >= heartBeatTime) {
                        logger.info("check the device " + deviceCode + ", offline!!!!!");
                        lostDeviceConnection(deviceCode, socketChannel);
                        entryIterator.remove();
                        lastBeatTime.remove(deviceCode);
                    } else {
                        logger.info("check the device " + deviceCode + ", online!!!!!");
                    }
                }
                if (socketChannelMap.size() == 0) {
                    socketChannelMaps.remove(socketChannelMap);
                    needRun = false;
                }
            }
        }
    }

    /**
     * 当设备不能与服务器连接时,更新设备的在线状态,status可选值为Y或N,也要更新上线或离线的时间
     *
     * @param deviceCode
     * @param status
     */
    private void updateDeviceStatus(String deviceCode, String status) {
        logger.info("update " + deviceCode + " to " + status);
        deviceService.updateDeviceStatus(deviceCode, status);
    }

    /**
     * 一旦丢失了连接，服务端需要把socket从ADSSocket中移除,并且更新设备在线状态
     *
     * @param deviceCode
     * @param socketChannel
     */
    private void lostDeviceConnection(String deviceCode, SocketChannel socketChannel) {
        adsSockets.remove(deviceCode);
        lastBeatTime.remove(deviceCode);
        updateDeviceStatus(deviceCode, Common.DEVICE_OFFLINE);
        if (socketChannel.isConnected() && socketChannel.isOpen()) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

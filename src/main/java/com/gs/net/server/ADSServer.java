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
import java.nio.channels.*;
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

    private ExecutorService writeCachedThreadPool;

    private Hashtable<String, Deque<String>> msgQueueTable; // 存储每一个终端设备的消息队列
    private Hashtable<String, Boolean> handlingDevices;
    private Hashtable<String, String> devMsgs;

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
        heartBeatTime = config.getInt(Common.HEART_BEAT_TIME) * 2 * 1000;
        totalDevicePerCheckThread = config.getInt(Common.TOTAL_DEVICE_PER_CHECK_THREAD);
        checkThreadSleepTime = config.getInt(Common.CHECK_THREAD_SLEEP_TIME) * 1000;
    }

    public ADSServer() {
        adsSockets = new Hashtable<String, SocketChannel>();
        lastBeatTime = new Hashtable<String, Long>();
        socketChannelMaps = new Vector<Hashtable<String, SocketChannel>>();
        writeCachedThreadPool = Executors.newCachedThreadPool();
        msgQueueTable = new Hashtable<String, Deque<String>>();
        handlingDevices = new Hashtable<String, Boolean>();
        devMsgs = new Hashtable<String, String>();
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
                    while (true) {
                        try {
                            if (selector.select() > 0) {
                                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                                while (selectionKeyIterator.hasNext()) {
                                    SelectionKey selectionKey = selectionKeyIterator.next();
                                    selectionKeyIterator.remove();
                                    try {
                                        if (selectionKey.isAcceptable()) {
                                            connect(selector, serverSocketChannel);
                                        } else if (selectionKey.isReadable()) {
                                            read(selectionKey);
                                        }
                                    } catch (CancelledKeyException e) {
                                        selectionKey.cancel();
                                        selectionKey.channel().close();
                                        logger.info("ADSServer CancelledKeyException when try to select from selector.....");
                                    }
                                }
                            }
                        } catch (ClosedSelectorException e) {
                            logger.info("ADSServer ClosedSelectorException when try to select from selector.....");
                        } catch (IOException e) {
                            logger.info("ADSServer IOException when try to select from selector.....");
                        }
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
        handlingDevices.clear();
        msgQueueTable.clear();
        writeCachedThreadPool.shutdown();
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
        } catch (Exception e) {
            logger.info("Exception occured when connect to the server...." + e.getMessage());
        }

    }

    private void read(SelectionKey selectionKey) {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buff = ByteBuffer.allocate(1024 * 2 * 10);
        try {
            while (socketChannel.read(buff) > 0) {
                buff.flip();
                String message = StringUnicodeUtil.unicodeToString("" + charset.decode(buff));
                logger.info("read msg from device, the msg: " + message);
                String devcode = getDevcodeFromMsg(message);
                String oldMsg = devMsgs.get(devcode);
                if (oldMsg != null) {
                    message = oldMsg + message;
                }
                String[] msgs = getAllMsgs(message); // 获取所有的消息，包括最后可能的不完整的消息
                int length = msgs.length; // 所有消息数
                String lastMsg = msgs[length - 1]; // 最后一条消息
                if (lastMsg.lastIndexOf("}") < 0) { // 如果最后一条消息不以}结尾，则这条消息应该与socket后面接收到的消息拼接到一起，真正需要读取的消息数就应该等于length - 1
                    devMsgs.put(devcode, lastMsg);
                    length -= 1;
                } else {
                    devMsgs.remove(devcode); // 如果最后一条消息是以}结尾的，则不需要与socket后面接收到的消息拼接到一起，真正读取的消息数就等于length
                }
                if (length > 0) { // 需要被读取的消息数大于0
                    for (int i = 0; i < length; i++) {
                        String msg = msgs[i];
                        if (isHeartBeatMsg(msg)) {
                            readHeartBeat(socketChannel, msg);
                        } else if (isDownloadMsg(msg)) {
                            readFileDownload(socketChannel, msg);
                        } else if (isPublishMsg(msg)) {
                            readPublish(socketChannel, msg);
                        } else if (isDeleteMsg(msg)) {
                            readFileDelete(socketChannel, msg);
                        } else if (isDeleteAllMsg(msg)) {
                            readFileDelete(socketChannel, msg);
                        } else {
                            logger.info("read other msg from device, the msg: " + msg);
                        }
                    }
                }
            }
            selectionKey.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            logger.info("IOException occured when reading msg from device");
            selectionKey.cancel();
            try {
                socketChannel.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        } catch (Exception e) {
            logger.info("Exception occured when reading msg from device " + e.getMessage());
            selectionKey.cancel();
            try {
                socketChannel.close();
            } catch (IOException ee) {
                ee.printStackTrace();
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
            handlingDevices.remove(deviceCode);
            msgQueueTable.remove(deviceCode);
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
        startWrite(socketChannel, heartBeatServer.getDevcode(), JSON.toJSONString(heartBeatServer), false);
    }

    private void readFileDownload(SocketChannel socketChannel, String msg) {
        FileDownloadClient fileDownloadClient = JSON.parseObject(msg, FileDownloadClient.class);
        String deviceCode = fileDownloadClient.getDevcode();
        logger.info("read file download msg from device " + deviceCode + ", the msg: " + msg);
        if (fileDownloadClient.getResult().equals(Common.RESULT_N)) {
            handlingDevices.remove(deviceCode);
            publishService.updatePublishLog(fileDownloadClient.getPubid(), PublishLog.FILE_NOT_DOWNLOADED);
            startWrite(socketChannel, deviceCode, null, true); // 如果下载失败，则开始写出消息队列中的下一条消息
        } else {
            // 如果客户端成功下载文件，则需要进行发布操作
            publishService.updatePublishLog(fileDownloadClient.getPubid(), PublishLog.FILE_DOWNLOADED);
            Publish publish = publishService.queryByDRId(fileDownloadClient.getPubid());
            writePublish(socketChannel, publish, true, false); // 如果下载成功，则开始写出发布消息
        }
    }

    private void readPublish(SocketChannel socketChannel, String msg) {
        PublishClient publishClient = JSON.parseObject(msg, PublishClient.class);
        String deviceCode = publishClient.getDevcode();
        logger.info("read publish msg from device " + deviceCode + ", the msg: " + msg);
        handlingDevices.remove(deviceCode);
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
        startWrite(socketChannel, deviceCode, null, true); // 开始写出消息队列中的下一条消息
    }

    private void readFileDelete(SocketChannel socketChannel, String msg) {
        FileDeleteClient fileDeleteClient = JSON.parseObject(msg, FileDeleteClient.class);
        String deviceCode = fileDeleteClient.getDevcode();
        logger.info("read the file delete from device " + deviceCode + ", the msg: " + msg);
        handlingDevices.remove(deviceCode);
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
        startWrite(socketChannel, deviceCode, null, true); // 开始写出消息队列中的下一条消息
    }

    private class WriteThread implements Runnable {

        private SocketChannel socketChannel;
        private String deviceCode;
        private String msg;
        private boolean toCheckHandling;

        private WriteThread(SocketChannel socketChannel, String deviceCode, String msg, boolean toCheckHandling) {
            this.socketChannel = socketChannel;
            this.deviceCode = deviceCode;
            this.msg = msg;
            this.toCheckHandling = toCheckHandling;
        }

        public void run() {
            if (msg == null) { // 如果不是心跳消息
                if (toCheckHandling && handlingDevices.get(deviceCode) != null) { // 如果需要检测设备是否在使用中，并且设备在使用中，则不能写出消息
                    return;
                }
                Deque<String> msgQueue = msgQueueTable.get(deviceCode); // 获取设备消息队列
                if (msgQueue != null && msgQueue.size() > 0) {
                    msg = msgQueue.pop(); // 如果消息队列中有消息，则弹出第一条消息
                    handlingDevices.put(deviceCode, true); // 且添加为正在处理的设备
                }
            }
            if (msg != null && msg.length() > 0) {
                logger.info("begin to send msg to device " + deviceCode + ", the msg: " + msg);
                try {
                    socketChannel.write(charset.encode(StringUnicodeUtil.stringToUnicode(msg)));
                    String publishId = getPubIdFromMsg(msg);
                    if (isDownloadMsg(msg)) {
                        publishService.updatePublishLog(publishId, PublishLog.FILE_DOWNLOADING);
                    } else if (isPublishMsg(msg)) {
                        publishService.updatePublishLog(publishId, PublishLog.PUBLISHING);
                    } else if (isDeleteMsg(msg)) {
                        publishService.updatePublishLog(publishId, PublishLog.RESOURCE_DELETING);
                    } else if (isDeleteAllMsg(msg)) {
                        publishService.updatePublishLogByDevCode(deviceCode, PublishLog.RESOURCE_DELETING);
                    }
                    logger.info("successfully send msg to device " + deviceCode + ", the msg: " + msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    lostDeviceConnection(deviceCode, socketChannel);
                }
            }
        }
    }

    /**
     * @param socketChannel
     * @param deviceCode
     * @param heartBeatMsg  如果heartBeatMsg不为空，则说明是心跳消息，否则为其他消息，其他消息从设备各自的消息队列中获取
     */
    private void startWrite(SocketChannel socketChannel, String deviceCode, String heartBeatMsg, boolean toCheckHandling) {
        if (socketChannel != null) {
            writeCachedThreadPool.execute(new WriteThread(socketChannel, deviceCode, heartBeatMsg, toCheckHandling));
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
        addMsgToQueue(deviceCode, JSON.toJSONString(fileDownloadServer), false); // 添加消息到消息队列的队尾
        startWrite(socketChannel, deviceCode, null, true); // 开始写出消息队列中的消息
    }

    private void writePublish(SocketChannel socketChannel, Publish publish, boolean addToFirst, boolean autoPub) {
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
        addMsgToQueue(deviceCode, JSON.toJSONString(publishServer), addToFirst); // 将发布消息添加到队头
        boolean toCheckHandling = false;
        if (autoPub) { // 如果是自动发布的消息，则需要检查当前设备是否在使用中，而如果是文件下载后的发布，则不需要检查当前设备是否在使用中
            toCheckHandling = true;
        }
        startWrite(socketChannel, deviceCode, null, toCheckHandling); // 开始写出发布消息，此发布消息放在消息队列的队头
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
        addMsgToQueue(deviceCode, JSON.toJSONString(fileDeleteServer), false); // 添加消息到消息队列的队尾
        startWrite(socketChannel, deviceCode, null, true); // 开始写出消息队列中的消息
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
                if (publishLog.equals(PublishLog.ALREADY_CHECKED)
                        || publishLog.equals(PublishLog.FILE_DOWNLOADING)
                        || publishLog.equals(PublishLog.FILE_NOT_DOWNLOADED)) {
                    logger.info("automatically file download publish id: " + publish.getId());
                    writeFileDownload(socketChannel, publish);
                } else if (publishLog.equals(PublishLog.FILE_DOWNLOADED)
                        || publishLog.equals(PublishLog.PUBLISHING)
                        || publishLog.equals(PublishLog.NOT_PUBLISHED)) {
                    logger.info("automatically publish handle publish id: " + publish.getId());
                    writePublish(socketChannel, publish, false, true);
                } else if (publishLog.equals(PublishLog.SUBMIT_TO_DELETE)
                        || publishLog.equals(PublishLog.RESOURCE_DELETING)
                        || publishLog.equals(PublishLog.RESOURCE_NOT_DELETED)) {
                    logger.info("automatically delete handle publish id: " + publish.getId());
                    writeFileDelete(socketChannel, publish, DeleteType.DELETE_RES_FROM_DEVICE);
                }
            }
        }
    }

    private void addMsgToQueue(String deviceCode, String msg, boolean addToFirst) {
        Deque<String> queue = msgQueueTable.get(deviceCode);
        if (queue == null) {
            queue = new ArrayDeque<>();
        }
        if (addToFirst) {
            queue.addFirst(msg);
        } else {
            queue.add(msg);
        }
        msgQueueTable.put(deviceCode, queue);
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
            new Thread(new CheckThread(socketChannelHashtable)).start();
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
                    Long lastBeat = lastBeatTime.get(deviceCode);
                    if (lastBeat != null && (System.currentTimeMillis() - lastBeat > heartBeatTime)) {
                        logger.info("check the device " + deviceCode + ", offline!!!!!");
                        lostDeviceConnection(deviceCode, socketChannel);
                        entryIterator.remove();
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
        handlingDevices.remove(deviceCode);
        msgQueueTable.remove(deviceCode);
        updateDeviceStatus(deviceCode, Common.DEVICE_OFFLINE);
        logger.error("device {} lost connection....", deviceCode);
        if (socketChannel.isConnected() && socketChannel.isOpen()) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPubIdFromMsg(String msg) {
        String pub = "\"pubid\":\"";
        int beginIdx = msg.indexOf(pub) + pub.length();
        return msg.substring(beginIdx, beginIdx + 36);
    }

    private String getDevcodeFromMsg(String msg) {
        String dev = "\"devcode\":\"";
        int beginIdx = msg.indexOf(dev) + dev.length();
        return msg.substring(beginIdx, beginIdx + 13);
    }

    private boolean isHeartBeatMsg(String msg) {
        return msg.contains("\"" + Common.TYPE_CHECK + "\"");
    }

    private boolean isDownloadMsg(String msg) {
        return msg.contains("\"" + Common.TYPE_DOWNLOAD + "\"");
    }

    private boolean isPublishMsg(String msg) {
        return msg.contains("\"" + Common.TYPE_PUBLISH + "\"");
    }

    private boolean isDeleteMsg(String msg) {
        return msg.contains("\"" + Common.TYPE_DELETE + "\"");
    }

    private boolean isDeleteAllMsg(String msg) {
        return msg.contains("\"" + Common.TYPE_DELETE_ALL + "\"");
    }

}

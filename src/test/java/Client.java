import com.alibaba.fastjson.JSON;
import com.gs.common.Constants;
import com.gs.common.util.StringUnicodeUtil;
import com.gs.net.parser.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangGenshen on 10/19/16.
 */
public class Client {

    private String[] devCodes = new String[]{"2016-1019-001", "2016-1019-002"};

    Map<String, Socket> sockets = new HashMap<String, Socket>();

    public Client() {
        new Thread(new ConnectThread()).start();
    }

    class ConnectThread implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 2000; i++) {
                try {
                    Thread.sleep(1 * 1000);
                    Socket socket = new Socket("localhost", 8898);
                    sockets.put("code" + i, socket);
                    OutputStream out = socket.getOutputStream();
                    out.write(StringUnicodeUtil.stringToUnicode(getFirstBeatString("code" + i)).getBytes(Constants.DEFAULT_ENCODING));
                    System.out.println("code" + i);
                    new Thread(new BeatThread("code" + i)).start();
                    new Thread(new ReadThread("code" + i)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*
            for (int i =0; i < 500; i++) {
                try {
                    String devCode = "dev" + i;
                    Socket socket = new Socket("localhost", 8898);
                    sockets.put(devCode, socket);
                    OutputStream out = socket.getOutputStream();
                    out.write(StringUnicodeUtil.stringToUnicode(getFirstBeatString(devCode)).getBytes(Constants.DEFAULT_ENCODING));
                    new Thread(new BeatThread(devCode)).start();
                    new Thread(new ReadThread(devCode)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            */
        }

    }

    class BeatThread implements Runnable {

        private String devCode;

        public BeatThread(String devCode) {
            this.devCode = devCode;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(130 * 1000);
                    OutputStream out = sockets.get(devCode).getOutputStream();
                    out.write(StringUnicodeUtil.stringToUnicode(getFirstBeatString(devCode)).getBytes(Constants.DEFAULT_ENCODING));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class ReadThread implements Runnable {

        private String devCode;

        public ReadThread(String devCode) {
            this.devCode = devCode;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    InputStream in = sockets.get(devCode).getInputStream();
                    int dataLength = in.available();
                    OutputStream out = sockets.get(devCode).getOutputStream();

                    if (dataLength > 2) {
                        byte[] bytes = new byte[in.available()];
                        in.read(bytes);
                        String str = new String(bytes, Constants.DEFAULT_ENCODING);
                        //System.out.println("Unicode: " + str);
                        String msg = StringUnicodeUtil.unicodeToString(str);
                        //System.out.println("String: " + msg);
                        String firstPart = msg.substring(0, msg.indexOf("}") + 1);
                        String secondPart = msg.substring(msg.indexOf("}") + 1);
                        /**
                        System.out.println("第一部分：" + firstPart);
                        System.out.println("第二部分：" + secondPart);
                        msg = firstPart;
                        if (msg.contains("\"" + Common.TYPE_CHECK + "\"")) {
                            HeartBeatServer heartBeatServer = JSON.parseObject(msg, HeartBeatServer.class);
                            System.out.println("devcode: " + heartBeatServer.getDevcode() + " read heart beat");
                        } else if (msg.contains("\"" + Common.TYPE_DOWNLOAD + "\"")) {
                            FileDownloadServer fileDownloadServer = JSON.parseObject(msg, FileDownloadServer.class);
                            out.write(StringUnicodeUtil.stringToUnicode(getFileDownString(fileDownloadServer.getPubid(), fileDownloadServer.getDevcode())).getBytes(Constants.DEFAULT_ENCODING));
                            System.out.println("pubid: " + fileDownloadServer.getPubid() + "devcode: " + devCode + ", download");
                        } else if (msg.contains("\"" + Common.TYPE_PUBLISH + "\"")) {
                            PublishServer publishServer = JSON.parseObject(msg, PublishServer.class);
                            out.write(StringUnicodeUtil.stringToUnicode(getPubString(publishServer.getPubid(), publishServer.getDevcode())).getBytes(Constants.DEFAULT_ENCODING));
                            System.out.println("pubid: " + publishServer.getPubid() + "devcode: " + devCode + ", publish");
                        } else if (msg.contains("\"" + Common.TYPE_DELETE + "\"")) {
                            FileDeleteServer fileDeleteServer = JSON.parseObject(msg, FileDeleteServer.class);
                            out.write(StringUnicodeUtil.stringToUnicode(getDeleteString(fileDeleteServer.getPubid(), fileDeleteServer.getDevcode())).getBytes(Constants.DEFAULT_ENCODING));
                            System.out.println("pubid: " + fileDeleteServer.getPubid() + "devcode: " + devCode + ", delete");
                        }
                        if (secondPart.contains("\"" + Common.TYPE_DOWNLOAD + "\"")) {
                            FileDownloadServer fileDownloadServer = JSON.parseObject(secondPart, FileDownloadServer.class);
                            out.write(StringUnicodeUtil.stringToUnicode(getFileDownString(fileDownloadServer.getPubid(), fileDownloadServer.getDevcode())).getBytes(Constants.DEFAULT_ENCODING));
                            System.out.println("pubid: " + fileDownloadServer.getPubid() + "devcode: " + devCode + ", download");
                        } else if (secondPart.contains("\"" + Common.TYPE_PUBLISH + "\"")) {
                            PublishServer publishServer = JSON.parseObject(secondPart, PublishServer.class);
                            out.write(StringUnicodeUtil.stringToUnicode(getPubString(publishServer.getPubid(), publishServer.getDevcode())).getBytes(Constants.DEFAULT_ENCODING));
                            System.out.println("pubid: " + publishServer.getPubid() + "devcode: " + devCode + ", publish");
                        } else if (secondPart.contains("\"" + Common.TYPE_DELETE + "\"")) {
                            FileDeleteServer fileDeleteServer = JSON.parseObject(secondPart, FileDeleteServer.class);
                            out.write(StringUnicodeUtil.stringToUnicode(getDeleteString(fileDeleteServer.getPubid(), fileDeleteServer.getDevcode())).getBytes(Constants.DEFAULT_ENCODING));
                            System.out.println("pubid: " + fileDeleteServer.getPubid() + "devcode: " + devCode + ", delete");
                        }
                         */
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFirstBeatString(String devCode) {
        return "{\"type\":\"check\",\"devcode\":\"{devCode}\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"Y\"}".replace("{devCode}", devCode);
    }

    private String getBeatString(String devCode) {
        return "{\"type\":\"check\",\"devcode\":\"{devCode}\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"N\"}".replace("{devCode}", devCode);
    }

    private String getFileDownString(String pubId, String devCode) {
        return "{\"type\":\"download\",\"pubid\":\"{pubId}\",\"devcode\":\"{devCode}\",\"filename\":\"test.txt\",\"url\":\"http://115.28.37.189/uploads/test.txt\",\"result\":\"Y\",\"time\":\"2016/07/29 18:10:21\"}".replace("{pubId}", pubId).replace("{devCode}", devCode);
    }

    private String getPubString(String pubId, String devCode) {
        return "{\"type\":\"publish\",\"pubid\":\"{pubId}\",\"devcode\":\"{devCode}\",\"filename\":\"test.txt\",\"restype\":\"txt\",\"time\":\"2016/07/29 19:23:30\",\"result\":\"Y\"}".replace("{pubId}", pubId).replace("{devCode}", devCode);
    }

    private String getDeleteString(String pubId, String devCode) {
        return "{\"type\":\"delete\",\"pubid\":\"{pubId}\",\"devcode\":\"{devCode}\",\"filename\":\"test.txt\",\"result\":\"Y\",\"time\":\"2016/07/29 18:10:21\"}".replace("{pubId}", pubId).replace("{devCode}", devCode);
    }

    public static void main(String[] args) {
        new Client();
    }

}

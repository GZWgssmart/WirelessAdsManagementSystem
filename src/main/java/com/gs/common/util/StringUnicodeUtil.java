package com.gs.common.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by WangGenshen on 8/1/16.
 */
public class StringUnicodeUtil {

    public static String stringToUnicode(String str) {
        char[] chars = str.toCharArray();
        String unicode = "";
        for (char c : chars) {
            unicode = unicode + "0x" + Integer.toHexString(c);
        }
        return unicode;
    }

    public static String unicodeToString(String unicode) {
        String[] chars = unicode.split("0x");
        String str = "";
        for(int i = 0; i < chars.length; i++){
            if (!chars[i].equals("")) {
                str = str + (char) Integer.parseInt(chars[i], 16);
            }
        }
        return str;
    }

    public static void main(String[] args) {
        System.out.println("第一次心跳包: " + stringToUnicode("{\"type\":\"check\",\"devcode\":\"2016-1019-001\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"Y\"}"));
        System.out.println("第二次心跳包: " + stringToUnicode("{\"type\":\"check\",\"devcode\":\"2016-1019-001\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"N\"}"));
        System.out.println("文件下载反馈: " + stringToUnicode("{\"type\":\"download\",\"pubid\":\"e291c8bc-8eb7-11e6-b524-4febb0ad5294\",\"devcode\":\"2016-0822-001\",\"filename\":\"test.txt\",\"url\":\"http://115.28.37.189/uploads/test.txt\",\"result\":\"Y\",\"time\":\"2016/07/29 18:10:21\"}"));
        System.out.println("消息发布反馈: " + stringToUnicode("{\"type\":\"publish\",\"pubid\":\"e291c8bc-8eb7-11e6-b524-4febb0ad5294\",\"devcode\":\"2016-0822-001\",\"filename\":\"test.txt\",\"restype\":\"txt\",\"time\":\"2016/07/29 19:23:30\",\"result\":\"Y\"}"));
        System.out.println("文件删除反馈：" + stringToUnicode("{\"type\":\"delete\",\"pubid\":\"e291c8bc-8eb7-11e6-b524-4febb0ad5294\",\"devcode\":\"2016-0822-001\",\"filename\":\"test.txt\",\"result\":\"Y\",\"time\":\"2016/07/29 18:10:21\"}"));
        System.out.println("第一次心跳包: " + stringToUnicode("{\"type\":\"check\",\"devcode\":\"2016-0822-002\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"Y\"}"));
        System.out.println("第二次心跳包: " + stringToUnicode("{\"type\":\"check\",\"devcode\":\"2016-0822-002\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"N\"}"));
        System.out.println("文件下载反馈: " + stringToUnicode("{\"type\":\"download\",\"pubid\":\"99fffbae-6992-11e6-a764-52a95bcae315\",\"devcode\":\"2016-0822-002\",\"filename\":\"test.txt\",\"url\":\"http://115.28.37.189/uploads/test.txt\",\"result\":\"Y\",\"time\":\"2016/07/29 18:10:21\"}"));
        System.out.println("消息发布反馈: " + stringToUnicode("{\"type\":\"publish\",\"pubid\":\"99fffbae-6992-11e6-a764-52a95bcae315\",\"devcode\":\"2016-0822-002\",\"filename\":\"test.txt\",\"restype\":\"txt\",\"time\":\"2016/07/29 19:23:30\",\"result\":\"Y\"}"));

        System.out.println(unicodeToString("0x7b0x220x740x790x700x650x220x3a0x220x630x680x650x630x6B0x220x2c0x220x640x650x760x630x6F0x640x650x220x3a0x220x320x300x310x360x2D0x310x300x310x310x2D0x300x300x310x220x2c0x220x660x690x720x730x740x620x650x610x740x220x3a0x220x4E0x220x2c0x220x740x690x6D0x650x220x3a0x220x320x300x310x360x2F0x310x300x2F0x310x340x200x320x310x3A0x320x380x3A0x330x340x220x7D"));
        System.out.println(stringToUnicode("{\"type\":\"delete\",\"pubid\":\"0b40c288-8fbb-11e6-83de-00163e020cd6\",\"devcode\":\"\",\"filename\":\"6.jpg\",\"type\":\"图片\",\"result\":\"Y\",\"time\":\"2016/10/12 21:28:39\",\"result\":\"Y\"}"));
    }

}

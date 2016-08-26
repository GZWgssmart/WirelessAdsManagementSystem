package com.gs.net.parser;

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
        System.out.println("第一次心跳包: " + stringToUnicode("{\"type\":\"check\",\"devcode\":\"2016-0822-001\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"Y\"}"));
        System.out.println("第二次心跳包: " + stringToUnicode("{\"type\":\"check\",\"devcode\":\"2016-0822-001\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"N\"}"));
        System.out.println("文件下载反馈: " + stringToUnicode("{\"type\":\"download\",\"pubid\":\"66f3b77e-6b62-11e6-a764-52a95bcae315\",\"devcode\":\"2016-0822-001\",\"filename\":\"test.txt\",\"url\":\"http://115.28.37.189/uploads/test.txt\",\"result\":\"Y\",\"time\":\"2016/07/29 18:10:21\"}"));
        System.out.println("消息发布反馈: " + stringToUnicode("{\"type\":\"publish\",\"pubid\":\"66f3b77e-6b62-11e6-a764-52a95bcae315\",\"devcode\":\"2016-0822-001\",\"filename\":\"test.txt\",\"restype\":\"txt\",\"time\":\"2016/07/29 19:23:30\",\"result\":\"Y\"}"));

        System.out.println("第一次心跳包: " + stringToUnicode("{\"type\":\"check\",\"devcode\":\"2016-0822-002\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"Y\"}"));
        System.out.println("第二次心跳包: " + stringToUnicode("{\"type\":\"check\",\"devcode\":\"2016-0822-002\",\"time\":\"2016/8/23 20:38:21\",\"firstbeat\":\"N\"}"));
        System.out.println("文件下载反馈: " + stringToUnicode("{\"type\":\"download\",\"pubid\":\"99fffbae-6992-11e6-a764-52a95bcae315\",\"devcode\":\"2016-0822-002\",\"filename\":\"test.txt\",\"url\":\"http://115.28.37.189/uploads/test.txt\",\"result\":\"Y\",\"time\":\"2016/07/29 18:10:21\"}"));
        System.out.println("消息发布反馈: " + stringToUnicode("{\"type\":\"publish\",\"pubid\":\"99fffbae-6992-11e6-a764-52a95bcae315\",\"devcode\":\"2016-0822-002\",\"filename\":\"test.txt\",\"restype\":\"txt\",\"time\":\"2016/07/29 19:23:30\",\"result\":\"Y\"}"));

    }

}

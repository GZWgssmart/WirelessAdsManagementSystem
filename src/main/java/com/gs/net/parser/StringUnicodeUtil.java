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
        System.out.println(stringToUnicode("{\"type\":\"check\",\"devcode\":\"2017070702\",\"time\":\"2016/8/1 20:38:21\"}"));

        System.out.println(stringToUnicode("{\"devcode\":\"2017070702\",\"time\":\"2016/08/01 22:40:17\",\"type\":\"check\"}"));

        String input = "青岛宝瑞";
        System.out.println(unicodeToString("0x6D4B0x8BD5"));
    }

}

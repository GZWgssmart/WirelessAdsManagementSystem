package com.gs.common.util;

import org.springframework.test.context.TestExecutionListeners;

import java.util.UUID;

/**
 * Created by WangGenshen on 9/15/16.
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

}

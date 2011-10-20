/**
 * Project: dubbo.hello.sample.consumer
 * 
 * File Created at 2010-9-2
 * $Id$
 * 
 * Copyright 2010 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.dubbo.common.utils;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

/**
 * TODO Comment of LogTest
 * 
 * @author tony.chenl
 */
public class LogUtil {

    private static Logger Log = LoggerFactory.getLogger(LogUtil.class);

    public static void start() {
        DubboAppender.doStart();
    }
    
    public static void stop() {
        DubboAppender.doStop();
    }

    public static boolean checkNoError() {
        if (findLevel(Level.ERROR) == 0) {
            return true;
        } else {
            return false;
        }

    }

    public static int findName(String expectedLogName) {
        int count = 0;
        List<Log> logList = DubboAppender.logList;
        for (int i = 0; i < logList.size(); i++) {
            String logName = logList.get(i).getLogName();
            if (logName.contains(expectedLogName)) count++;
        }
        return count;
    }

    public static int findLevel(Level expectedLevel) {
        int count = 0;
        List<Log> logList = DubboAppender.logList;
        for (int i = 0; i < logList.size(); i++) {
            Level logLevel = logList.get(i).getLogLevel();
            if (logLevel.equals(expectedLevel)) count++;
        }
        return count;
    }

    public static int findThread(String expectedThread) {
        int count = 0;
        List<Log> logList = DubboAppender.logList;
        for (int i = 0; i < logList.size(); i++) {
            String logThread = logList.get(i).getLogThread();
            if (logThread.contains(expectedThread)) count++;
        }
        return count;
    }

    public static int findMessage(String expectedMessage) {
        int count = 0;
        List<Log> logList = DubboAppender.logList;
        for (int i = 0; i < logList.size(); i++) {
            String logMessage = logList.get(i).getLogMessage();
            if (logMessage.contains(expectedMessage)) count++;
        }
        return count;
    }

    public static <T> void printList(List<T> list) {
        Log.info("PrintList:");
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            Log.info(it.next().toString());
        }

    }
}

/**
 * Project: dubbo-monitor
 * 
 * File Created at 2011-10-18
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.dubbo.monitor.filter.support.dubbo;

import junit.framework.Assert;

import org.junit.Test;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.monitor.MonitorService;
import com.alibaba.dubbo.monitor.support.dubbo.DubboMonitor;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * DubboMonitorTest
 * 
 * @author william.liangf
 */
public class DubboMonitorTest {
    
    private volatile URL lastStatistics;
    
    private final Invoker<MonitorService> monitorInvoker = new Invoker<MonitorService>() {
        public Class<MonitorService> getInterface() {
            return MonitorService.class;
        }
        public URL getUrl() {
            return URL.valueOf("dubbo://127.0.0.1:7070?interval=1");
        }
        public boolean isAvailable() {
            return false;
        }
        public Result invoke(Invocation invocation) throws RpcException {
            return null;
        }
        public void destroy() {
        }
    };
    
    private final MonitorService monitorService = new MonitorService() {

        public void count(URL statistics) {
            DubboMonitorTest.this.lastStatistics = statistics;
        }
        
    };
    
    @Test
    public void testCount() throws Exception {
        DubboMonitor monitor = new DubboMonitor(monitorInvoker, monitorService);
        URL statistics = new URL("dubbo", "10.20.153.10", 0)
            .addParameter(MonitorService.APPLICATION, "morgan")
            .addParameter(MonitorService.INTERFACE, "MemberService")
            .addParameter(MonitorService.METHOD, "findPerson")
            .addParameter(MonitorService.CLIENT, "10.20.153.11")
            .addParameter(MonitorService.SUCCESS, 1)
            .addParameter(MonitorService.FAILURE, 0)
            .addParameter(MonitorService.ELAPSED, 3)
            .addParameter(MonitorService.MAX_ELAPSED, 3)
            .addParameter(MonitorService.CONCURRENT, 1)
            .addParameter(MonitorService.MAX_CONCURRENT, 1);
        monitor.count(statistics);
        while (lastStatistics == null) {
            Thread.sleep(10);
        }
        Assert.assertEquals(statistics, lastStatistics);
        monitor.destroy();
    }

}

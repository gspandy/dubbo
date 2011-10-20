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
package com.alibaba.dubbo.monitor.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import junit.framework.Assert;

import org.junit.Test;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.monitor.Monitor;
import com.alibaba.dubbo.monitor.MonitorFactory;
import com.alibaba.dubbo.monitor.MonitorService;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;

/**
 * MonitorFilterTest
 * 
 * @author william.liangf
 */
public class MonitorFilterTest {

    private volatile URL lastStatistics;
    
    private volatile Invocation lastInvocation;
    
    private final Invoker<MonitorService> serviceInvoker = new Invoker<MonitorService>() {
        public Class<MonitorService> getInterface() {
            return MonitorService.class;
        }
        public URL getUrl() {
            try {
                return URL.valueOf("dubbo://" + NetUtils.getLocalHost() + ":20880?" + Constants.APPLICATION_KEY + "=abc&" + Constants.MONITOR_KEY + "=" + URLEncoder.encode("dubbo://" + NetUtils.getLocalHost() + ":7070", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        public boolean isAvailable() {
            return false;
        }
        public Result invoke(Invocation invocation) throws RpcException {
            lastInvocation = invocation;
            return null;
        }
        public void destroy() {
        }
    };
    
    private MonitorFactory monitorFactory = new MonitorFactory() {
        public Monitor getMonitor(final URL url) {
            return new Monitor() {
                public URL getUrl() {
                    return url;
                }
                public boolean isAvailable() {
                    return true;
                }
                public void destroy() {
                }
                public void count(URL statistics) {
                    MonitorFilterTest.this.lastStatistics = statistics;
                }
            };
        }
    };
    
    @Test
    public void testFilter() throws Exception {
        MonitorFilter monitorFilter = new MonitorFilter();
        monitorFilter.setMonitorFactory(monitorFactory);
        Invocation invocation = new RpcInvocation("aaa", new Class<?>[0], new Object[0]);
        RpcContext.getContext().setRemoteAddress(NetUtils.getLocalHost(), 20880).setLocalAddress(NetUtils.getLocalHost(), 2345);
        monitorFilter.invoke(serviceInvoker, invocation);
        while (lastStatistics == null) {
            Thread.sleep(10);
        }
        Assert.assertEquals("abc", lastStatistics.getParameter(MonitorService.APPLICATION));
        Assert.assertEquals(MonitorService.class.getName(), lastStatistics.getParameter(MonitorService.INTERFACE));
        Assert.assertEquals("aaa", lastStatistics.getParameter(MonitorService.METHOD));
        Assert.assertEquals(NetUtils.getLocalHost(), lastStatistics.getHost());
        Assert.assertEquals(NetUtils.getLocalHost() + ":20880", lastStatistics.getParameter(MonitorService.SERVER));
        Assert.assertEquals(null, lastStatistics.getParameter(MonitorService.CLIENT));
        Assert.assertEquals(1, lastStatistics.getIntParameter(MonitorService.SUCCESS));
        Assert.assertEquals(0, lastStatistics.getIntParameter(MonitorService.FAILURE));
        Assert.assertEquals(1, lastStatistics.getIntParameter(MonitorService.CONCURRENT));
        Assert.assertEquals(invocation, lastInvocation);
    }

}

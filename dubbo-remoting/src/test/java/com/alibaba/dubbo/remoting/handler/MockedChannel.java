/**
 * Project: dubbo-remoting-2.0.7-SNAPSHOT
 * 
 * File Created at 2011-10-14
 * $Id: MockedChannel.java 118480 2011-10-19 10:57:27Z chao.liuc $
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
package com.alibaba.dubbo.remoting.handler;

import java.net.InetSocketAddress;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;

/**
 * @author chao.liuc
 *
 */
public class MockedChannel implements Channel {
    private boolean isClosed ; 
    private URL url; 
    private ChannelHandler handler ;
    
    public MockedChannel() {
        super();
    }


    public URL getUrl() {
        return url;
    }

    public ChannelHandler getChannelHandler() {
        
        return this.handler;
    }

    public InetSocketAddress getLocalAddress() {
        
        return null;
    }

    public void send(Object message) throws RemotingException {
    }

    public void send(Object message, boolean sent) throws RemotingException {
        this.send(message);
    }

    public void close() {
        isClosed = true;
    }

    public void close(int timeout) {
        this.close();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    public boolean isConnected() {
        
        return false;
    }

    public boolean hasAttribute(String key) {
        
        return false;
    }

    public Object getAttribute(String key) {
        
        return null;
    }

    public void setAttribute(String key, Object value) {
        
    }

    public void removeAttribute(String key) {
        
    }
    
}

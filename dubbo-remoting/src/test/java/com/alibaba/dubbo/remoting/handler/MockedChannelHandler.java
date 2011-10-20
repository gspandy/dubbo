/**
 * Project: dubbo-remoting-2.0.7-SNAPSHOT
 * 
 * File Created at 2011-10-14
 * $Id: MockedChannelHandler.java 117434 2011-10-14 11:10:24Z chao.liuc $
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

import java.util.Collections;
import java.util.Set;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;

/**
 * @author chao.liuc
 *
 */
public class MockedChannelHandler implements ChannelHandler {
//    ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
    ConcurrentHashSet<Channel> channels = new ConcurrentHashSet<Channel>();

    public void connected(Channel channel) throws RemotingException {
        channels.add(channel);
    }

    public void disconnected(Channel channel) throws RemotingException {
        channels.remove(channel);
    }

    public void sent(Channel channel, Object message) throws RemotingException {
        channel.send(message);
    }

    public void received(Channel channel, Object message) throws RemotingException {
        //echo 
        channel.send(message);
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
        throw new RemotingException(channel, exception);
        
    }
    public Set<Channel> getChannels(){
        return Collections.unmodifiableSet(channels);
    }
}

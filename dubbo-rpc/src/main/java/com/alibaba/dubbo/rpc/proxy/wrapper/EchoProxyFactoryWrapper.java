/**
 * Project: dubbo-rpc
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
package com.alibaba.dubbo.rpc.proxy.wrapper;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.proxy.ProxyFactory;
import com.alibaba.dubbo.rpc.service.EchoService;

/**
 * EchoProxyFactoryWrapper
 * 
 * @author william.liangf
 */
public class EchoProxyFactoryWrapper implements ProxyFactory {
    
    private final ProxyFactory proxyFactory;
    
    public EchoProxyFactoryWrapper(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }
    
    private Class<?>[] appendEchoService(Class<?>[] types) {
        if (types == null || types.length == 0) {
            return new Class<?>[] { EchoService.class };
        } else {
            Class<?>[] clses = new Class<?>[types.length + 1];
            System.arraycopy(types, 0, clses, 0, types.length);
            clses[types.length] = EchoService.class;
            return clses;
        }
    }

    public <T> T getProxy(Invoker<T> invoker, Class<?>... types) throws RpcException {
        return proxyFactory.getProxy(invoker, appendEchoService(types));
    }
    
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return proxyFactory.getInvoker(proxy, type, url);
    }
    
}

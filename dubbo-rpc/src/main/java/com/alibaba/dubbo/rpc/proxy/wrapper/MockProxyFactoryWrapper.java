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

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.proxy.ProxyFactory;
import com.alibaba.dubbo.rpc.service.GenericService;

/**
 * MockProxyFactoryWrapper
 * 
 * @author william.liangf
 */
public class MockProxyFactoryWrapper implements ProxyFactory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MockProxyFactoryWrapper.class);
    
    private final ProxyFactory proxyFactory;
    
    public MockProxyFactoryWrapper(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }
    
    @SuppressWarnings({ "unchecked"})
    public <T> T getProxy(Invoker<T> invoker, Class<?>... types) throws RpcException {
        String mock = invoker.getUrl().getParameter(Constants.MOCK_KEY);
        if (ConfigUtils.isNotEmpty(mock) && GenericService.class != invoker.getInterface()) {
            Class<?> serviceType = invoker.getInterface();
            if (ConfigUtils.isDefault(mock)) {
                mock = serviceType.getName() + "Mock";
            }
            try {
                Class<?> mockClass = ReflectUtils.forName(mock);
                if (! serviceType.isAssignableFrom(mockClass)) {
                    throw new IllegalArgumentException("The mock implemention class " + mockClass.getName() + " not implement interface " + serviceType.getName());
                }
                try {
                    T mockObject = (T) mockClass.newInstance();
                    invoker = new MockInvoker<T>(invoker, proxyFactory.getInvoker(mockObject, invoker.getInterface(), invoker.getUrl()));
                } catch (InstantiationException e) {
                    throw new IllegalStateException("No such empty constructor \"public " + mockClass.getSimpleName() + "()\" in mock implemention class " + mockClass.getName(), e);
                }
            } catch (Throwable t) {
                LOGGER.error("Failed to create mock implemention class " + mock + " in consumer " + NetUtils.getLocalHost() + " use dubbo version " + Version.getVersion() + ", cause: " + t.getMessage(), t);
                // ignore
            }
        }
        return proxyFactory.getProxy(invoker, types);
    }
    
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return proxyFactory.getInvoker(proxy, type, url);
    }
    
}

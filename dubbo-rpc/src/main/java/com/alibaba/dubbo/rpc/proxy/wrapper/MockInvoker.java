/**
 * Project: dubbo-rpc
 * 
 * File Created at 2011-10-19
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
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * MockInvoker
 * 
 * @author william.liangf
 */
public class MockInvoker<T> implements Invoker<T> {
    
    private final Invoker<T> invoker;
    
    private final Invoker<T> mockInvoker;

    public MockInvoker(Invoker<T> invoker, Invoker<T> mockInvoker) {
        this.invoker = invoker;
        this.mockInvoker = mockInvoker;
    }

    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    public URL getUrl() {
        return invoker.getUrl();
    }

    public boolean isAvailable() {
        return invoker.isAvailable() && mockInvoker.isAvailable();
    }

    public Result invoke(Invocation invocation) throws RpcException {
        try {
            return invoker.invoke(invocation);
        } catch (RpcException e) {
            return mockInvoker.invoke(invocation);
        }
    }

    public void destroy() {
        try {
            invoker.destroy();
        } finally {
            mockInvoker.destroy();
        }
    }

}

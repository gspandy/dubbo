/**
 * Project: dubbo-config
 * 
 * File Created at 2011-10-19
 * $Id: MockProtocol.java 118511 2011-10-19 12:45:01Z haomin.liuhm $
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
package com.alibaba.dubbo.config.test;

import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * 
 * @author haomin.liuhm
 *
 */
@Extension("mockprotocol")
public class MockProtocol implements Protocol {

    /* (non-Javadoc)
     * @see com.alibaba.dubbo.rpc.Protocol#getDefaultPort()
     */
    public int getDefaultPort() {

        return 0;
    }

    /* (non-Javadoc)
     * @see com.alibaba.dubbo.rpc.Protocol#export(com.alibaba.dubbo.rpc.Invoker)
     */
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return null;
    }

    /* (non-Javadoc)
     * @see com.alibaba.dubbo.rpc.Protocol#refer(java.lang.Class, com.alibaba.dubbo.common.URL)
     */
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        
        final URL u = url;
        
        return new Invoker<T>(){
            public Class<T> getInterface(){
                return null;
            }
            public URL getUrl(){
                return u;
            }
            public boolean isAvailable(){
                return true;
            }
            public Result invoke(Invocation invocation) throws RpcException{
                return null;
            }
            
            public void destroy(){
                
            }            
        };
    }

    /* (non-Javadoc)
     * @see com.alibaba.dubbo.rpc.Protocol#destroy()
     */
    public void destroy() {
        
    }

}

/*
 * Copyright 1999-2101 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.injvm;

import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.support.AbstractProtocol;

/**
 * InjvmProtocol
 * 
 * @author qian.lei
 * @author william.liangf
 */
@Extension(InjvmProtocol.NAME)
public class InjvmProtocol extends AbstractProtocol implements Protocol {
    
    public static final String NAME = "injvm";

    public static final int DEFAULT_PORT = 1;

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return new InjvmExporter<T>(invoker, serviceKey(invoker.getUrl()), exporterMap);
    }

    public <T> Invoker<T> refer(Class<T> serviceType, URL url) throws RpcException {
        return new InjvmInvoker<T>(serviceType, url, serviceKey(url), exporterMap);
    }
    
}
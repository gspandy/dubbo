/*
 * Copyright 1999-2011 Alibaba Group.
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
package com.alibaba.dubbo.monitor.filter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.monitor.Monitor;
import com.alibaba.dubbo.monitor.MonitorFactory;
import com.alibaba.dubbo.monitor.MonitorService;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
 
/**
 * MonitorFilter. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@Extension("monitor")
public class MonitorFilter implements Filter {
    
    private final ConcurrentMap<String, AtomicInteger> concurrents = new ConcurrentHashMap<String, AtomicInteger>();
    
    private MonitorFactory monitorFactory;
    
    public void setMonitorFactory(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
    }
    
    // 调用过程拦截
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (invoker.getUrl().hasParameter(Constants.MONITOR_KEY)) {
            RpcContext context = RpcContext.getContext(); // 提供方必须在invoke()之前获取context信息
            long start = System.currentTimeMillis(); // 记录起始时间戮
            getConcurrent(invoker, invocation).incrementAndGet(); // 并发计数
            try {
                Result result = invoker.invoke(invocation); // 让调用链往下执行
                collect(invoker, invocation, context, start, false);
                return result;
            } catch (RpcException e) {
                collect(invoker, invocation, context, start, true);
                throw e;
            } finally {
                getConcurrent(invoker, invocation).decrementAndGet(); // 并发计数
            }
        } else {
            return invoker.invoke(invocation);
        }
    }
    
    // 信息采集
    private void collect(Invoker<?> invoker, Invocation invocation, RpcContext context, long start, boolean error) {
        // ---- 服务信息获取 ----
        long elapsed = System.currentTimeMillis() - start; // 计算调用耗时
        int concurrent = getConcurrent(invoker, invocation).get(); // 当前并发数
        String application = invoker.getUrl().getParameter(Constants.APPLICATION_KEY);
        String service = invoker.getInterface().getName(); // 获取服务名称
        String method = invocation.getMethodName(); // 获取方法名
        URL url = URL.valueOf(invoker.getUrl().getParameterAndDecoded(Constants.MONITOR_KEY));
        Monitor monitor = monitorFactory.getMonitor(url);
        // ---- 服务提供方监控 ----
        String server = context.getLocalAddressString(); // 本地提供方地址
        if (invoker.getUrl().getAddress().equals(server)) {
            monitor.count(new URL(invoker.getUrl().getProtocol(), NetUtils.getLocalHost(), context.getLocalPort())
                    .addParameters(MonitorService.APPLICATION, application, 
                            MonitorService.INTERFACE, service, 
                            MonitorService.METHOD, method,
                            MonitorService.CLIENT, context.getRemoteHost(),
                            error ? MonitorService.FAILURE : MonitorService.SUCCESS, String.valueOf(1),
                            MonitorService.ELAPSED, String.valueOf(elapsed),
                            MonitorService.CONCURRENT, String.valueOf(concurrent)));
        }
        // ---- 服务消费方监控 ----
        context = RpcContext.getContext(); // 消费方必须在invoke()之后获取context信息
        server = context.getRemoteAddressString(); // 远程提供方地址
        if (invoker.getUrl().getAddress().equals(server)) {
            monitor.count(new URL(invoker.getUrl().getProtocol(), NetUtils.getLocalHost(), 0)
                    .addParameters(MonitorService.APPLICATION, application, 
                            MonitorService.INTERFACE, service, 
                            MonitorService.METHOD, method,
                            MonitorService.SERVER, server,
                            error ? MonitorService.FAILURE : MonitorService.SUCCESS, String.valueOf(1),
                            MonitorService.ELAPSED, String.valueOf(elapsed),
                            MonitorService.CONCURRENT, String.valueOf(concurrent)));
        }
    }
    
    // 获取并发计数器
    private AtomicInteger getConcurrent(Invoker<?> invoker, Invocation invocation) {
        String key = invoker.getInterface().getName() + "." + invocation.getMethodName();
        AtomicInteger concurrent = concurrents.get(key);
        if (concurrent == null) {
            concurrents.putIfAbsent(key, new AtomicInteger());
            concurrent = concurrents.get(key);
        }
        return concurrent;
    }

}
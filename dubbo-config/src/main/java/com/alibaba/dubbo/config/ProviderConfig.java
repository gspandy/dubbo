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
package com.alibaba.dubbo.config;

import java.util.Arrays;

import com.alibaba.dubbo.common.status.StatusChecker;
import com.alibaba.dubbo.common.threadpool.ThreadPool;
import com.alibaba.dubbo.remoting.Transporter;
import com.alibaba.dubbo.remoting.exchange.Exchanger;
import com.alibaba.dubbo.remoting.telnet.TelnetHandler;

/**
 * ProviderConfig
 * 
 * @see com.alibaba.dubbo.config.ProtocolConfig
 * @see com.alibaba.dubbo.config.ServiceConfig
 * @author william.liangf
 */
public class ProviderConfig extends AbstractServiceConfig {

    private static final long   serialVersionUID = 6913423882496634749L;

    // ======== 协议缺省值，当协议属性未设置时使用该缺省值替代  ========

    // 服务IP地址(多网卡时使用)
    private String              host;

    // 服务端口
    private Integer             port;

    // 上下
    private String              contextpath;

    // 线程池类型
    private String              threadpool;
    
    // 线程池大小(固定大小)
    private Integer             threads;

    // IO线程池大小(固定大小)
    private Integer             iothreads;
    
    // 线程池队列大小
    private Integer             queues;

    // 最大接收连接数
    private Integer             accepts;
    
    // 协议编码
    private String              codec;
    
    // 序列化方式
    private String              serialization;

    // 字符集
    private String              charset;
    
    // 最大请求数据长度
    private Integer             payload;

    // 缓存区大小
    private Integer             buffer;
    
    // 网络传输方式
    private String              transporter;
    
    // 信息交换方式
    private String              exchanger;
    
    // 服务器端实现
    private String              server;
    
    // 客户端实现
    private String              client;
    
    // 支持的telnet命令，多个命令用逗号分隔
    private String              telnet;

    // status检查
    private String              status;
    
    // 停止时等候时间
    private Integer             wait;
    
    // 是否为缺省
    private Boolean             isDefault;
    
    @Deprecated
    public void setProtocol(String protocol) {
        this.protocols = Arrays.asList(new ProtocolConfig[] {new ProtocolConfig(protocol)});
    }

    @Parameter(excluded = true)
    public Boolean isDefault() {
        return isDefault;
    }

    @Deprecated
    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    @Parameter(excluded = true)
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    @Parameter(excluded = true)
    public Integer getPort() {
        return port;
    }
    
    @Deprecated
    public void setPort(Integer port) {
        this.port = port;
    }

    @Deprecated
    @Parameter(excluded = true)
    public String getPath() {
        return getContextpath();
    }

    @Deprecated
    public void setPath(String path) {
        setContextpath(path);
    }

    @Parameter(excluded = true)
    public String getContextpath() {
        return contextpath;
    }

    public void setContextpath(String contextpath) {
        checkPathName("contextpath", contextpath);
        this.contextpath = contextpath;
    }

    public String getThreadpool() {
        return threadpool;
    }

    public void setThreadpool(String threadpool) {
        checkExtension(ThreadPool.class, "threadpool", threadpool);
        this.threadpool = threadpool;
    }
    
    public Integer getThreads() {
        return threads;
    }
    
    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getIothreads() {
        return iothreads;
    }

    public void setIothreads(Integer iothreads) {
        this.iothreads = iothreads;
    }

    public Integer getQueues() {
        return queues;
    }
    
    public void setQueues(Integer queues) {
        this.queues = queues;
    }
    
    public Integer getAccepts() {
        return accepts;
    }
    
    public void setAccepts(Integer accepts) {
        this.accepts = accepts;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getSerialization() {
        return serialization;
    }
    
    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getPayload() {
        return payload;
    }
    
    public void setPayload(Integer payload) {
        this.payload = payload;
    }

    public Integer getBuffer() {
        return buffer;
    }

    public void setBuffer(Integer buffer) {
        this.buffer = buffer;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
    
    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTelnet() {
        return telnet;
    }
    
    public void setTelnet(String telnet) {
        checkMultiExtension(TelnetHandler.class, "telnet", telnet);
        this.telnet = telnet;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        checkMultiExtension(StatusChecker.class, "status", status);
        this.status = status;
    }

    @Parameter(key = "default.cluster")
    @Override
    public String getCluster() {
        return super.getCluster();
    }

    @Parameter(key = "default.connections")
    @Override
    public Integer getConnections() {
        return super.getConnections();
    }

    @Parameter(key = "default.timeout")
    @Override
    public Integer getTimeout() {
        return super.getTimeout();
    }

    @Parameter(key = "default.retries")
    @Override
    public Integer getRetries() {
        return super.getRetries();
    }

    @Parameter(key = "default.loadbalance")
    @Override
    public String getLoadbalance() {
        return super.getLoadbalance();
    }

    @Parameter(key = "default.async")
    @Override
    public Boolean isAsync() {
        return super.isAsync();
    }

    @Parameter(key = "default.actives")
    @Override
    public Integer getActives() {
        return super.getActives();
    }
    
    public String getTransporter() {
        return transporter;
    }
    
    public void setTransporter(String transporter) {
        checkExtension(Transporter.class, "transporter", transporter);
        this.transporter = transporter;
    }
    
    public String getExchanger() {
        return exchanger;
    }
    
    public void setExchanger(String exchanger) {
        checkExtension(Exchanger.class, "exchanger", exchanger);
        this.exchanger = exchanger;
    }
    
    public Integer getWait() {
        return wait;
    }
    
    public void setWait(Integer wait) {
        this.wait = wait;
    }

}
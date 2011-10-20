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
package com.alibaba.dubbo.config;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.InvokerListener;
import com.alibaba.dubbo.rpc.RpcConstants;


/**
 * AbstractConsumerConfig
 * 
 * @see com.alibaba.dubbo.config.ReferenceConfig
 * @author william.liangf
 */
public abstract class AbstractConsumerConfig extends AbstractReferenceConfig {

    private static final long serialVersionUID = -2786526984373031126L;

    // ======== 引用缺省值，当引用属性未设置时使用该缺省值替代  ========
    
    // 检查服务提供者是否存在
    protected Boolean             check;

    // 是否使用泛接口
    protected Boolean             generic;
    
    // 优先从JVM内获取引用实例
    protected Boolean             injvm;
    
    // lazy create connection
    protected Boolean             lazy;
    
    //stub是否支持event事件. //TODO slove merge problem 
    protected Boolean             stubevent ;//= RpcConstants.DEFAULT_STUB_EVENT;
    
    public Boolean isCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    @Parameter(excluded = true)
    public Boolean isGeneric() {
        return generic;
    }

    public void setGeneric(Boolean generic) {
        this.generic = generic;
    }
    
    public Boolean isInjvm() {
        return injvm;
    }
    
    public void setInjvm(Boolean injvm) {
        this.injvm = injvm;
    }

    @Override
    public void setTimeout(Integer timeout) {
        super.setTimeout(timeout);
        if (timeout != null && timeout > 0) {
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(timeout));
        }
    }

    @Parameter(key = Constants.REFERENCE_FILTER_KEY)
    public String getFilter() {
        return super.getFilter();
    }

    @Parameter(key = Constants.INVOKER_LISTENER_KEY)
    public String getListener() {
        return super.getListener();
    }

    @Override
    public void setListener(String listener) {
        checkMultiExtension(InvokerListener.class, "listener", listener);
        super.setListener(listener);
    }

    @Parameter(key = RpcConstants.LAZY_CONNECT_KEY)
    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    @Override
    public void setOnconnect(String onconnect) {
        if (onconnect != null && onconnect.length() >0){
            this.stubevent = true;
        }
        super.setOnconnect(onconnect);
    }

    @Override
    public void setOndisconnect(String ondisconnect) {
        if (ondisconnect != null && ondisconnect.length() >0){
            this.stubevent = true;
        }
        super.setOndisconnect(ondisconnect);
    }

    @Parameter(key = RpcConstants.STUB_EVENT_KEY)
    public Boolean getStubevent() {
        return stubevent;
    }
}
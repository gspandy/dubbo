/**
 * Project: dubbo-config
 * 
 * File Created at 2011-10-19
 * $Id: MockRegistry.java 118511 2011-10-19 12:45:01Z haomin.liuhm $
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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.Registry;

/**
 * TODO Comment of MockRegistry
 * @author haomin.liuhm
 *
 */
public class MockRegistry implements Registry {

    static URL subscribedUrl = new URL("null", "0.0.0.0", 0);
    
    public static URL getSubscribedUrl(){
        return subscribedUrl;
    }
    
    /* 
     * @see com.alibaba.dubbo.common.Node#getUrl()
     */
    public URL getUrl() {
        return null;
    }

    /* 
     * @see com.alibaba.dubbo.common.Node#isAvailable()
     */
    public boolean isAvailable() {
        return true;
    }

    /* 
     * @see com.alibaba.dubbo.common.Node#destroy()
     */
    public void destroy() {
        
    }

    /* 
     * @see com.alibaba.dubbo.registry.RegistryService#register(com.alibaba.dubbo.common.URL)
     */
    public void register(URL url) {
        
    }

    /* 
     * @see com.alibaba.dubbo.registry.RegistryService#unregister(com.alibaba.dubbo.common.URL)
     */
    public void unregister(URL url) {
        
    }

    /* 
     * @see com.alibaba.dubbo.registry.RegistryService#subscribe(com.alibaba.dubbo.common.URL, com.alibaba.dubbo.registry.NotifyListener)
     */
    public void subscribe(URL url, NotifyListener listener) {
        this.subscribedUrl = url;
        List<URL> urls = new ArrayList<URL>();
        
        urls.add(url.setProtocol("mockprotocol")
                    .addParameter(Constants.METHODS_KEY, "sayHello"));
        
        listener.notify(urls);
    }

    /* 
     * @see com.alibaba.dubbo.registry.RegistryService#unsubscribe(com.alibaba.dubbo.common.URL, com.alibaba.dubbo.registry.NotifyListener)
     */
    public void unsubscribe(URL url, NotifyListener listener) {
        
    }

    /* 
     * @see com.alibaba.dubbo.registry.RegistryService#lookup(com.alibaba.dubbo.common.URL)
     */
    public List<URL> lookup(URL url) {
        return null;
    }

}

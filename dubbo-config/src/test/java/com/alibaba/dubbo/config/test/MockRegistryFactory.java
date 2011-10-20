/**
 * Project: dubbo-config
 * 
 * File Created at 2011-10-19
 * $Id: MockRegistryFactory.java 118511 2011-10-19 12:45:01Z haomin.liuhm $
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
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.RegistryFactory;

/**
 * TODO Comment of MockRegistryFactory
 * @author haomin.liuhm
 *
 */
@Extension("mockregistry")
public class MockRegistryFactory implements RegistryFactory {

    /* 
     * @see com.alibaba.dubbo.registry.RegistryFactory#getRegistry(com.alibaba.dubbo.common.URL)
     */
    public Registry getRegistry(URL url) {
        
        return new MockRegistry();
    }

}


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
package com.alibaba.dubbo.container.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.container.Container;

/**
 * Standalone
 * 
 * @author william.liangf
 */
@Extension("spring")
public class SpringContainer implements Container {

    private static final Logger LOGGER                = LoggerFactory.getLogger(SpringContainer.class);

    private static final String SPRING_CONFIG_KEY     = "spring.config";
    
    private static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";

    private ClassPathXmlApplicationContext context;

    public ApplicationContext getApplicationContext() {
        return context;
    }
    
    public void start() {
        String configPath = System.getProperty(SPRING_CONFIG_KEY);
        if (configPath == null || configPath.length() == 0) {
            configPath = DEFAULT_SPRING_CONFIG;
        }
        context = new ClassPathXmlApplicationContext(configPath.split("[,\\s]+"));
        context.start();
        LOGGER.info("Dubbo spring container started!");
    }

    public void stop() {
        try {
            context.stop();
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
        try {
            context.close();
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("Dubbo spring container stopped!");
    }

}
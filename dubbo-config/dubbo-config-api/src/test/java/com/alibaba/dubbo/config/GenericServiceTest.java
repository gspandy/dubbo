/*
 * Copyright 1999-2012 Alibaba Group.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.dubbo.config.api.DemoException;
import com.alibaba.dubbo.config.api.DemoService;
import com.alibaba.dubbo.config.api.User;
import com.alibaba.dubbo.config.provider.impl.DemoServiceImpl;
import com.alibaba.dubbo.rpc.service.GenericException;
import com.alibaba.dubbo.rpc.service.GenericService;

/**
 * GenericServiceTest
 * 
 * @author william.liangf
 */
public class GenericServiceTest {
    
    @Test
    public void testGenericServiceException() {
        ServiceConfig<GenericService> service = new ServiceConfig<GenericService>();
        service.setApplication(new ApplicationConfig("generic-provider"));
        service.setRegistry(new RegistryConfig("N/A"));
        service.setProtocol(new ProtocolConfig("dubbo", 29581));
        service.setInterface(DemoService.class.getName());
        service.setRef(new GenericService() {
            public Object $invoke(String method, String[] parameterTypes, Object[] args)
                    throws GenericException {
                if ("sayName".equals(method)) {
                    return "Generic " + args[0];
                }
                if ("throwDemoException".equals(method)) {
                    throw new GenericException(DemoException.class.getName(), "Generic");
                }
                if ("getUsers".equals(method)) {
                    return args[0];
                }
                return null;
            }
        });
        service.export();
        try {
            ReferenceConfig<DemoService> reference = new ReferenceConfig<DemoService>();
            reference.setApplication(new ApplicationConfig("generic-consumer"));
            reference.setInterface(DemoService.class);
            reference.setUrl("dubbo://127.0.0.1:29581?generic=true");
            DemoService demoService = reference.get();
            try {
                // say name
                Assert.assertEquals("Generic Haha", demoService.sayName("Haha"));
                // get users
                List<User> users = new ArrayList<User>();
                users.add(new User("Aaa"));
                users = demoService.getUsers(users);
                Assert.assertEquals("Aaa", users.get(0).getName());
                // throw demo exception
                try {
                    demoService.throwDemoException();
                    Assert.fail();
                } catch (DemoException e) {
                    Assert.assertEquals("Generic", e.getMessage());
                }
            } finally {
                reference.destroy();
            }
        } finally {
            service.unexport();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGenericReferenceException() {
        ServiceConfig<DemoService> service = new ServiceConfig<DemoService>();
        service.setApplication(new ApplicationConfig("generic-provider"));
        service.setRegistry(new RegistryConfig("N/A"));
        service.setProtocol(new ProtocolConfig("dubbo", 29581));
        service.setInterface(DemoService.class.getName());
        service.setRef(new DemoServiceImpl());
        service.export();
        try {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
            reference.setApplication(new ApplicationConfig("generic-consumer"));
            reference.setInterface(DemoService.class);
            reference.setUrl("dubbo://127.0.0.1:29581?scope=remote");
            reference.setGeneric(true);
            GenericService genericService = reference.get();
            try {
                List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
                Map<String, Object> user = new HashMap<String, Object>();
                user.put("class", "com.alibaba.dubbo.config.api.User");
                user.put("name", "actual.provider");
                users.add(user);
                users = (List<Map<String, Object>>) genericService.$invoke("getUsers", new String[] {List.class.getName()}, new Object[] {users});
                Assert.assertEquals(1, users.size());
                Assert.assertEquals("actual.provider", users.get(0).get("name"));
            } finally {
                reference.destroy();
            }
        } finally {
            service.unexport();
        }
    }

}

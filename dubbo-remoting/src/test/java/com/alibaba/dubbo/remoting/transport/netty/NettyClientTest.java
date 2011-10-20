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
package com.alibaba.dubbo.remoting.transport.netty;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.Server;
import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.Exchangers;

/**
 * User: heyman
 * Date: 5/3/11
 * Time: 5:47 PM
 */
public class NettyClientTest {
    static Server server;


    @BeforeClass
    public static void setUp() throws Exception {
        /*int port = (int) (1000 * Math.random() + 10000);
        //System.out.println(port);*/
        server = Exchangers.bind(URL.valueOf("exchange://localhost:10001?server=netty"), new TelnetServerHandler());
    }

    @Test
    public void testClientClose() throws Exception {
        List<ExchangeChannel> clients = new ArrayList<ExchangeChannel>(100);
        for (int i = 0; i < 100; i++) {
            ExchangeChannel client = Exchangers.connect(URL.valueOf("exchange://localhost:10001?client=netty"));
            Thread.sleep(5);
            clients.add(client);
        }
        for (ExchangeChannel client : clients){
            client.close();
        }
        Thread.sleep(1000);
    }

    @Test
    public void testServerClose() throws Exception {
        for (int i = 0; i < 100; i++) {
            Server aServer = Exchangers.bind(URL.valueOf("exchange://localhost:" + (5000 + i) + "?client=netty"), new TelnetServerHandler());
            System.out.println(i);
            aServer.close();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        try {
            if (server != null)
                server.close();
        } finally {

        }
    }
}
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
package com.alibaba.dubbo.rpc.cluster.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.dubbo.common.ExtensionLoader;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcConstants;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Directory;
import com.alibaba.dubbo.rpc.cluster.LoadBalance;
import com.alibaba.dubbo.rpc.cluster.loadbalance.LeastActiveLoadBalance;
import com.alibaba.dubbo.rpc.cluster.loadbalance.RandomLoadBalance;
import com.alibaba.dubbo.rpc.cluster.loadbalance.RoundRobinLoadBalance;

/**
 * AbstractClusterInvokerTest
 * @author chao.liuc
 *
 */
@SuppressWarnings("rawtypes")
public class AbstractClusterInvokerTest {
    List<Invoker<AbstractClusterInvokerTest>> invokers = new ArrayList<Invoker<AbstractClusterInvokerTest>>();
    List<Invoker<AbstractClusterInvokerTest>> selectedInvokers = new ArrayList<Invoker<AbstractClusterInvokerTest>>();
    AbstractClusterInvoker<AbstractClusterInvokerTest> cluster;
    AbstractClusterInvoker<AbstractClusterInvokerTest> cluster_nocheck;
    Directory<AbstractClusterInvokerTest> dic ;
    Invocation invocation;
    URL url = URL.valueOf("registry://localhost:9090");
    
    Invoker<AbstractClusterInvokerTest> invoker1 ;
    Invoker<AbstractClusterInvokerTest> invoker2 ;
    Invoker<AbstractClusterInvokerTest> invoker3 ;
    Invoker<AbstractClusterInvokerTest> invoker4 ;
    Invoker<AbstractClusterInvokerTest> invoker5 ;
    

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }
    @SuppressWarnings({ "unchecked" })
    @Before
    public void setUp() throws Exception {
        dic = EasyMock.createMock(Directory.class);
        
        invoker1 = EasyMock.createMock(Invoker.class);
        invoker2 = EasyMock.createMock(Invoker.class);
        invoker3 = EasyMock.createMock(Invoker.class);
        invoker4 = EasyMock.createMock(Invoker.class);
        invoker5 = EasyMock.createMock(Invoker.class);
        
        URL turl = URL.valueOf("test://test:11/test");
        
        EasyMock.expect(invoker1.isAvailable()).andReturn(false).anyTimes();
        EasyMock.expect(invoker1.getInterface()).andReturn(AbstractClusterInvokerTest.class).anyTimes();
        EasyMock.expect(invoker1.getUrl()).andReturn(turl.addParameter("name", "invoker1")).anyTimes();
        
        EasyMock.expect(invoker2.isAvailable()).andReturn(true).anyTimes();
        EasyMock.expect(invoker2.getInterface()).andReturn(AbstractClusterInvokerTest.class).anyTimes();
        EasyMock.expect(invoker2.getUrl()).andReturn(turl.addParameter("name", "invoker2")).anyTimes();
        
        EasyMock.expect(invoker3.isAvailable()).andReturn(false).anyTimes();
        EasyMock.expect(invoker3.getInterface()).andReturn(AbstractClusterInvokerTest.class).anyTimes();
        EasyMock.expect(invoker3.getUrl()).andReturn(turl.addParameter("name", "invoker3")).anyTimes();
        
        EasyMock.expect(invoker4.isAvailable()).andReturn(true).anyTimes();
        EasyMock.expect(invoker4.getInterface()).andReturn(AbstractClusterInvokerTest.class).anyTimes();
        EasyMock.expect(invoker4.getUrl()).andReturn(turl.addParameter("name", "invoker4")).anyTimes();
        
        EasyMock.expect(invoker5.isAvailable()).andReturn(false).anyTimes();
        EasyMock.expect(invoker5.getInterface()).andReturn(AbstractClusterInvokerTest.class).anyTimes();
        EasyMock.expect(invoker5.getUrl()).andReturn(turl.addParameter("name", "invoker5")).anyTimes();
        
        EasyMock.expect(dic.getUrl()).andReturn(url).anyTimes();
        
        invocation = EasyMock.createMock(Invocation.class);
        EasyMock.expect(invocation.getMethodName()).andReturn("method1").anyTimes();
        EasyMock.replay(dic,invoker1,invoker2,invoker3,invoker4,invoker5,invocation);
        
        cluster = new AbstractClusterInvoker(dic) {
            @Override
            protected Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
                    throws RpcException {
                return null;
            }
        };
        
        cluster_nocheck = new AbstractClusterInvoker(dic,url.addParameterIfAbsent(RpcConstants.CLUSTER_AVAILABLE_CHECK_KEY, Boolean.FALSE.toString())) {
            @Override
            protected Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
                    throws RpcException {
                return null;
            }
        };
    }
    
    @Test
    public void testSelect_Invokersize0() throws Exception {
        {
            Invoker invoker = cluster.select(null,null,null,null);
            Assert.assertEquals(null, invoker);
        }
        {
            invokers.clear();
            selectedInvokers.clear();
            Invoker invoker = cluster.select(null,null,invokers,null);
            Assert.assertEquals(null, invoker);
        }
    }
    
    @Test
    public void testSelect_Invokersize1() throws Exception {
        invokers.clear();
        invokers.add(invoker1);
        Invoker invoker = cluster.select(null,null,invokers,null);
        Assert.assertEquals(invoker1, invoker);
    }
    
    @Test
    public void testSelect_Invokersize2AndselectNotNull() throws Exception {
        invokers.clear();
        invokers.add(invoker1);
        invokers.add(invoker2);
        {
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            Invoker invoker = cluster.select(null,null,invokers,selectedInvokers);
            Assert.assertEquals(invoker2, invoker);
        }
        {
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            Invoker invoker = cluster.select(null,null,invokers,selectedInvokers);
            Assert.assertEquals(invoker1, invoker);
        }
    }
    
    @Test
    public void testSelect_multiInvokers() throws Exception {
        testSelect_multiInvokers( RoundRobinLoadBalance.NAME);
        testSelect_multiInvokers( LeastActiveLoadBalance.NAME);
        testSelect_multiInvokers( RandomLoadBalance.NAME);
    }
    
    @Test
    public void testCloseAvailablecheck(){
        LoadBalance lb = EasyMock.createMock(LoadBalance.class);
        EasyMock.expect(lb.select(invokers, invocation)).andReturn(invoker1);
        EasyMock.replay(lb);
        initlistsize5();
        
        Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
        Assert.assertEquals(false,sinvoker.isAvailable());
        Assert.assertEquals(invoker1,sinvoker);
        
    }
    
    @Test
    public void testDonotSelectAgainAndNoCheckAvailable(){
        
        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(RoundRobinLoadBalance.NAME);
        initlistsize5();
        {
            //边界测试.
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertSame(invoker1, sinvoker);
        }
        {
            //边界测试.
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertSame(invoker2, sinvoker);
        }
        {
            //边界测试.
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers );
            Assert.assertSame(invoker3, sinvoker);
        }
        {
            //边界测试.
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers );
            Assert.assertSame(invoker5, sinvoker);
        }
        {
            //边界测试.
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster_nocheck.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertTrue(invokers.contains(sinvoker));
        }
        
    }
    
    @Test
    public void testSelectAgainAndCheckAvailable(){
        
        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(RoundRobinLoadBalance.NAME);
        initlistsize5();
        {
            //边界测试.
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertTrue(sinvoker == invoker4 );
        }
        {
            //边界测试.
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker4);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertTrue(sinvoker == invoker2 || sinvoker == invoker4);
        }
        {
            //边界测试.
            for(int i=0;i<100;i++){
                selectedInvokers.clear();
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assert.assertTrue(sinvoker == invoker2 || sinvoker == invoker4);
            }
        }
        {
            //边界测试.
            for(int i=0;i<100;i++){
                selectedInvokers.clear();
                selectedInvokers.add(invoker1);
                selectedInvokers.add(invoker3);
                selectedInvokers.add(invoker5);
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assert.assertTrue(sinvoker == invoker2 || sinvoker == invoker4);
            }
        }
        {
            //边界测试.
            for(int i=0;i<100;i++){
                selectedInvokers.clear();
                selectedInvokers.add(invoker1);
                selectedInvokers.add(invoker3);
                selectedInvokers.add(invoker2);
                selectedInvokers.add(invoker4);
                selectedInvokers.add(invoker5);
                Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
                Assert.assertTrue(sinvoker == invoker2 || sinvoker == invoker4);
            }
        }
    }
    
    
    public void testSelect_multiInvokers(String lbname) throws Exception {
        
        int min=1000,max=5000;
        Double d =  (Math.random()*(max-min+1)+min);
        int runs =  d.intValue();
        Assert.assertTrue(runs>min);
        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(lbname);
        initlistsize5();
        for(int i=0;i<runs;i++){
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertEquals(true,sinvoker.isAvailable());
        }
        for(int i=0;i<runs;i++){
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertEquals(true,sinvoker.isAvailable());
        }
        for(int i=0;i<runs;i++){
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertEquals(true,sinvoker.isAvailable());
        }
        for(int i=0;i<runs;i++){
            selectedInvokers.clear();
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker4);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertEquals(true,sinvoker.isAvailable());
        }
        for(int i=0;i<runs;i++){
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker3);
            selectedInvokers.add(invoker5);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertEquals(true,sinvoker.isAvailable());
        }
        for(int i=0;i<runs;i++){
            selectedInvokers.clear();
            selectedInvokers.add(invoker1);
            selectedInvokers.add(invoker2);
            selectedInvokers.add(invoker3);
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            Assert.assertEquals(true,sinvoker.isAvailable());
        }
    }

    /**
     * 测试均衡.
     */
    @Test
    public void testSelectBalance(){
        
        LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(RoundRobinLoadBalance.NAME);
        initlistsize5();
        
        Map<Invoker,AtomicLong> counter = new ConcurrentHashMap<Invoker,AtomicLong>();
        for(Invoker invoker :invokers){
            counter.put(invoker, new AtomicLong(0));
        }
        int runs = 1000;
        for(int i=0;i<runs;i++){
            selectedInvokers.clear();
            Invoker sinvoker = cluster.select(lb, invocation, invokers, selectedInvokers);
            counter.get(sinvoker).incrementAndGet();
        }
        
        for (Invoker minvoker :counter.keySet() ){
            Long count = counter.get(minvoker).get();
//            System.out.println(count);
            if(minvoker.isAvailable())
                Assert.assertTrue("count should > avg", count>runs/invokers.size());
        }
        
        Assert.assertEquals(runs, counter.get(invoker2).get()+counter.get(invoker4).get());;
        
    }
    
    private void initlistsize5(){
        invokers.clear();
        selectedInvokers.clear();//需要清除，之前的测试中会主动将正确的invoker2放入其中.
        invokers.add(invoker1);
        invokers.add(invoker2);
        invokers.add(invoker3);
        invokers.add(invoker4);
        invokers.add(invoker5);
    }
    @Test
    public void testInvoke() {
//        fail("Not yet implemented");
    }

    @Test
    public void testDoInvoke() {
//        fail("Not yet implemented");
    }

    
}
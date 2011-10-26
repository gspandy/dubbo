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
package com.alibaba.dubbo.rpc.protocol.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.dubbo.rpc.RpcResult;

/**
 * AbstratcRmiInvocationHandler
 * 
 * @author william.liangf
 */
public abstract class AbstractRmiInvocationHandler implements RmiInvocationHandler {

    public RpcResult invoke(RpcInvocation invocation) throws RemoteException, NoSuchMethodException,
                                                     IllegalAccessException, InvocationTargetException {
        return (RpcResult) invoke((Invocation) invocation);
    }

}
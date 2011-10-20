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
package com.alibaba.dubbo.rpc;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;

/**
 * Thread local context. (API, ThreadLocal, ThreadSafe)
 * 
 * @see com.alibaba.dubbo.rpc.filter.ContextFilter
 * @see com.alibaba.dubbo.rpc.support.AbstractExporter#invoke(Invocation, InetSocketAddress)
 * @author qian.lei
 * @author william.liangf
 */
public class RpcContext {
	
	private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
		@Override
		protected RpcContext initialValue() {
			return new RpcContext();
		}
	};

	/**
	 * get context.
	 * 
	 * @return context
	 */
	public static RpcContext getContext() {
	    return LOCAL.get();
	}
	
	/**
	 * remove context.
	 * 
	 * @see com.alibaba.dubbo.rpc.filter.ContextFilter
	 */
	public static void removeContext() {
	    LOCAL.remove();
	}

    private final Map<String, Object> values = new HashMap<String, Object>();
    
    private final Map<String, String> attachments = new HashMap<String, String>();
    
    private Invoker<?> invoker;
    
    private Invocation invocation;
    
	private InetSocketAddress localAddress;

	private InetSocketAddress remoteAddress;
	
	private Future<?> future;
	
	protected RpcContext() {
	}

    /**
     * is server side.
     * 
     * @return server side.
     */
    public boolean isServerSide() {
        return ! isClientSide();
    }

    /**
     * is client side.
     * 
     * @return client side.
     */
    public boolean isClientSide() {
        Invoker<?> invoker = getInvoker();
        if (invoker == null) {
            return false;
        }
        URL url = invoker.getUrl();
        if (url == null) {
            return false;
        }
        InetSocketAddress address = getRemoteAddress();
        if (address == null) {
            return false;
        }
        String host;
        if (address.getAddress() == null) {
            host = address.getHostName();
        } else {
            host = address.getAddress().getHostAddress();
        }
        return url.getPort() == address.getPort() && 
                NetUtils.filterLocalHost(url.getHost()).equals(NetUtils.filterLocalHost(host));
    }
    
    /**
     * set current invoker.
     * 
     * @param invoker
     * @return context
     */
    public RpcContext setInvoker(Invoker<?> invoker) {
        this.invoker = invoker;
        return this;
    }

    /**
     * get current invoker.
     * 
     * @return invoker
     */
    public Invoker<?> getInvoker() {
        return invoker;
    }
    
    /**
     * set invocation.
     * 
     * @param invocation
     * @return context
     */
    public RpcContext setInvocation(Invocation invocation) {
        this.invocation = invocation;
        return this;
    }

    /**
     * get invocation.
     * 
     * @return invocation
     */
    public Invocation getInvocation() {
        return invocation;
    }

    /**
     * set local address.
     * 
     * @param address
     * @return context
     */
	public RpcContext setLocalAddress(InetSocketAddress address) {
	    this.localAddress = address;
	    return this;
	}

	/**
	 * set local address.
	 * 
	 * @param host
	 * @param port
	 * @return context
	 */
    public RpcContext setLocalAddress(String host, int port) {
        if (port < 0) {
            port = 0;
        }
        this.localAddress = InetSocketAddress.createUnresolved(host, port);
        return this;
    }

	/**
	 * get local address.
	 * 
	 * @return local address
	 */
	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	public String getLocalAddressString() {
        return getLocalHost() + ":" + getLocalPort();
    }
    
	/**
	 * get local host name.
	 * 
	 * @return local host name
	 */
	public String getLocalHostName() {
		String host = localAddress == null ? null : localAddress.getHostName();
		if (host == null || host.length() == 0) {
		    return getLocalHost();
		}
		return host;
	}

    /**
     * set remote address.
     * 
     * @param address
     * @return context
     */
    public RpcContext setRemoteAddress(InetSocketAddress address) {
        this.remoteAddress = address;
        return this;
    }
    
    /**
     * set remote address.
     * 
     * @param host
     * @param port
     * @return context
     */
    public RpcContext setRemoteAddress(String host, int port) {
        if (port < 0) {
            port = 0;
        }
        this.remoteAddress = InetSocketAddress.createUnresolved(host, port);
        return this;
    }

	/**
	 * get remote address.
	 * 
	 * @return remote address
	 */
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	
	/**
	 * get remote address string.
	 * 
	 * @return remote address string.
	 */
	public String getRemoteAddressString() {
	    return getRemoteHost() + ":" + getRemotePort();
	}
	
	/**
	 * get remote host name.
	 * 
	 * @return remote host name
	 */
	public String getRemoteHostName() {
		return remoteAddress == null ? null : remoteAddress.getHostName();
	}

    /**
     * get local host.
     * 
     * @return local host
     */
    public String getLocalHost() {
        String host = localAddress == null ? null : 
            localAddress.getAddress() == null ? localAddress.getHostName() 
                    : NetUtils.filterLocalHost(localAddress.getAddress().getHostAddress());
        if (host == null || host.length() == 0) {
            return NetUtils.getLocalHost();
        }
        return host;
    }

    /**
     * get local port.
     * 
     * @return port
     */
    public int getLocalPort() {
        return localAddress == null ? 0 : localAddress.getPort();
    }

    /**
     * get remote host.
     * 
     * @return remote host
     */
    public String getRemoteHost() {
        return remoteAddress == null ? null : 
            remoteAddress.getAddress() == null ? remoteAddress.getHostName() 
                    : NetUtils.filterLocalHost(remoteAddress.getAddress().getHostAddress());
    }

    /**
     * get remote port.
     * 
     * @return remote port
     */
    public int getRemotePort() {
        return remoteAddress == null ? 0 : remoteAddress.getPort();
    }

    /**
     * get values.
     * 
     * @return
     */
    public Map<String, Object> get() {
        return values;
    }

    /**
     * set values
     * 
     * @param values
     * @return
     */
    public RpcContext set(Map<String, Object> value) {
        this.values.clear();
        if (value != null && value.size() > 0) {
            this.values.putAll(value);
        }
        return this;
    }
    
    /**
     * set value.
     * 
     * @param key
     * @param value
     * @return context
     */
    public RpcContext set(String key, Object value) {
        if (value == null) {
            values.remove(key);
        } else {
            values.put(key, value);
        }
        return this;
    }
    
    /**
     * remove value.
     * 
     * @param key
     * @return value
     */
    public RpcContext remove(String key) {
        values.remove(key);
        return this;
    }

    /**
     * get value.
     * 
     * @param key
     * @return value
     */
    public Object get(String key) {
        return values.get(key);
    }

    /**
     * get attachments.
     * 
     * @return
     */
    public Map<String, String> getAttachments() {
        return attachments;
    }

    /**
     * set attachments
     * 
     * @param attachment
     * @return
     */
    public RpcContext setAttachments(Map<String, String> attachment) {
        this.attachments.clear();
        if (attachment != null && attachment.size() > 0) {
            this.attachments.putAll(attachment);
        }
        return this;
    }
    
    /**
     * set attachment.
     * 
     * @param key
     * @param value
     * @return
     */
    public RpcContext setAttachment(String key, String value) {
        if (value == null) {
            attachments.remove(key);
        } else {
            attachments.put(key, value);
        }
        return this;
    }

    /**
     * remove attachment.
     * 
     * @param key
     * @return
     */
    public RpcContext removeAttachment(String key) {
        attachments.remove(key);
        return this;
    }

    /**
     * get attachment.
     * 
     * @param key
     * @return
     */
    public Object getAttachment(String key) {
        return attachments.get(key);
    }

    /**
     * get future.
     * 
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Future<T> getFuture() {
        return (Future<T>) future;
    }

    /**
     * set future.
     * 
     * @param future
     */
    public void setFuture(Future<?> future) {
        this.future = future;
    }
    
}
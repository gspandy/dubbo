package com.alibaba.dubbo.demo.registry;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.RegistryService;

public class RegistryServiceLocal implements RegistryService {
    RegistryService registryService;
    ConcurrentMap<URL, NotifyListener> listeners = new ConcurrentHashMap<URL, NotifyListener>();
    

    public RegistryServiceLocal(RegistryService registryService) {
        this.registryService = registryService;
    }

    public void register(URL url) {
        registryService.register(url);
    }

    public void unregister(URL url) {
        registryService.unregister(url);
    }

    public void subscribe(URL url, NotifyListener listener) {
        registryService.subscribe(url, listener);
        listeners.put(url, listener);
    }

    public void unsubscribe(URL url, NotifyListener listener) {
        registryService.unsubscribe(url, listener);
    }

    public List<URL> lookup(URL url) {
        return registryService.lookup(url);
    }
    
    public void ondisconnect(){
        for(URL url : listeners.keySet()){
            registryService.subscribe(url, listeners.get(url));
        }
    }
}

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
package com.alibaba.dubbo.rpc.dubbo.telnet;

import java.util.Collection;

import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.ExchangeServer;
import com.alibaba.dubbo.remoting.telnet.TelnetHandler;
import com.alibaba.dubbo.remoting.telnet.support.Help;
import com.alibaba.dubbo.rpc.dubbo.DubboProtocol;

/**
 * ServerTelnetHandler
 * 
 * @author william.liangf
 */
@Help(parameter = "[-l] [port]", summary = "Print server ports and connections.", detail = "Print server ports and connections.")
@Extension("ps")
public class PortTelnetHandler implements TelnetHandler {

    public String telnet(Channel channel, String message) {
        StringBuilder buf = new StringBuilder();
        String port = null;
        boolean detail = false;
        if (message.length() > 0) {
            String[] parts = message.split("\\s+");
            for (String part : parts) {
                if ("-l".equals(part)) {
                    detail = true;
                } else {
                    if (port != null && port.length() > 0) {
                        return "Invaild parameter " + part;
                    }
                    port = part;
                }
            }
        }
        if (port == null || port.length() == 0) {
            for (ExchangeServer server : DubboProtocol.getDubboProtocol().getServers()) {
                if (buf.length() > 0) {
                    buf.append("\r\n");
                }
                if (detail) {
                    buf.append(server.getUrl().getProtocol() + "://" + server.getUrl().getAddress());
                } else {
                    buf.append(server.getUrl().getPort());
                }
            }
        } else {
            if (! StringUtils.isInteger(port)) {
                return "Illegal port " + port + ", must be integer.";
            }
            int p = Integer.parseInt(port);
            ExchangeServer server = null;
            for (ExchangeServer s : DubboProtocol.getDubboProtocol().getServers()) {
                if (p == s.getUrl().getPort()) {
                    server = s;
                    break;
                }
            }
            if (server != null) {
                Collection<ExchangeChannel> channels = server.getExchangeChannels();
                for (ExchangeChannel c : channels) {
                    if (buf.length() > 0) {
                        buf.append("\r\n");
                    }
                    if (detail) {
                        buf.append(c.getRemoteAddress() + " -> " + c.getLocalAddress());
                    } else {
                        buf.append(c.getRemoteAddress());
                    }
                }
            } else {
                buf.append("No such port " + port);
            }
        }
        return buf.toString();
    }

}
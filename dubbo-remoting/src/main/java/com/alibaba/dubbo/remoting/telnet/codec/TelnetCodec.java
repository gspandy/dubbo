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
package com.alibaba.dubbo.remoting.telnet.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.transport.codec.TransportCodec;

/**
 * TelnetCodec
 * 
 * @author heyman
 * @author william.liangf
 * @author chao.liuc
 */
@Extension("telnet")
public class TelnetCodec extends TransportCodec {

    private static final Logger  logger = LoggerFactory.getLogger(TelnetCodec.class);
    
    private static final String HISTORY_LIST_KEY = "telnet.history.list";

    private static final String HISTORY_INDEX_KEY = "telnet.history.index";
    
    private static final byte[] UP = new byte[] {27, 91, 65};
    
    private static final byte[] DOWN = new byte[] {27, 91, 66};

    private static final List<?> ENTER  = Arrays.asList(new Object[] { new byte[] { '\r', '\n' } /* Windows Enter */, new byte[] { '\n' } /* Linux Enter */ });

    private static final List<?> EXIT   = Arrays.asList(new Object[] { new byte[] { 3 } /* Windows Ctrl+C */, new byte[] { -1, -12, -1, -3, 6 } /* Linux Ctrl+C */, new byte[] { -1, -19, -1, -3, 6 } /* Linux Pause */ });

    public void encode(Channel channel, OutputStream output, Object message) throws IOException {
        if (message instanceof String) {
            if (isClientSide(channel)) {
                message = message + "\r\n";
            }
            byte[] msgData = ((String) message).getBytes(getCharset(channel).name());
            output.write(msgData);
            output.flush();
        } else {
            super.encode(channel, output, message);
        }
    }
    
    public Object decode(Channel channel, InputStream is) throws IOException {
        int readable = is.available();
        byte[] message = new byte[readable];
        is.read(message);
        return decode(channel, is, readable, message);
    }

    @SuppressWarnings("unchecked")
    protected Object decode(Channel channel, InputStream is, int readable, byte[] message) throws IOException {
        if (isClientSide(channel)) {
            return toString(message, getCharset(channel));
        }
        checkPayload(channel, readable);
        if (message == null || message.length == 0) {
            return NEED_MORE_INPUT;
        }
        
        if (message[message.length - 1] == '\b') { // Windows backspace echo
            try {
                boolean doublechar = message.length > 2 && message[message.length - 3] < 0; // double byte char
                channel.send(new String(doublechar ? new byte[] {32, 32, 8, 8} : new byte[] {32, 8}, getCharset(channel).name()));
            } catch (RemotingException e) {
                throw new IOException(StringUtils.toString(e));
            }
            return NEED_MORE_INPUT;
        }
        
        for (Object command : EXIT) {
            if (isEquals(message, (byte[]) command)) {
                if (logger.isInfoEnabled()) {
                    logger.info(new Exception("Close channel " + channel + " on exit command " + command));
                }
                channel.close();
                return null;
            }
        }
        
        boolean up = endsWith(message, UP);
        boolean down = endsWith(message, DOWN);
        if (up || down) {
            LinkedList<String> history = (LinkedList<String>) channel.getAttribute(HISTORY_LIST_KEY);
            if (history == null || history.size() == 0) {
                return NEED_MORE_INPUT;
            }
            byte[] input = new byte[message.length - (up? UP.length:DOWN.length) ];
            System.arraycopy(message, 0, input, 0, input.length);
            Integer currentIndex = (Integer) channel.getAttribute(HISTORY_INDEX_KEY);
            Integer targetIndex = currentIndex;
            if (currentIndex == null) {
                currentIndex = history.size() - 1;
                //first up need point to the last history item
                targetIndex = history.size();
            }
            if (up) {
                if (targetIndex > 0 ){
                    targetIndex = Math.min(targetIndex - 1, history.size() -1);
                }
            } else {
                if (targetIndex < history.size() - 1){
                    targetIndex = Math.min(targetIndex + 1, history.size() -1);
                }
                //else current input?? message-down
            }
            String value = history.get(targetIndex);
            String out = getbackSpaceString(toString(input, getCharset(channel)));
            value = out + value;
            try {
                channel.send(value);
            } catch (RemotingException e) {
                throw new IOException(StringUtils.toString(e));
            }
            channel.setAttribute(HISTORY_INDEX_KEY, targetIndex);
            return NEED_MORE_INPUT;
        } else {
            byte[] enter = null;
            for (Object command : ENTER) {
                if (endsWith(message, (byte[]) command)) {
                    enter = (byte[]) command;
                    break;
                }
            }
            if (enter == null) {
                return NEED_MORE_INPUT;
            }             
            LinkedList<String> history = (LinkedList<String>) channel.getAttribute(HISTORY_LIST_KEY);
            if (history == null ) {
                //telnet ignore concurrent
                history = new LinkedList<String>();
                channel.setAttribute(HISTORY_LIST_KEY, history);
            }
            String result = toString(message, getCharset(channel));
            history.add(result);
            return result;
        }
        
        
    }
    private static String getbackSpaceString(String input){
        StringBuffer buf = new StringBuffer(input.length()*3);
        for (int i = 0; i < input.length(); i ++) {
            buf.append("\b");
        }
        for (int i = 0; i < input.length(); i ++) {
            buf.append(" ");
        }
        for (int i = 0; i < input.length(); i ++) {
            buf.append("\b");
        }
        buf.append(input);
        return buf.toString();
    }
    
    private static boolean isClientSide(Channel channel) {
        InetSocketAddress address = channel.getRemoteAddress();
        URL url = channel.getUrl();
        return url.getPort() == address.getPort() && 
                    NetUtils.filterLocalHost(url.getHost())
                    .equals(NetUtils.filterLocalHost(address.getAddress().getHostAddress()));
    }

    private static Charset getCharset(Channel channel) {
        if (channel != null) {
            Object attribute = channel.getAttribute(Constants.CHARSET_KEY);
            if (attribute instanceof String) {
                try {
                    return Charset.forName((String) attribute);
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            } else if (attribute instanceof Charset) {
                return (Charset) attribute;
            }
            URL url = channel.getUrl();
            if (url != null) {
                String parameter = url.getParameter(Constants.CHARSET_KEY);
                if (parameter != null && parameter.length() > 0) {
                    try {
                        return Charset.forName(parameter);
                    } catch (Throwable t) {
                        logger.warn(t.getMessage(), t);
                    }
                }
            }
        }
        try {
            return Charset.forName("GBK");
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
        return Charset.defaultCharset();
    }

    private static String toString(byte[] message, Charset charset) throws UnsupportedEncodingException {
        byte[] copy = new byte[message.length];
        int index = 0;
        for (int i = 0; i < message.length; i ++) {
            byte b = message[i] ;
            if (b == '\b') { // backspace
                if (index > 0) {
                    index --;
                }
                if (i > 2 && message[i - 2] < 0) { // double byte char
                    if (index > 0) {
                        index --;
                    }
                }
            } else if (b == 27) { // escape
                if (i < message.length - 4 && message[i + 4] == 126) {
                    i = i + 4;
                } else if (i < message.length - 3 && message[i + 3] == 126) {
                    i = i + 3;
                } else if (i < message.length - 2) {
                    i = i + 2;
                }
            } else if (b == -1 && i < message.length - 2 
                    && (message[i + 1] == -3 || message[i + 1] == -5)) { // handshake
                i = i + 2;
            } else {
                copy[index ++] = message[i];
            }
        }
        if (index == 0) {
            return "";
        }
        return new String(copy, 0, index, charset.name()).trim();
    }

    private static boolean isEquals(byte[] message, byte[] command) throws IOException {
        return message.length == command.length && endsWith(message, command);
    }

    private static boolean endsWith(byte[] message, byte[] command) throws IOException {
        if (message.length < command.length) {
            return false;
        }
        int offset = message.length - command.length;
        for (int i = command.length - 1; i >= 0 ; i --) {
            if (message[offset + i] != command[i]) {
                return false;
            }
        }
        return true;
    }

}
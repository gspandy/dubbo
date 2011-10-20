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
package com.alibaba.dubbo.remoting.exchange;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Request.
 * 
 * @author qian.lei
 * @author william.liangf
 */
public class Request {

    private static final AtomicLong INVOKE_ID = new AtomicLong(0);

    private final long    mId;

    private String  mVersion;

    private boolean mTwoWay   = true;

    private boolean mHeatbeat = false;

    private boolean mBroken   = false;

    private Object  mData;

    public Request() {
        mId = newId();
    }

    public Request(long id){
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public boolean isTwoWay() {
        return mTwoWay;
    }

    public void setTwoWay(boolean twoWay) {
        mTwoWay = twoWay;
    }

    public boolean isHeartbeat() {
        return mHeatbeat;
    }

    public void setHeartbeat(boolean isHeartbeat) {
        this.mHeatbeat = isHeartbeat;
    }

    public boolean isBroken() {
        return mBroken;
    }

    public void setBroken(boolean mBroken) {
        this.mBroken = mBroken;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object msg) {
        mData = msg;
    }

    private static long newId() {
        // getAndIncrement()增长到MAX_VALUE时，再增长会变为MIN_VALUE，负数也可以做为ID
        return INVOKE_ID.getAndIncrement();
    }

    @Override
    public String toString() {
        return "Request [id=" + mId + ", version=" + mVersion + ", twoway=" + mTwoWay + ", heatbeat=" + mHeatbeat
               + ", broken=" + mBroken + ", data=" + (mData == this ? "this" : mData) + "]";
    }

}
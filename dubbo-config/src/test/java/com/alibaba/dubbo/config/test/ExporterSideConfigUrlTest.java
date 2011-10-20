/**
 * Project: dubbo.test
 * 
 * File Created at 2011-8-17
 * $Id: ExporterSideConfigUrlTest.java 118511 2011-10-19 12:45:01Z haomin.liuhm $
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.dubbo.config.test;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;


/**
 * @author haomin.liuhm
 *
 */
public class ExporterSideConfigUrlTest extends UrlTestBase {
    
    private static final Logger log = LoggerFactory.getLogger(ExporterSideConfigUrlTest.class);
    
    // ======================================================
    //   tests start
    // ======================================================  
    @BeforeClass
    public static void start(){
        
        
    }
    
    
    @Before
    public void setUp(){
        
        initServConf();
        
        return;
    }
    
    @After()
    public void teardown() {
    }
    
    @Test
    public void exporterMethodConfigUrlTest(){
        
        verifyExporterUrlGeneration(methodConfForService, methodConfForServiceTable);
    }
    
    @Test
    public void exporterServiceConfigUrlTest(){
        
        verifyExporterUrlGeneration(servConf, servConfTable);
    }
    
    @Test
    public void exporterProviderConfigUrlTest(){
        
        verifyExporterUrlGeneration(provConf, provConfTable);
    }
    
    @Test
    public void exporterRegistryConfigUrlTest(){
        
        //verifyExporterUrlGeneration(regConfForService, regConfForServiceTable);
    }


    protected <T> void verifyExporterUrlGeneration(T config, Object[][] dataTable) {
        
        // 1. fill corresponding config with data
        ////////////////////////////////////////////////////////////
        fillConfigs(config, dataTable, TESTVALUE1);
        
        // 2. export service and get url parameter string from db
        ////////////////////////////////////////////////////////////
        servConf.export();
        String paramStringFromDb = getProviderParamString();
        try {
            paramStringFromDb = URLDecoder.decode(paramStringFromDb, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // impossible
        }
        
        
        assertUrlStringWithLocalTable(paramStringFromDb, dataTable, config.getClass().getName(), TESTVALUE1);
        
       
        // 4. unexport service
        ////////////////////////////////////////////////////////////
        servConf.unexport();
    }
}

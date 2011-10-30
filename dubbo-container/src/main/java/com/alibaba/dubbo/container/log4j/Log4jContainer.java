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
package com.alibaba.dubbo.container.log4j;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.container.Container;

/**
 * Log4jContainer
 * 
 * @author william.liangf
 */
@Extension("log4j")
public class Log4jContainer implements Container {

    private static final Logger logger = LoggerFactory.getLogger(Log4jContainer.class);

    public static final String LOG4J_FILE = "log4j.file";

    public static final String LOG4J_LEVEL = "log4j.level";

    public static final String LOG4J_SUBDIRECTORY = "log4j.subdirectory";

    public static final String DEFAULT_LOG4J_FILE = System.getProperty("user.home") + "/dubbo.log";

    public static final String DEFAULT_LOG4J_LEVEL = "ERROR";

    @SuppressWarnings("unchecked")
    public void start() {
        String file = System.getProperty(LOG4J_FILE);
        if (file == null || file.length() == 0) {
            file = DEFAULT_LOG4J_FILE;
        }
        String level = System.getProperty(LOG4J_LEVEL);
        if (level == null || level.length() == 0) {
            level = DEFAULT_LOG4J_LEVEL;
        }
        Properties properties = new Properties();
        properties.setProperty("log4j.rootLogger", level + ",application");
        properties.setProperty("log4j.appender.application", "org.apache.log4j.DailyRollingFileAppender");
        properties.setProperty("log4j.appender.application.File", file);
        properties.setProperty("log4j.appender.application.Append", "true");
        properties.setProperty("log4j.appender.application.DatePattern", "'.'yyyy-MM-dd");
        properties.setProperty("log4j.appender.application.layout", "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.application.layout.ConversionPattern", "%d [%t] %-5p %C{6} (%F:%L) - %m%n");
        PropertyConfigurator.configure(properties);
        String subdirectory = System.getProperty(LOG4J_SUBDIRECTORY);
        if (subdirectory != null && subdirectory.length() > 0) {
            Enumeration<org.apache.log4j.Logger> ls = LogManager.getCurrentLoggers();
            while (ls.hasMoreElements()) {
                modifyLogDirectory(ls.nextElement(), subdirectory);
            }
        }
        logger.info("Dubbo log4j container started!");
    }

    public void stop() {
        logger.info("Dubbo log4j container stopped!");
    }

    @SuppressWarnings("unchecked")
    private static String[] modifyLogDirectory(org.apache.log4j.Logger l, String subdirectory) {
        String[] params = new String[3];
        if (l != null) {
            params[2] = l.getLevel() == null ? null : l.getLevel().toString();
            Enumeration<Appender> as = l.getAllAppenders();
            while (as.hasMoreElements()) {
                Appender a = as.nextElement();
                if (a instanceof FileAppender) {
                    FileAppender fa = (FileAppender)a;
                    String file = fa.getFile();
                    if (file != null && file.length() > 0) {
                        int i = file.replace('\\', '/').lastIndexOf('/');
                        String path;
                        if (i == -1) {
                            path = subdirectory;
                        } else {
                            path = file.substring(0, i);
                            if (! path.endsWith(subdirectory)) {
                                path = path + "/" + subdirectory;
                            }
                            file = file.substring(i + 1);
                        }
                        params[0] = path;
                        params[1] = file;
                        fa.setFile(path + "/" + file);
                        fa.activateOptions();
                    }
                }
            }
        }
        return params;
    }

}
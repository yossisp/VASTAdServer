package com.vastserver.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VastLogger {
    public static Logger getLogger(Class clazz) {
        return LogManager.getLogger(clazz);
    }
}

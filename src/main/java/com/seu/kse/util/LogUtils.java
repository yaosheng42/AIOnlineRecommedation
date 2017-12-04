package com.seu.kse.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogUtils {

    public static void debug(String msg,Class caller){

        Logger logger = LoggerFactory.getLogger(caller);
        logger.debug(msg);

    }

    public static void error(String msg,Class caller){

        Logger logger = LoggerFactory.getLogger(caller);
        logger.error(msg);
    }

    public static void info(String msg,Class caller){

        Logger logger = LoggerFactory.getLogger(caller);
        logger.info(msg);
    }

}

package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {

    private LoggerUtil(){}

    public static Logger getLogger(Class<?> cls) {

        return LogManager.getLogger(cls);
    }
}
package cn.edu.bjtu.eboscommand.service.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogImpl {
    private static final Logger log = LogManager.getLogger("cn.edu.bjtu.eboscommand.service.log");
    public static void debug(String message) {
            log.debug(message);
    }
    public static void debug(String message, Exception e) {
            log.debug(message, e);
    }
    public static void info(String message) {
            log.info(message);
    }
    public static void info(String message, Exception e) {
            log.info(message, e);
    }
    public static void warn(String message) {
            log.warn(message);
    }
    public static void warn(String message, Exception e) {
            log.warn(message, e);
    }
    public static void error(String message) {
            log.error(message);
    }
    public static void error(String message, Exception e) {
            log.error(message, e);
    }

}

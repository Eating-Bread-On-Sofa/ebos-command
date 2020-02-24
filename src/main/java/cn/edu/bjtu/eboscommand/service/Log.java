package cn.edu.bjtu.eboscommand.service;

import org.springframework.stereotype.Component;

@Component
public interface Log {
    void debug(String message);
    void debug(String message, Exception e);
    void info(String message);
    void info(String message, Exception e);
    void warn(String message);
    void warn(String message, Exception e);
    void error(String message);
    void error(String message, Exception e);
}

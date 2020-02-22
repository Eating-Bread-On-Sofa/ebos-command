package cn.edu.bjtu.eboscommand.service;

public interface Log {
    void debug(String message);
    void debug(String message, Exception e);
    void info(String message);
    void info(String message, Exception e);
    void warn(String message);
    void warn(String message, Exception e);

}

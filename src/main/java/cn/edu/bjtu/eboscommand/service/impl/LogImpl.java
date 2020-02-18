package cn.edu.bjtu.eboscommand.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class LogImpl {
    public static String top = "";
    private static Logger log = LogManager.getLogger();
    public static void trace(String message) {
        if (log.isTraceEnabled()) {
            getTop();
            log.trace(top + "-" + message);
        }
    }
    public static void trace(String message, Exception e) {
        if (log.isTraceEnabled()) {
            getTop();
            log.trace(top + "-" + message, e);
        }
    }
    public static void debug(String message) {
        if (log.isDebugEnabled()) {
            getTop();
            log.debug(top + "-" + message);
        }
    }
    public static void debug(String message, Exception e) {
        if (log.isDebugEnabled()) {
            getTop();
            log.debug(top + "-" + message, e);
        }
    }
    public static void info(String message) {
        if (log.isInfoEnabled()) {
            getTop();
            log.info(top + "-" + message);
        }
    }
    public static void info(String message, Exception e) {
        if (log.isInfoEnabled()) {
            getTop();
            log.info(top + "-" + message, e);
        }
    }
    public static void warn(String message) {
        if (log.isWarnEnabled()) {
            getTop();
            log.warn(top + "-" + message);
        }
    }
    public static void warn(String message, Exception e) {
        if (log.isWarnEnabled()) {
            getTop();
            log.warn(top + "-" + message, e);
        }
    }
    public static void error(String message) {
        if (log.isErrorEnabled()) {
            getTop();
            log.error(top + "-" + message);
        }
    }
    public static void error(String message, Exception e) {
        if (log.isErrorEnabled()) {
            getTop();
            log.error(top + "-" + message, e);
        }
    }
    public static void fatal(String message) {
        if (log.isFatalEnabled()) {
            getTop();
            log.fatal(top + "-" + message);
        }
    }
    public static void fatal(String message, Exception e) {
        if (log.isFatalEnabled()) {
            getTop();
            log.fatal(top + "-" + message, e);
        }
    }
    public static void getTop() {
        // 获取堆栈信息
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        if (callStack == null) {
            top = "";
        }
        else {
            // 最原始被调用的堆栈信息
            StackTraceElement caller = null;
            // 日志类名称
            String logClassName = Logger.class.getName();
            // 循环遍历到日志类标识
            boolean isEachLogClass = false;
            // 遍历堆栈信息，获取出最原始被调用的方法信息
            for (StackTraceElement s : callStack) {
                // 遍历到日志类
                if (logClassName.equals(s.getClassName())) {
                    isEachLogClass = true;
                }
                // 下一个非日志类的堆栈，就是最原始被调用的方法
                if (isEachLogClass) {
                    if (!logClassName.equals(s.getClassName())) {
                        isEachLogClass = false;
                        caller = s;
                        break;
                    }
                }
            }
            top = caller.toString();
        }
    }
    public static String read (String filepath) {
        File file = new File(filepath);
        String enCode = "UTF-8";
        if (file.isFile() && file.exists()) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),enCode);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer();
                String content = "";
                while ((content = bufferedReader.readLine()) != null) {
                    content += '\n';
                    sb.append(content);
                }
                bufferedReader.close();
                inputStreamReader.close();
                return sb.toString();
            }
            catch (Exception e) {
                System.out.println("读取文件内容出错");
                e.printStackTrace();
            }
        }else{
            System.out.println("找不到指定文件");
        }
        return null;
    }
    public static String readLine(String filePath, int lineNumber){
        FileReader fr = null;
        LineNumberReader reader = null;
        String content = "";
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            try{
                fr = new FileReader(file);
                reader = new LineNumberReader(fr);
                int line = 0;
                while(content != null){
                    line ++;
                    content = reader.readLine(); // 读出一行的内容
                    if(line == lineNumber){
                        break;
                    }
                }
                reader.close();
                fr.close();
                return content;
            }catch(Exception e) {
                System.out.println("读取文件内容出错");
                e.printStackTrace();
            }
        }else{
            System.out.println("找不到指定文件");
        }
        return null;
    }
}

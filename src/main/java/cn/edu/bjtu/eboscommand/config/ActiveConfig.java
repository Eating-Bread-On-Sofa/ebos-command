package cn.edu.bjtu.eboscommand.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActiveConfig {

    private static String user;

    private static String password;

    private static String url;

    public static String getUser() { return user; }

    @Value("${spring.activemq.user}")
    public void setUser(String user) { ActiveConfig.user = user; }

    public static String getPassword() {
        return password;
    }

    @Value("${spring.activemq.password}")
    public void setPassword(String password) {
        ActiveConfig.password = password;
    }

    public static String getUrl() {
        return url;
    }

    @Value("${spring.activemq.broker-url}")
    public void setUrl(String url) { ActiveConfig.url = url; }
}

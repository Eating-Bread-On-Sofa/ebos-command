package cn.edu.bjtu.eboscommand.service;

import cn.edu.bjtu.eboscommand.entity.Command;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class InitListener implements ApplicationRunner {
    @Autowired
    MqFactory mqFactory;
    @Autowired
    CommandService commandService;
    @Value("${mq}")
    private String name;

    @Override
    public void run(ApplicationArguments arguments) {
        new Thread(() -> {
            MqConsumer mqConsumer = mqFactory.createConsumer("run.command");
            while (true) {
                JSONObject msg = JSON.parseObject(mqConsumer.subscribe());
                System.out.println("收到：" + msg);
                Command fullMsg = commandService.find(msg.getString("name"));
                switch (fullMsg.getLevel()) {
                    case 1:
                        commandService.sendCommand(fullMsg);
                        break;
                    case 2:
                        JSONArray array = fullMsg.getJsonArray();
                        for (int i = 0; i < array.size(); i++) {
                            Command subMsg = commandService.find(array.getJSONObject(i).getString("name"));
                            commandService.sendCommand(subMsg);
                        }
                        break;
                }
            }
        }).start();
    }
}

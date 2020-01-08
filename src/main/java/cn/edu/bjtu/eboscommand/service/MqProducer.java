package cn.edu.bjtu.eboscommand.service;

public interface MqProducer {
    void publish(String topic, String message);
}

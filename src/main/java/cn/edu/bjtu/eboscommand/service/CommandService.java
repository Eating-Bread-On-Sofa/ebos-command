package cn.edu.bjtu.eboscommand.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface CommandService {
    boolean addCommand(JSONObject info);
    boolean deleteCommand(String name);
    JSONArray showAll();
    JSONObject find(String name);
    void sendCommand(JSONObject command);
}

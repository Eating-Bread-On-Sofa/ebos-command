package cn.edu.bjtu.eboscommand.service.impl;

import cn.edu.bjtu.eboscommand.service.MqFactory;
import cn.edu.bjtu.eboscommand.service.MqProducer;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.eboscommand.dao.CommandRepository;
import cn.edu.bjtu.eboscommand.entity.Command;
import cn.edu.bjtu.eboscommand.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CommandServiceImpl implements CommandService {
    @Autowired
    CommandRepository commandRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    MqFactory mqFactory;
    @Value("${server.edgex}")
    private String ip;

    @Override
    public boolean addCommand(JSONObject info){
        Command command = new Command(info);
        Command findCommand = commandRepository.findByName(command.getName());
        if(findCommand == null){
            commandRepository.save(command);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean deleteCommand(String name){
        Command find = commandRepository.findByName(name);
        if(find == null){
            return false;
        }else {
            commandRepository.deleteById(find.getId());
            return true;        }
    }

    @Override
    public JSONArray showAll(){
        JSONArray all = new JSONArray();
        List<Command> allCommands = commandRepository.findAll();
        allCommands.forEach(command -> all.add(command.toJson()));
        return all;
    }

    @Override
    public JSONObject find(String name){
        Command find = commandRepository.findByName(name);
        return find.toJson();
    }

    @Override
    public void sendCommand(JSONObject command){
        String url = "http://"+ip+":48082/api/v1/device/" + command.getString("deviceId")+ "/command/" + command.getString("commandId");
        MqProducer mqProducer = mqFactory.createProducer();
        switch (command.getString("commandType")){
            case "get":
                try {
                    JSONObject getObj = new JSONObject(restTemplate.getForObject(url, JSONObject.class));
                    mqProducer.publish("command.result",getObj.toString());
                } catch (Exception e) {
                    JSONObject err = new JSONObject();
                    err.put("name",command.getString("name"));
                    err.put("alert","失败！");
                    mqProducer.publish("command.result",err.toString());
                }
                break;
            case "put":
                restTemplate.put(url,command.getJSONObject("jsonObject"), String.class);
                break;
        }
    }

    @Override
    public void plusCommand(JSONArray jsonArray){
        for (int i = 0; i<jsonArray.size();i++) {
            Command command = new Command(jsonArray.getJSONObject(i));
            Command findCommand = commandRepository.findByName(command.getName());
            if(findCommand != null){
                commandRepository.delete(findCommand);
            }
            commandRepository.save(command);
        }
    }
}

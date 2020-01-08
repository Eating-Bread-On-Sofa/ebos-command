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
        Command command = new Command();
        command.setName(info.getString("name"));
        command.setCommandId(info.getString("commandId"));
        command.setCommandName(info.getString("commandName"));
        command.setDeviceId(info.getString("deviceId"));
        command.setDeviceName(info.getString("deviceName"));
        command.setCommandType(info.getString("commandType"));
        command.setJsonObject(info.getJSONObject("jsonObject"));
        command.setJsonArray(info.getJSONArray("jsonArray"));
        command.setLevel(info.getIntValue("level"));
        command.setDescription(info.getString("description"));
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
        for(int i=0; i<allCommands.size();i++){
            JSONObject command = new JSONObject();
            command.put("name",allCommands.get(i).getName());
            command.put("commandId",allCommands.get(i).getCommandId());
            command.put("commandName",allCommands.get(i).getCommandName());
            command.put("commandType",allCommands.get(i).getCommandType());
            command.put("deviceId",allCommands.get(i).getDeviceId());
            command.put("deviceName",allCommands.get(i).getDeviceName());
            command.put("jsonObject",allCommands.get(i).getJsonObject());
            command.put("jsonArray",allCommands.get(i).getJsonArray());
            command.put("level",allCommands.get(i).getLevel());
            command.put("description",allCommands.get(i).getDescription());
            all.add(command);
        }
        return all;
    }

    @Override
    public JSONObject find(String name){
        Command find = commandRepository.findByName(name);
        JSONObject command = new JSONObject();
        command.put("name",find.getName());
        command.put("commandId",find.getCommandId());
        command.put("commandName",find.getCommandName());
        command.put("commandType",find.getCommandType());
        command.put("deviceId",find.getDeviceId());
        command.put("deviceName",find.getDeviceName());
        command.put("jsonObject",find.getJsonObject());
        command.put("jsonArray",find.getJsonArray());
        command.put("level",find.getLevel());
        command.put("description",find.getDescription());
        return command;
    }

    @Override
    public void sendCommand(JSONObject command){
        String url = "http://"+ip+":48082/api/v1/device/" + command.getString("deviceId")+ "/command/" + command.getString("commandId");
        MqProducer mqProducer = mqFactory.createProducer();
        switch (command.getString("commandType")){
            case "get":
                try {
                    JSONObject getObj = new JSONObject(restTemplate.getForObject(url, JSONObject.class));
                    mqProducer.publish("show",getObj.toString());
                } catch (Exception e) {
                    JSONObject err = new JSONObject();
                    err.put("name",command.getString("name"));
                    err.put("alert","失败！");
                    mqProducer.publish("show",err.toString());
                }
                break;
            case "put":
                restTemplate.put(url,command.getJSONObject("jsonObject"), String.class);
                break;
        }
    }
}

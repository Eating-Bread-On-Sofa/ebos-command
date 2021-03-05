package cn.edu.bjtu.eboscommand.service.impl;

import cn.edu.bjtu.eboscommand.service.MqFactory;
import cn.edu.bjtu.eboscommand.service.MqProducer;
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
    @Value("${docker}")
    private String ip;

    @Override
    public boolean addCommand(Command command){
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
    public List<Command> showAll(){
        return commandRepository.findAll();
    }

    @Override
    public Command find(String name){
        return commandRepository.findByName(name);
    }

    @Override
    public void sendCommand(Command command){
        String url = "http://"+ip+":48082/api/v1/device/" + command.getDeviceId()+ "/command/" + command.getCommandId();
        MqProducer mqProducer = mqFactory.createProducer();
        switch (command.getCommandType()){
            case "get":
                try {
                    JSONObject getObj = new JSONObject(restTemplate.getForObject(url, JSONObject.class));
                    mqProducer.publish("command.result",getObj.toString());
                } catch (Exception e) {
                    JSONObject err = new JSONObject();
                    err.put("name",command.getName());
                    err.put("alert","失败！");
                    err.put("exception",e.toString());
                    mqProducer.publish("command.result",err.toString());
                }
                break;
            case "put":
                restTemplate.put(url,command.getJsonObject(), String.class);
                break;
        }
    }

    @Override
    public void plusCommand(Command[] commands){
        for (Command command:commands) {
            Command findCommand = commandRepository.findByName(command.getName());
            if(findCommand != null){
                commandRepository.delete(findCommand);
            }
            commandRepository.save(command);
        }
    }
}

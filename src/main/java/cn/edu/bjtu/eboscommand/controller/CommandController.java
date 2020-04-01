package cn.edu.bjtu.eboscommand.controller;

import cn.edu.bjtu.eboscommand.service.LogService;
import cn.edu.bjtu.eboscommand.service.MqFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.eboscommand.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/api/command")
@RestController
public class CommandController {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CommandService commandService;
    @Autowired
    MqFactory mqFactory;
    @Autowired
    LogService logService;
    @Value("${server.edgex}")
    private String ip;

    @CrossOrigin
    @GetMapping("/list")
    public JSONArray checkCommand(){
        String allUrl = "http://"+ip+":48082/api/v1/device";
        JSONArray commands = new JSONArray();
        JSONArray all = new JSONArray(restTemplate.getForObject(allUrl,JSONArray.class));
        for(int i = 0; i<all.size();i++) {
            JSONObject deviceObj = all.getJSONObject(i);
            String deviceId = deviceObj.getString("id");
            String deviceName = deviceObj.getString("name");
            JSONArray commandsArr = deviceObj.getJSONArray("commands");
            for(int j = 0; j < commandsArr.size();j++) {
                String commandId = commandsArr.getJSONObject(j).getString("id");
                String commandName = commandsArr.getJSONObject(j).getString("name");
                JSONObject thisCommand = new JSONObject();
                thisCommand.put("deviceId",deviceId);
                thisCommand.put("deviceName",deviceName);
                thisCommand.put("commandId",commandId);
                thisCommand.put("commandName",commandName);
                commands.add(thisCommand);
            }
        }
        return commands;
    }

    @CrossOrigin
    @PostMapping()
    public String add(@RequestBody JSONObject info){
        boolean flag = commandService.addCommand(info);
        if(flag){
            logService.info("添加新指令"+info.getString("name"));
            return "添加成功！";
        }else {
            logService.warn("添加指令失败");
            return "添加失败！";
        }
    }

    @CrossOrigin
    @PostMapping("/recover")
    public String plus(@RequestBody JSONArray jsonArray){
        try {
            commandService.plusCommand(jsonArray);
            logService.info("command已成功恢复以下数据"+jsonArray.toString());
            return "command已恢复";
        }catch (Exception e){
            logService.error(e.toString());
            return e.toString();}
    }

    @CrossOrigin
    @DeleteMapping()
    public boolean delete(@RequestParam String name){
        boolean flag = commandService.deleteCommand(name);
        logService.info("删除了"+name+"指令");
        return flag;
    }

    @CrossOrigin
    @GetMapping()
    public JSONArray show(){
        return commandService.showAll();
    }

    @CrossOrigin
    @GetMapping("/{name}")
    public void sendTest(@PathVariable String name){
        JSONObject jsonCommand = new JSONObject();
        jsonCommand.put("name",name);
        mqFactory.createProducer().publish("run.command",jsonCommand.toString());
    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

}

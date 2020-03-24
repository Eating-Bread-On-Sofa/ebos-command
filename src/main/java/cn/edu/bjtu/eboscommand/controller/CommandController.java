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
            return "添加成功！";
        }else {
            return "添加失败！";
        }
    }

    @CrossOrigin
    @PostMapping("/recover")
    public String plus(@RequestBody JSONArray jsonArray){
        try {
            commandService.plusCommand(jsonArray);
            return "command已恢复";
        }catch (Exception e){ return e.toString();}
    }

    @CrossOrigin
    @DeleteMapping()
    public boolean delete(@RequestParam String name){
        boolean flag = commandService.deleteCommand(name);
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

    @CrossOrigin
    @RequestMapping ("/logtest")
    public String logtest1(){
        logService.info("command");
        return "成功";
    }
    @CrossOrigin
    @GetMapping("/logtest")
    public String logtest2(){
        return logService.findLogByCategory("info");
    }
}

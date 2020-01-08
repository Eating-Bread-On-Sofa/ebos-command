package cn.edu.bjtu.eboscommand.controller;

import cn.edu.bjtu.eboscommand.service.MqFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.eboscommand.service.CommandService;
import cn.edu.bjtu.eboscommand.util.LayuiTableResultUtil;
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
    @DeleteMapping()
    public boolean delete(@RequestParam String name){
        boolean flag = commandService.deleteCommand(name);
        return flag;
    }

    @CrossOrigin
    @GetMapping()
    public LayuiTableResultUtil<JSONArray> show(){
        JSONArray table = commandService.showAll();
        return new LayuiTableResultUtil<JSONArray>("",table,0,table.size());
    }


//    @JmsListener(destination = "run.command", containerFactory = "topicContainerFactory")
//    public void subscribeCommand(JSONObject msg) {
//        JSONObject fullMsg = commandService.find(msg.getString("name"));
//        switch (fullMsg.getIntValue("level")){
//            case 1:
//                sendCommand(fullMsg);
//                break;
//            case 2:
//                JSONArray array = fullMsg.getJSONArray("jsonArray");
//                for(int i = 0; i < array.size(); i++){
//                    JSONObject subMsg = commandService.find(array.getJSONObject(i).getString("name"));
//                    sendCommand(subMsg);
//                }
//                break;
//        }
//    }

    @CrossOrigin
    @GetMapping("/{name}")
    public void sendTest(@PathVariable String name){
        JSONObject jsonCommand = new JSONObject();
        jsonCommand.put("name",name);
        mqFactory.createProducer().publish("run.command",jsonCommand.toString());
    }

//    private void sendCommand(JSONObject command){
//        String url = "http://"+ip+":48082/api/v1/device/" + command.getString("deviceId")+ "/command/" + command.getString("commandId");
//        switch (command.getString("commandType")){
//            case "get":
//                try {
//                    JSONObject getObj = new JSONObject(restTemplate.getForObject(url, JSONObject.class));
//                    mqService.publish("show",getObj);
//                } catch (Exception e) {
//                    JSONObject err = new JSONObject();
//                    err.put("name",command.getString("name"));
//                    err.put("alert","失败！");
//                    mqService.publish("show",err);
//                }
//                break;
//            case "put":
//                restTemplate.put(url,command.getJSONObject("jsonObject"), String.class);
//                break;
//        }
//    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

}

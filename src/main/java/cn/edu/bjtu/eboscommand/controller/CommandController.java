package cn.edu.bjtu.eboscommand.controller;

import cn.edu.bjtu.eboscommand.entity.Command;
import cn.edu.bjtu.eboscommand.service.LogService;
import cn.edu.bjtu.eboscommand.service.MqFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.eboscommand.service.CommandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Api(tags = "指令管理")
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

    @ApiOperation(value = "查看所属网关设备所支持的指令",notes = "用于创建指令期间要选择设备及相关资源时，填充下拉菜单")
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
    public String add(@RequestBody Command info){
        boolean flag = commandService.addCommand(info);
        if(flag){
            logService.info("添加新指令"+info.getName());
            return "添加成功！";
        }else {
            logService.warn("添加指令失败");
            return "添加失败！";
        }
    }

    @CrossOrigin
    @PostMapping("/recover")
    public String plus(@RequestBody Command[] commands){
        try {
            commandService.plusCommand(commands);
            logService.info("command已成功恢复以下数据"+ Arrays.toString(commands));
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
    public List<Command> show(){
        return commandService.showAll();
    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

}

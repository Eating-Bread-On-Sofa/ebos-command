package cn.edu.bjtu.eboscommand.controller;

import cn.edu.bjtu.eboscommand.entity.Command;
import cn.edu.bjtu.eboscommand.model.ListedCommand;
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
import java.util.LinkedList;
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
    public List<ListedCommand> checkCommand(){
        String allUrl = "http://"+ip+":48082/api/v1/device";
        List<ListedCommand> listedCommands = new LinkedList<>();
        JSONArray all = new JSONArray(restTemplate.getForObject(allUrl,JSONArray.class));
        if(all.isEmpty()){
            listedCommands.add(new ListedCommand());
            return listedCommands;
        }
        for(int i = 0; i<all.size();i++) {
            JSONObject deviceObj = all.getJSONObject(i);
            String deviceId = deviceObj.getString("id");
            String deviceName = deviceObj.getString("name");
            JSONArray commandsArr = deviceObj.getJSONArray("commands");
            for(int j = 0; j < commandsArr.size();j++) {
                String commandId = commandsArr.getJSONObject(j).getString("id");
                String commandName = commandsArr.getJSONObject(j).getString("name");
                ListedCommand listedCommand = new ListedCommand();
                listedCommand.setCommandId(commandId);
                listedCommand.setCommandName(commandName);
                listedCommand.setDeviceId(deviceId);
                listedCommand.setDeviceName(deviceName);
                listedCommands.add(listedCommand);
            }
        }
        return listedCommands;
    }

    @ApiOperation(value = "向所属网关添加指令",notes = "所属网关为部署了本微服务的网关")
    @CrossOrigin
    @PostMapping()
    public String add(@RequestBody Command info){
        boolean flag = commandService.addCommand(info);
        if(flag){
            logService.info(null,"添加新指令"+info.getName());
            return "添加成功！";
        }else {
            logService.warn(null,"添加指令失败");
            return "添加失败！";
        }
    }

    @ApiOperation(value = "向所属网关恢复指令",notes = "与添加不同的是，其一可以批量恢复，其二添加是不允许重名，恢复时如果重名会选择覆盖策略")
    @CrossOrigin
    @PostMapping("/recover")
    public String plus(@RequestBody Command[] commands){
        try {
            commandService.plusCommand(commands);
            logService.info(null,"command已成功恢复以下数据"+ Arrays.toString(commands));
            return "command已恢复";
        }catch (Exception e){
            logService.error(null,e.toString());
            return e.toString();}
    }

    @ApiOperation(value = "向所属网关删除指令")
    @CrossOrigin
    @DeleteMapping()
    public boolean delete(@RequestParam String name){
        boolean flag = commandService.deleteCommand(name);
        logService.info(null,"尝试删除"+name+"指令："+flag);
        return flag;
    }

    @ApiOperation(value = "查看所属网关设备所有指令的详细信息", notes = "用于显示已有指令列表")
    @CrossOrigin
    @GetMapping()
    public List<Command> show(){
        return commandService.showAll();
    }

    @ApiOperation(value = "微服务运行检测", notes = "微服务正常运行时返回 pong")
    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

}

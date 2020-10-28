package cn.edu.bjtu.eboscommand.controller;

import cn.edu.bjtu.eboscommand.entity.Command;
import cn.edu.bjtu.eboscommand.model.ListedCommand;
import cn.edu.bjtu.eboscommand.service.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Api(tags = "网关指令")
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
    @Autowired
    SubscribeService subscribeService;

    public static final List<RawSubscribe> status = new LinkedList<>();
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 50,3, TimeUnit.SECONDS,new SynchronousQueue<>());

    @Value("${server.edgex}")
    private String ip ;

    @ApiOperation(value = "查看所属网关设备所支持的指令",notes = "用于创建指令期间要选择设备及相关资源时，填充下拉菜单")
    @CrossOrigin
    @GetMapping("/list")
    public List<ListedCommand> checkCommand(){
        String allUrl = "http://"+ip+":48082/api/v1/device";
        List<ListedCommand> listedCommands = new LinkedList<>();
        JSONArray all = restTemplate.getForObject(allUrl,JSONArray.class);
        if(all == null){
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
        System.out.println(info);
        boolean flag = commandService.addCommand(info);
        if(flag){
            logService.info("create","添加新指令"+info.getName());
            return "添加成功！";
        }else {
            logService.warn("create","添加指令失败");
            return "添加失败！";
        }
    }

    @ApiOperation(value = "向所属网关恢复指令",notes = "与添加不同的是，其一可以批量恢复，其二添加是不允许重名，恢复时如果重名会选择覆盖策略")
    @CrossOrigin
    @PostMapping("/recover")
    public String plus(@RequestBody Command[] commands){
        try {
            commandService.plusCommand(commands);
            logService.info("update","command已成功恢复以下数据"+ Arrays.toString(commands));
            return "command已恢复";
        }catch (Exception e){
            logService.error("update",e.toString());
            return "失败";}
    }

    @ApiOperation(value = "向所属网关删除指令")
    @CrossOrigin
    @DeleteMapping()
    public boolean delete(@RequestParam String name){
        boolean flag = commandService.deleteCommand(name);
        logService.info("delete","尝试删除"+name+"指令："+flag);
        return flag;
    }

    @ApiOperation(value = "查看所属网关设备所有指令的详细信息", notes = "用于显示已有指令列表")
    @CrossOrigin
    @GetMapping()
    public List<Command> show(){
        return commandService.showAll();
    }

    @ApiOperation(value = "微服务订阅mq的主题")
    @CrossOrigin
    @PostMapping("/subscribe")
    public String newSubscribe(RawSubscribe rawSubscribe){
        if(!CommandController.check(rawSubscribe.getSubTopic())){
            try{
                status.add(rawSubscribe);
                subscribeService.save(rawSubscribe.getSubTopic());
                threadPoolExecutor.execute(rawSubscribe);
                logService.info("create","指令管理成功订阅主题"+ rawSubscribe.getSubTopic());
                return "订阅成功";
            }catch (Exception e) {
                e.printStackTrace();
                logService.error("create","指令管理订阅主题"+rawSubscribe.getSubTopic()+"时，参数设定有误。");
                return "参数错误!";
            }
        }else {
            logService.error("create","运维监控已订阅主题"+rawSubscribe.getSubTopic()+",再次订阅失败");
            return "订阅主题重复";
        }
    }

    public static boolean check(String subTopic){
        boolean flag = false;
        for (RawSubscribe rawSubscribe : status) {
            if(subTopic.equals(rawSubscribe.getSubTopic())){
                flag=true;
                break;
            }
        }
        return flag;
    }

    @ApiOperation(value = "删除微服务订阅mq的主题")
    @CrossOrigin
    @DeleteMapping("/subscribe/{subTopic}")
    public boolean deleteTopic(@PathVariable String subTopic){
        boolean flag;
        synchronized (status){
            flag = status.remove(search(subTopic));
        }
        return flag;
    }

    public static RawSubscribe search(String subTopic){
        for (RawSubscribe rawSubscribe : status) {
            if(subTopic.equals(rawSubscribe.getSubTopic())){
                return rawSubscribe;
            }
        }
        return null;
    }

    @ApiOperation(value = "微服务向mq的某主题发布消息")
    @CrossOrigin
    @PostMapping("/publish")
    public String publish(@RequestParam(value = "topic") String topic,@RequestParam(value = "message") String message){
        MqProducer mqProducer = mqFactory.createProducer();
        mqProducer.publish(topic,message);
        return "发布成功";
    }

    @ApiOperation(value = "微服务运行检测", notes = "微服务正常运行时返回 pong")
    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

    @CrossOrigin
    @GetMapping("/test")
    public String testhhh(){
        JSONObject jo = new JSONObject();
        jo.put("name","testhhh");
        MqProducer mqProducer = mqFactory.createProducer();
        mqProducer.publish("run.command",jo.toString());
        return "成功";
    }

    @CrossOrigin
    @GetMapping("/testSend")
    public String testSSS(String name) {
        Command command = commandService.find(name);
        commandService.sendCommand(command);
        return "成功";
    }

}

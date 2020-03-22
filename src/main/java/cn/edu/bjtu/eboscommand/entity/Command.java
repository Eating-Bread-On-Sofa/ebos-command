package cn.edu.bjtu.eboscommand.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Command {
    @Id
    private String id;
    private String name;
    private String deviceId;
    private String deviceName;
    private String commandId;
    private String commandType;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private String commandName;
    private int level;
    private String description;

    public Command(){}

    public Command(JSONObject info){
        this.name = info.getString("name");
        this.commandId = info.getString("commandId");
        this.commandName = info.getString("commandName");
        this.deviceId = info.getString("deviceId");
        this.deviceName = info.getString("deviceName");
        this.commandType = info.getString("commandType");
        this.jsonObject = info.getJSONObject("jsonObject");
        this.jsonArray = info.getJSONArray("jsonArray");
        this.level = info.getIntValue("level");
        this.description = info.getString("description");
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",this.name);
        jsonObject.put("deviceId",this.deviceId);
        jsonObject.put("deviceName",this.deviceName);
        jsonObject.put("commandId",this.commandId);
        jsonObject.put("commandType", this.commandType);
        jsonObject.put("jsonObject", this.jsonObject);
        jsonObject.put("jsonArray",this.jsonArray);
        jsonObject.put("commandName",this.commandName);
        jsonObject.put("level",this.level);
        jsonObject.put("description",this.description);
        return jsonObject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

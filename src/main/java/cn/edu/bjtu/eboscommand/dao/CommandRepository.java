package cn.edu.bjtu.eboscommand.dao;

import cn.edu.bjtu.eboscommand.entity.Command;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends MongoRepository<Command, String> {
    Command findByName(String name);
    Command findByDeviceName(String name);
    Command findByDeviceId(String id);
    Command findByCommandId(String id);
    Command findByDeviceNameAndCommandType(String name, String type);
    Command findByDeviceNameAndCommandName(String device, String command);
    Command findByDeviceNameAndCommandTypeAndCommandName(String device, String type, String command);
}

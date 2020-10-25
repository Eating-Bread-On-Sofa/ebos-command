package cn.edu.bjtu.eboscommand.service;

import cn.edu.bjtu.eboscommand.entity.Command;

import java.util.List;

public interface CommandService {
    boolean addCommand(Command command);
    void plusCommand(Command[] commands);
    boolean deleteCommand(String name);
    List<Command> showAll();
    Command find(String name);
    void sendCommand(Command command);
}

package managers;

import commands.*;
import commands.network.Response;


import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;

public class Invoker {
    private static final HashMap<String, Command> commandMap = new HashMap<>();
    private Receiver receiver;
    public Invoker(CollectionManager collectionManager, DataBaseManager dataBaseManager){
        this.receiver = new Receiver(collectionManager, dataBaseManager);

        commandMap.put("help", new Help(receiver));
        commandMap.put("info", new Info(receiver));
        commandMap.put("show", new Show(receiver));
        commandMap.put("add", new Add(receiver));
        commandMap.put("update", new Update(receiver));
        commandMap.put("remove_by_id", new RemoveById(receiver));
        commandMap.put("clear", new Clear(receiver));
        //commandMap.put("execute_script", new ExecuteScript(receiver));
        commandMap.put("add_if_min", new AddIfMin(receiver));
        commandMap.put("remove_greater", new RemoveGreater(receiver));
        commandMap.put("remove_lower", new RemoveLower(receiver));
        commandMap.put("filter_by_car", new FilterByCar(receiver));
        commandMap.put("print_unique_car", new PrintUniqueCar(receiver));
        commandMap.put("print_field_descending_mood", new PrintFieldDescendingMood(receiver));
        commandMap.put("connect", new Connect());
        commandMap.put("login", new Login(receiver));
        commandMap.put("register", new Register(receiver));
    }

    public Response execute(String commandName, String[] args, Object obj, User user) throws IOException {
        Command command = commandMap.get(commandName);
        Response response = command.execute(args, obj, user);
        return response;
    }
    public Response execute(String commandName, String[] args, Object obj) throws IOException {
        Command command = commandMap.get(commandName);
        Response response = command.execute(args, obj, null);
        return response;
    }
    public Response execute(String commandName, String[] args) throws  IOException {
        Command command = commandMap.get(commandName);
        Response response = command.execute(args, null, null);
        return response;
    }

    public static HashMap<String, Command> getCommandMap(){
        return commandMap;
    }

}

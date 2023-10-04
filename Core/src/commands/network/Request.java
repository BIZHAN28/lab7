package commands.network;

import managers.User;

import java.io.Serializable;


public class Request implements Serializable {
    private final String commandName;
    private final String[] arguments;
    private final Object object;
    private String username;
    private byte[] password;

    public Request(String commandName, String[] arguments, Object object, User user) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.object = object;
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(byte[] password){
        this.password = password;
    }

    public byte[] getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArguments() {
        return arguments;
    }

    public Object getElement() {
        return object;
    }
}

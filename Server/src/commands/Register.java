package commands;

import commands.network.CommandDescription;
import commands.network.Response;
import managers.Invoker;
import managers.Receiver;
import managers.User;
import validation.CommandInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class Register implements Command{
    private final Receiver receiver;
    public Register(Receiver receiver) {
        this.receiver = receiver;
    }
    @Override
    public Response execute(String [] args, Object obj, User user) {
        return receiver.register(args, obj, user);
    }
}

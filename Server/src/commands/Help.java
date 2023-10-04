package commands;

import commands.network.Response;
import managers.Receiver;
import managers.User;
import validation.CommandInfo;

@CommandInfo(name = "Help")
public class Help implements Command{
    private final Receiver receiver;

    public Help(Receiver receiver){
        this.receiver = receiver;
    }

    @Override
    public Response execute(String[] args, Object obj, User user) {
        return receiver.help(args);
    }
}

package commands;

import commands.network.Response;
import managers.Receiver;
import managers.User;
import validation.CommandInfo;

@CommandInfo(name = "Info")
public class Info implements Command{
    private final Receiver receiver;
    public Info(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public Response execute(String[] args, Object obj, User user) {
        return receiver.info(args);
    }
}

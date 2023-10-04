package commands;

import commands.network.Response;
import managers.Receiver;
import managers.User;
import validation.CommandInfo;

@CommandInfo(name = "Show")
public class Show implements Command{
    private final Receiver receiver;

    public Show(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public Response execute(String[] args, Object object, User user) {
        return receiver.show(args);
    }
}

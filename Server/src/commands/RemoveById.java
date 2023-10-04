package commands;

import commands.network.Response;
import managers.Receiver;
import managers.User;
import validation.CommandInfo;

@CommandInfo(name = "Remove_By_Id", argsCount = 1)
public class RemoveById implements Command{
    private final Receiver receiver;
    public RemoveById(Receiver receiver) {
        this.receiver = receiver;
    }
    @Override
    public Response execute(String[] args, Object obj, User user) {
        return receiver.removeById(args, user);
    }
}

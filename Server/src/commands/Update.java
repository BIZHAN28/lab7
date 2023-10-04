package commands;

import commands.network.Response;
import managers.Receiver;
import managers.User;
import models.HumanBeing;
import validation.CommandInfo;

@CommandInfo(name = "Update", argsCount = 1, requiredObjectType = HumanBeing.class)
public class Update implements Command{
    private final Receiver receiver;

    public Update(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public Response execute(String[] args, Object obj, User user) {
        return receiver.update(args, obj, user);
    }
}

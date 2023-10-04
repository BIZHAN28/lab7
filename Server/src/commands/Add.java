package commands;

import commands.network.Response;
import managers.Receiver;
import managers.User;
import models.HumanBeing;
import validation.CommandInfo;

@CommandInfo(name="Add", requiredObjectType = HumanBeing.class)
public class Add implements Command{
    private final Receiver receiver;

    public Add(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public Response execute(String[] args, Object obj, User user) {
        return receiver.add(args, obj, user);
    }
}

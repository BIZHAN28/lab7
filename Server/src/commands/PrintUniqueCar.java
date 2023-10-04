package commands;

import commands.network.Response;

import managers.Receiver;
import managers.User;
import validation.CommandInfo;

@CommandInfo(name = "Print_Unique_Car")
public class PrintUniqueCar implements Command{
    private final Receiver receiver;
    public PrintUniqueCar(Receiver receiver){
        this.receiver = receiver;
    }
    @Override
    public Response execute(String[] args, Object obj, User user) {
        return receiver.printUniqueCar(args);
    }
}

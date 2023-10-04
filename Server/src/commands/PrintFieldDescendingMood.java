package commands;

import commands.network.Response;
import managers.Receiver;
import managers.User;
import validation.CommandInfo;

@CommandInfo(name = "Print_Field_Descending_Mood")
public class PrintFieldDescendingMood implements Command{
    private Receiver receiver;
    public PrintFieldDescendingMood(Receiver receiver){
        this.receiver = receiver;
    }
    @Override
    public Response execute(String[] args, Object obj, User user) {
        return receiver.printFieldDescendingMood(args);
    }
}

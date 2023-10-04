import managers.DataBaseManager;
import managers.Invoker;
import commands.network.Request;
import commands.network.Response;
import managers.CollectionManager;
import commands.ScriptStack;
import managers.User;

import java.io.IOException;
import java.util.Objects;

public class CommandProcessor {
    private CollectionManager collectionManager;
    private Invoker invoker;

    public CommandProcessor(CollectionManager collectionManager,DataBaseManager dataBaseManager) {
        this.collectionManager = collectionManager;
        this.invoker = new Invoker(collectionManager, dataBaseManager);
        dataBaseManager.loadUsersFromDB();
        collectionManager.setHumanBeings(dataBaseManager.loadFromDB());
    }

    public Response processRequest(Request request) {
        try {
            if (Objects.equals(request.getCommandName(), "next")){
                return new Response("next");
            }
            return invoker.execute(request.getCommandName(), request.getArguments(), request.getElement(),new User(request.getUsername(),request.getPassword()));
        } catch (IOException ignored){
            return  null;
        }
    }
}

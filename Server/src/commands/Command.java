package commands;

import commands.network.Response;
import managers.User;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public interface Command {
    Response execute(String[] args, Object obj, User user) throws IOException;

}

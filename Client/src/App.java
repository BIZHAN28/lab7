import commands.network.CommandDescription;
import commands.network.Request;
import commands.network.Response;
import exceptions.RecursiveException;
import managers.AskManager;
import managers.ConsoleInput;
import managers.SerializeManager;
import managers.User;
import models.HumanBeing;

import java.io.*;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

public class App {
    public static void main(String[] args) {
        while (true) {
            try {
                ScriptStack scriptStack = new ScriptStack();
                InetAddress host = InetAddress.getLocalHost();
                int port = 6789;
                Socket sock = new Socket(host, port);
                OutputStream os = sock.getOutputStream();
                InputStream is = sock.getInputStream();
                RequestSender requestSender = new RequestSender(os);
                ResponseReader responseReader = new ResponseReader(2684780,is);
                Response response;
                ConsoleInput consoleInput = new ConsoleInput();


                AuthorizationManager authorizationManager = new AuthorizationManager(consoleInput, requestSender, responseReader);

                User user = authorizationManager.ask();
                requestSender.sendRequest(new Request("connect", null, null, user));
                response = responseReader.readResponse();
                if (Objects.equals(response.getMessage(), "The server is currently busy. Come back later.")) {
                    System.out.println("The server is currently busy. Come back later.");
                    sock.close();
                    System.exit(0);
                }

                requestSender.sendRequest(new Request("next", null, null, user));
                responseReader.readResponse();
                ArrayList<CommandDescription> commandDescriptions = (ArrayList<CommandDescription>) (response.getObject());
                HashMap<String, CommandDescription> commands = new HashMap<>();
                for (CommandDescription commandDescription : commandDescriptions) {
                    commands.put(commandDescription.getName().toLowerCase(), commandDescription);
                }
                //responseReader.readResponse();

                System.out.println("\n" +
                        "██╗      █████╗ ██████╗  ██████╗ \n" +
                        "██║     ██╔══██╗██╔══██╗██╔════╝ \n" +
                        "██║     ███████║██████╔╝███████╗ \n" +
                        "██║     ██╔══██║██╔══██╗██╔═══██╗\n" +
                        "███████╗██║  ██║██████╔╝╚██████╔╝\n" +
                        "╚══════╝╚═╝  ╚═╝╚═════╝  ╚═════╝ \n" +
                        "                                 \n");
                System.out.println("Welcome to the program");
                System.out.println("Type \"help\" for a list of commands");

                AskManager askManager = new AskManager(consoleInput);
                String message = "#end";

                while (true) {
                    if (Objects.equals(message, "#end")) {
                        String s = consoleInput.handleInput().nextLine().trim();
                        String[] spl = s.split(" ", 2);
                        if (commands.containsKey(spl[0].toLowerCase())) {
                            Object obj = null;
                            if (commands.get(spl[0]).getRequiredObjectType() != Void.class) {
                                obj = askManager.ask(commands.get(spl[0]).getRequiredObjectType().getConstructor().newInstance());
                            }
                            requestSender.sendRequest(new Request(spl[0], Arrays.copyOfRange(spl, 1, spl.length), obj, user));
                            response = responseReader.readResponse();
                            message = (response.getMessage());

                            if (!Objects.equals(message, "#end")) {
                                System.out.println(message);
                            }
                        } else if (spl[0].equalsIgnoreCase("exit")) {
                            System.exit(0);
                        } else if (spl[0].equalsIgnoreCase("logout")){
                            throw new RuntimeException();
                        } else if (spl[0].equalsIgnoreCase("execute_script")) {
                            try {
                                List<Request> requests = ScriptExecutor.execute(spl[1], commands, scriptStack, user);
                                for (Request request : requests) {
                                    requestSender.sendRequest(request);
                                    response = responseReader.readResponse();
                                    message = response.getMessage();
                                    if (!Objects.equals(message, "#end")) {
                                        System.out.println(message);
                                    }
                                }
                            } catch (RecursiveException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("This command is not recognized, try again");
                        }
                    } else {
                        Request request = new Request("next", null, null, user);
                        requestSender.sendRequest(request);
                        response = responseReader.readResponse();
                        message = response.getMessage();
                        if (!Objects.equals(message, "#end")) {
                            System.out.println(message);
                        }
                    }
                }

            } catch (ConnectException e) {
                System.out.println("Server is unavailable. Please wait.");
            } catch (NoSuchElementException e){
                
                System.exit(0);
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //e.printStackTrace();
            }
        }
    }
}
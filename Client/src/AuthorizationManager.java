import commands.network.Request;
import commands.network.Response;
import managers.ConsoleInput;
import managers.User;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class AuthorizationManager {
    private final ConsoleInput consoleInput;
    private String username;
    private byte[] password;
    private final RequestSender requestSender;
    private final ResponseReader responseReader;
    public AuthorizationManager(ConsoleInput consoleInput, RequestSender requestSender, ResponseReader responseReader){
        this.consoleInput = consoleInput;
        this.responseReader = responseReader;
        this.requestSender = requestSender;
    }
    public User ask() throws IOException {
        String answer = "";
        while (!answer.equals("1") && !answer.equals("0")){
            System.out.println("Do you want to login or register? (0 for login, 1 for register)");
            answer = consoleInput.handleInput().nextLine().trim();
        }
        if (answer.equals("1")){
            return register();
        } else {
            return login();
        }
    }
    public User login() throws IOException {
        String message = "";
        while (!Objects.equals(message, "signed")) {
            username = askUsername();
            password = askPassword();
            Request request = new Request("login", null, null, new User(username,password));
            message = getMessage(request);
            if (!Objects.equals(message, "signed")){
                System.out.println(message);
            }
        }
        return new User(username, password);
    }
    public User register() throws IOException {
        String message = "";
        while (!Objects.equals(message, "registered")) {
            username = askUsername();
            password = askPassword();
            Request request = new Request("register", null, null, new User(username,password));
            message = getMessage(request);
            if (!Objects.equals(message, "registered")){
                System.out.println(message);
            }
        }
        return new User(username, password);
    }

    private String getMessage(Request request) throws IOException {
        String message;
        requestSender.sendRequest(request);
        Response response = responseReader.readResponse();
        if (Objects.equals(response.getMessage(), "The server is currently busy. Come back later.")) {
            System.out.println("The server is currently busy. Come back later.");
            System.exit(0);
        }
        requestSender.sendRequest(new Request("next",null,null, new User(username,password)));
        responseReader.readResponse();
        message = response.getMessage();
        return message;
    }

    private String askUsername(){
        System.out.println("Enter username:");
        return consoleInput.handleInput().nextLine().trim();
    }
    private byte[] askPassword(){
        System.out.println("Enter password:");
        String password = consoleInput.handleInput().nextLine().trim();
        try {
            return MessageDigest.getInstance("SHA-256").digest(password.getBytes());
        } catch (NoSuchAlgorithmException ignored){}
        return null;
    }
}

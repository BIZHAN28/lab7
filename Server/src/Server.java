public class Server {
    public static void main(String[] args) {
        try {
            int port = 6789;

            ConnectionHandler connectionHandler = new ConnectionHandler(port);

            while (true) {
                connectionHandler.acceptConnections();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import commands.network.Request;
import commands.network.Response;
import managers.CollectionManager;
import managers.DataBaseManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class ConnectionHandler {
    private ServerSocketChannel serverSocketChannel;
    private List<SocketChannel> connectedClients;

    public ConnectionHandler(int port) throws IOException {
        InetAddress host = InetAddress.getLocalHost();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        connectedClients = new ArrayList<>();
    }

    public void acceptConnections() throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            connectedClients.add(socketChannel);
            new Thread(() -> handleConnection(socketChannel)).start();
        }
    }

    private void handleConnection(SocketChannel socketChannel) {
        try {
            RequestReader requestReader = new RequestReader(100000);
            CommandProcessor commandProcessor = new CommandProcessor(new CollectionManager(), new DataBaseManager());
            ResponseSender responseSender = new ResponseSender();

            while (true) {
                try {
                    Request request = requestReader.readRequest(socketChannel);
                    if (request != null) {
                        // Многопоточная обработка запроса с использованием ForkJoinPool
                        ForkJoinPool.commonPool().submit(() -> {
                            Response response = commandProcessor.processRequest(request);
                            // Многопоточная отправка ответа с использованием нового потока
                            new Thread(() -> {
                                try {
                                    responseSender.sendResponse(socketChannel, response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        });
                    }
                } catch (IOException e) {
                    removeConnection(socketChannel);
                    break;  // Выход из цикла, если возникла ошибка чтения
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeConnection(SocketChannel socketChannel) {
        if (connectedClients.contains(socketChannel)) {
            try {
                socketChannel.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            connectedClients.remove(socketChannel);
        }
    }

    public List<SocketChannel> getConnectedClients() {
        return connectedClients;
    }

    public void close() throws IOException {
        serverSocketChannel.close();
    }
}

import commands.network.Request;
import commands.network.Response;
import managers.SerializeManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class RequestSender {

    private final OutputStream os;

    public RequestSender(OutputStream os) {
        this.os = os;
    }

    public void sendRequest(Request request) throws IOException {
        os.flush();
        os.write(SerializeManager.serialize(request));
//        if (responseId == 0){
//            responseMessage = response.getMessage();
//        }
//        if (responseId == responseMessage.split("\n").length){
//            socketChannel.write(ByteBuffer.wrap(SerializeManager.serialize(new Response("#end"))));
//            responseId = 0;
//        }else {
//            String s = responseMessage.split("\n")[responseId];
//            byte[] responseData = SerializeManager.serialize(new Response(s, response.getObject()));
//            ByteBuffer buffer = ByteBuffer.wrap(responseData);
//            socketChannel.write(buffer);
//            responseId++;
//        }

    }
}

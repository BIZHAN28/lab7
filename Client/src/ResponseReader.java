import commands.network.Response;
import managers.SerializeManager;

import java.io.IOException;
import java.io.InputStream;

public class ResponseReader {
    private final int dataSize;
    private final InputStream is;
    public ResponseReader(int byteMassiveSize, InputStream is){
        dataSize = byteMassiveSize;
        this.is = is;
    }
    public Response readResponse() throws IOException{
        byte[] data = new byte[dataSize];
        is.read(data);
        return (Response) SerializeManager.deserialize(data);
    }
}

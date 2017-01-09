package tools;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Miu on 09/01/2017.
 */
public class DataSender {

    public static void send(Socket socket, int OpCode, String data) throws IOException {
        //Copied from Packet's send implementation, using two buffers.
        ByteArrayDataOutput preBuffer = ByteStreams.newDataOutput();
        ByteArrayDataOutput postBuffer = ByteStreams.newDataOutput();
        postBuffer.writeInt(OpCode);
        char[] charArray = data.toCharArray();
        int length = charArray.length;
        postBuffer.writeInt(length);
        for (char aCharArray : charArray) {
            postBuffer.writeChar(aCharArray);
        }
        preBuffer.writeInt(postBuffer.toByteArray().length);
        preBuffer.write(postBuffer.toByteArray());
        socket.getOutputStream().write(preBuffer.toByteArray());
        socket.getOutputStream().flush();
    }
}

package testing.tools;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

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

    public static ByteBuffer simulatePacket(int OpCode, String data){
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

        //Can't use wrap here because of remlaining = 0 after wraping.
        byte[] finalData = preBuffer.toByteArray();
        ByteBuffer result = ByteBuffer.allocate(finalData.length);
        result.put(finalData);
        return result;
    }
}

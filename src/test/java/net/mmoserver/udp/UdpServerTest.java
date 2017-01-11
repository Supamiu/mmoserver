package net.mmoserver.udp;

import net.mmoserver.common.Config;
import org.junit.Test;
import testing.tools.DataSender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Miu on 11/01/2017.
 */
public class UdpServerTest {

    @Test
    public void udpTest() throws IOException {
        Config.datagramPort = 58009;
        UdpServer server = new UdpServer();
        assertNotNull(server.getChannel());

        DatagramSocket socket = new DatagramSocket();

        ByteBuffer data = DataSender.simulatePacket(1, "Testing");
        Config.datagramBlockSize = data.array().length;
        DatagramPacket packet = new DatagramPacket(data.array(), data.array().length,
                InetAddress.getByName("127.0.0.1"), 58009);

        socket.send(packet);

        //It's pretty hard to test UdpServer since it's not very well implemented at the moment.
        //I think it'll be more relevant to test it once the real implementation process will have started.
    }
}

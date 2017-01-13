package net.mmoserver.udp;

import net.mmoserver.common.Config;
import net.mmoserver.common.Session;
import net.mmoserver.packet.Packet;
import org.junit.After;
import org.junit.Test;
import testing.mock.MockPacket;
import testing.tools.DataSender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

/**
 * Created by Miu on 11/01/2017.
 */
public class UdpServerTest {

    @Test
    public void udpTest() throws IOException, InterruptedException {
        Packet mockPacket = mock(MockPacket.class);
        Packet.add(mockPacket);

        SelectionKey mockKey = mock(SelectionKey.class);
        Session session = new Session(mockKey);
        UUID uuid = session.getSessionKey();

        Config.datagramPort = 58009;
        UdpServer server = new UdpServer();
        assertNotNull(server.getChannel());

        ByteBuffer mockPacketData = DataSender.simulateUdpPacket(10, uuid, "foo");
        DatagramPacket mockPacketPacket = new DatagramPacket(mockPacketData.array(), mockPacketData.array().length,
                InetAddress.getByName("127.0.0.1"), 58009);

        DatagramSocket socket2 = new DatagramSocket();

        socket2.send(mockPacketPacket);

        Thread.sleep(100);

        assertEquals(1, mockingDetails(mockPacket).getInvocations().size());
        server.stop();
    }

    @Test
    public void errorTest() throws IOException {
        //Here we expect the packet not found exception to be handled properly and thus not thrown here.

        SelectionKey mockKey = mock(SelectionKey.class);
        Session session = new Session(mockKey);
        UUID uuid = session.getSessionKey();

        Config.datagramPort = 58009;
        UdpServer server = new UdpServer();
        assertNotNull(server.getChannel());

        DatagramSocket socket = new DatagramSocket();

        ByteBuffer data = DataSender.simulateUdpPacket(1, uuid, "Testing");
        DatagramPacket packet = new DatagramPacket(data.array(), data.array().length,
                InetAddress.getByName("127.0.0.1"), 58009);

        socket.send(packet);
    }
}

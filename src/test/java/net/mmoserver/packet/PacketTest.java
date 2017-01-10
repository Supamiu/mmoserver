package net.mmoserver.packet;

import net.mmoserver.common.Config;
import net.mmoserver.common.Session;
import org.junit.Before;
import org.junit.Test;
import testing.mock.MockPacket;
import testing.tools.DataSender;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Miu on 10/01/2017.
 */
public class PacketTest {

    @Before
    public void before() {
        Config.enableUDP = false;
        Session.getSessionMap().clear();
        Session.getSessions().clear();
        Session.bytesOutCurrent = 0;
        Session.bytesOut = 0;
    }

    @Test
    public void basicTest(){
        new Packet() {
            @Override
            public void decodeTcp(Session session) throws IOException {
                //This is just a test to see if I can extends packet with no problems.
            }

            @Override
            public void decodeUdp(Session session, ByteBuffer udpBuffer) throws IOException {

            }
        };
    }

    @Test
    public void packetRegisterTest() throws IllegalArgumentException {
        Packet mockPacket = mock(MockPacket.class);
        Packet.add(mockPacket);
        assertEquals(1, Packet.getPackets().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void packetRegisterErrorTest() throws IllegalArgumentException {
        Packet mockPacket = mock(Packet.class);
        Packet.add(mockPacket);
    }

    @Test(expected = PacketNotFoundException.class)
    public void tcpDecodeNotFoundTest() throws IOException, PacketNotFoundException {
        Session session = mock(Session.class);
        when(session.getInputBuffer()).thenReturn(DataSender.simulatePacket(1, "Testing"));
        //This should throw a PacketNotFoundException exception.
        Packet._decode(session);
    }

    @Test
    public void tcpDecodeTest() throws IOException, PacketNotFoundException, IllegalArgumentException {
        Packet mockPacket = mock(MockPacket.class);
        Packet.add(mockPacket);

        Session session = mock(Session.class);
        when(session.getInputBuffer()).thenReturn(DataSender.simulatePacket(10, "Testing"));
        //This time we do not expect the exception, as the packet is already known.
        Packet._decode(session);

        //We expect the decode method of our packet with opCode 10 to have been called once.
        assertEquals(1, mockingDetails(mockPacket).getInvocations().size());
    }

    @Test(expected = PacketNotFoundException.class)
    public void udpDecodeNotFoundTest() throws IOException, PacketNotFoundException {
        Session session = mock(Session.class);
        //This should throw a PacketNotFoundException exception.
        Packet._decode(session, 1, DataSender.simulatePacket(1, "Testing"));
    }

    @Test
    public void udpDecodeTest() throws IOException, PacketNotFoundException, IllegalArgumentException {
        Packet mockPacket = mock(MockPacket.class);
        Packet.add(mockPacket);

        Session session = mock(Session.class);
        SocketChannel channel = mock(SocketChannel.class);
        when(session.getChannel()).thenReturn(channel);
        //This time we do not expect the exception, as the packet is already known.
        Packet._decode(session, 10, DataSender.simulatePacket(10, "Testing"));

        //We expect the decode method of our packet with opCode 10 to have been called once.
        assertEquals(1, mockingDetails(mockPacket).getInvocations().size());
    }

    @Test
    public void sendTCPTest() throws IOException {
        Session session = mock(Session.class);
        SocketChannel channel = mock(SocketChannel.class);
        when(session.getChannel()).thenReturn(channel);
        ByteBuffer expectedData = DataSender.simulatePacket(20, "Test send");
        when(channel.write(any(ByteBuffer.class))).thenReturn(expectedData.array().length);

        Packet.send(Packet.PacketType.TCP, session, 20, "Test send");

        assertEquals(expectedData.array().length, Session.bytesOut);
        verify(channel, times(1)).write(any(ByteBuffer.class));

        Packet.send(Packet.PacketType.TCP, session, 20, (int) 50);
        Packet.send(Packet.PacketType.TCP, session, 20, 50L);
        Packet.send(Packet.PacketType.TCP, session, 20, (char)'a');
        Packet.send(Packet.PacketType.TCP, session, 20, 50f);
        Packet.send(Packet.PacketType.TCP, session, 20, 50D);
        Packet.send(Packet.PacketType.TCP, session, 20, 0x50);
        Packet.send(Packet.PacketType.TCP, session, 20, false);
        Packet.send(Packet.PacketType.TCP, session, 20, true);
        Packet.send(Packet.PacketType.TCP, session, 20, (byte)8);

        verify(channel, times(10)).write(any(ByteBuffer.class));
    }

    @Test
    public void sendUDPTest() throws IOException {
        Config.enableUDP = true;

        Session session = mock(Session.class);

        UUID uuid = UUID.randomUUID();

        SocketAddress address = mock(SocketAddress.class);
        when(address.toString()).thenReturn("127.0.0.1");
        SocketChannel channel = mock(SocketChannel.class);
        when(channel.getRemoteAddress()).thenReturn(address);

        when(session.getChannel()).thenReturn(channel);
        when(session.getSessionKey()).thenReturn(uuid);

        Packet.send(Packet.PacketType.UDP, session, 20, "Test send");

        //Nothing to assert here, just no exception and we're fine.
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendUDPTestError() throws IOException, IllegalArgumentException {
        Config.enableUDP = false;
        Session session = mock(Session.class);

        UUID uuid = UUID.randomUUID();

        SocketAddress address = mock(SocketAddress.class);
        when(address.toString()).thenReturn("127.0.0.1");
        SocketChannel channel = mock(SocketChannel.class);
        when(channel.getRemoteAddress()).thenReturn(address);

        when(session.getChannel()).thenReturn(channel);
        when(session.getSessionKey()).thenReturn(uuid);

        Packet.send(Packet.PacketType.UDP, session, 20, "Test send");

        //Nothing to assert here, just no exception and we're fine.
    }

    @Test
    public void sendGlobalTest() throws IOException {
        Config.enableUDP = true;
        final SelectionKey key = mock(SelectionKey.class);
        final SelectionKey key2 = mock(SelectionKey.class);

        SocketAddress address = mock(SocketAddress.class);
        when(address.toString()).thenReturn("127.0.0.1");
        SocketChannel channel = mock(SocketChannel.class);
        when(channel.getRemoteAddress()).thenReturn(address);

        when(key.channel()).thenReturn(channel);
        when(key2.channel()).thenReturn(channel);

        new Session(key);
        new Session(key2);

        ByteBuffer expectedData = DataSender.simulatePacket(20, "Test send");
        when(channel.write(any(ByteBuffer.class))).thenReturn(expectedData.array().length);

        Packet.sendGlobal(Packet.PacketType.TCP, 20, "Test send");
        Packet.sendGlobal(Packet.PacketType.UDP, 20, "Test send");

        assertEquals(expectedData.array().length * 2, Session.bytesOut);
        verify(channel, times(4)).write(any(ByteBuffer.class));
    }

    @Test
    public void sendGlobalTestException() throws IOException {
        SelectionKey key = mock(SelectionKey.class);
        SelectionKey key2 = mock(SelectionKey.class);

        SocketAddress address = mock(SocketAddress.class);
        when(address.toString()).thenReturn("127.0.0.1");
        SocketChannel channel = mock(SocketChannel.class);
        when(channel.getRemoteAddress()).thenReturn(address);
        //Even if we throw an exception, it should not be caught here.
        when(channel.write(any(ByteBuffer.class))).thenThrow(new IOException("Testing exceptions"));

        when(key.channel()).thenReturn(channel);
        when(key2.channel()).thenReturn(channel);

        new Session(key);
        new Session(key2);

        Packet.sendGlobal(Packet.PacketType.TCP, 20, "Test send");
    }
}

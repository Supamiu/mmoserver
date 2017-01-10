package net.mmoserver.packet;

import net.mmoserver.common.Session;
import org.junit.Test;
import testing.mock.MockPacket;
import testing.tools.DataSender;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Miu on 10/01/2017.
 */
public class PacketTest {

    @Test
    public void packetRegisterTest() {
        Packet mockPacket = mock(MockPacket.class);
        Packet.add(mockPacket);
        assertEquals(1, Packet.getPackets().size());
    }

    @Test(expected = PacketNotFoundException.class)
    public void tcpDecodeNotFoundTest() throws IOException, PacketNotFoundException {
        Session session = mock(Session.class);
        when(session.getInputBuffer()).thenReturn(DataSender.simulatePacket(1, "Testing"));
        //This should throw a PacketNotFoundException exception.
        Packet._decode(session);
    }

    @Test
    public void tcpDecodeTest() throws IOException, PacketNotFoundException {
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
    public void udpDecodeTest() throws IOException, PacketNotFoundException {
        Packet mockPacket = mock(MockPacket.class);
        Packet.add(mockPacket);

        Session session = mock(Session.class);
        //This time we do not expect the exception, as the packet is already known.
        Packet._decode(session, 10, DataSender.simulatePacket(10, "Testing"));

        //We expect the decode method of our packet with opCode 10 to have been called once.
        assertEquals(1, mockingDetails(mockPacket).getInvocations().size());
    }

    @Test
    public void sendTest() {
        //TODO
    }
}

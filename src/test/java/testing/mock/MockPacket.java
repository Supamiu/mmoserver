package testing.mock;

import io.ygg.common.Session;
import io.ygg.packet.Packet;
import io.ygg.packet.PacketOpcode;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Miu on 10/01/2017.
 */
@PacketOpcode(10)
public class MockPacket extends Packet {

    @Override
    public void decodeTcp(Session session) throws IOException {
    }

    @Override
    public void decodeUdp(Session session, ByteBuffer udpBuffer) throws IOException {
    }
}

package net.mmoserver.common;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Miu on 11/01/2017.
 */
public class SessionTest {


    @AfterClass
    public static void after() {
        Config.enableUDP = false;
        Session.getSessions().clear();
    }

    @Test
    public void UDPTest() throws IOException {
        Config.enableUDP = true;

        SelectionKey key = mock(SelectionKey.class);
        SocketChannel channel = mock(SocketChannel.class);
        when(key.channel()).thenReturn(channel);

        Session session = new Session(key);

        assertNotNull(session);
    }

    @Test
    public void attachTest() throws IOException {
        SelectionKey key = mock(SelectionKey.class);
        SocketChannel channel = mock(SocketChannel.class);
        when(key.channel()).thenReturn(channel);

        Session session = new Session(key);

        session.attach("Testing");
        assertEquals("Testing", session.getAttachment());
    }
}

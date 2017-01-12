package net.mmoserver.tcp;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by flavien on 12/01/17.
 */
public class TcpProcessorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    @AfterClass
    public static void afterAll() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
    }

    @Test
    public void selectorClosed() {
        Selector selector = mock(Selector.class);
        when(selector.isOpen()).thenReturn(false);

        ServerSocketChannel channel = mock(ServerSocketChannel.class);

        TcpProcessor processor = new TcpProcessor(selector, channel);

        processor.run();

        assertEquals("[ERROR]: Failure to initialize the server, perhaps the socket or selector is closed?"
                + System.getProperty("line.separator"), errContent.toString());
    }
}

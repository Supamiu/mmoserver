package net.yggdrasil.common;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Miu on 11/01/2017.
 */
public class LogTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        Config.debugging = true;
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
    public void constructorTest(){
        //Even if we don't construct it, it might be good to know if it can't be created.
        Log log = new Log();
        assertNotNull(log);
    }

    @Test
    public void info() {
        Log.info("test");
        assertEquals("[INFO]: test" + System.getProperty("line.separator"), outContent.toString());
    }

    @Test
    public void error() {
        Log.error("test");
        assertEquals("[ERROR]: test" + System.getProperty("line.separator"), errContent.toString());
    }

    @Test
    public void warning() {
        Log.warning("test");
        assertEquals("[WARNING]: test" + System.getProperty("line.separator"), outContent.toString());
    }

    @Test
    public void debug() {
        Log.debug("test");
        assertEquals("[DEBUG]: test" + System.getProperty("line.separator"), outContent.toString());
        Config.debugging = false;
        Log.debug("test");
        assertEquals("[DEBUG]: test" + System.getProperty("line.separator"), outContent.toString());
    }
}

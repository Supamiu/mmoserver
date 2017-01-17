package io.ygg.common;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Miu on 11/01/2017.
 */
public class ConfigTest {

    @Test
    public void simpleConstructorTest() {
        Config config = new Config();
        assertNotNull(config);
    }
}

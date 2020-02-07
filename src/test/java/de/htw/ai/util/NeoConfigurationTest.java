package de.htw.ai.util;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class NeoConfigurationTest {

    NeoConfiguration config;
    String[] testArgs = new String[]{"-config", "src/test/resources/configexample.txt"};

    @BeforeEach
    public void beforeEach() {
        config = new NeoConfiguration();
    }

    @Test
    public void argsTest() {
        Assertions.assertDoesNotThrow(() -> config.parse(testArgs));

        Assertions.assertThrows(ParseException.class, () -> config.parse(new String[] {"-unknown", "value"}));
    }

    @Test
    public void getConfigValueTest() throws IOException, ParseException {
        config.parse(testArgs);

        Assertions.assertEquals("test", config.getConfigValue("test"));
        Assertions.assertEquals("test1", config.getConfigValue("test1"));
    }
}

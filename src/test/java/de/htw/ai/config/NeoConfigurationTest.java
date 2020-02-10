package de.htw.ai.config;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NeoConfigurationTest {

    private String configPath = "src/test/resources/configexample.txt";
    NeoConfiguration config;
    String[] testArgs = new String[]{"-config", configPath};

    @BeforeEach
    public void beforeEach() {
        config = new NeoConfiguration();
    }

    @AfterAll
    public void afterAll() throws IOException {
        Files.delete(Paths.get(configPath));
    }

    @Test
    public void argsTest() {
        Assertions.assertDoesNotThrow(() -> config.parse(testArgs));

        Assertions.assertThrows(ParseException.class, () -> config.parse(new String[]{"-unknown", "value"}));
    }

    @Test
    public void getConfigValueTest() throws IOException, ParseException {
        config.parse(testArgs);

        Assertions.assertEquals("test", config.getConfigValue("test"));
        Assertions.assertEquals("test1", config.getConfigValue("test1"));
    }
}

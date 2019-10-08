package com.edilson.justiniano.kalah.unit.configuration;

import com.edilson.justiniano.kalah.configuration.JsonMapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link JsonMapperConfig} class
 */
@RunWith(JUnit4.class)
public class JsonMapperConfigTest {

    private JsonMapperConfig jsonMapperConfig = new JsonMapperConfig();

    @Test
    public void objectMapper_shouldReturnANewInstance() {
        // when
        ObjectMapper result = jsonMapperConfig.objectMapper();

        // then
        assertNotNull(result);
    }
}

package com.edilson.justiniano.kalah.unit.configuration;

import com.edilson.justiniano.kalah.configuration.SwaggerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link SwaggerConfig} class
 */
@RunWith(JUnit4.class)
public class SwaggerConfigTest extends SwaggerConfig {

    private SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    public void greetingApi_shouldReturnNotNull() {
        // when
        Docket result = swaggerConfig.greetingApi();

        // then
        assertNotNull(result);
    }
}

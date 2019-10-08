package com.edilson.justiniano.kalah.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SwaggerConfig} class
 */
@RunWith(MockitoJUnitRunner.class)
public class SwaggerConfigTest {

    @Mock
    private ResourceHandlerRegistry resourceHandlerRegistry;

    @Mock
    private ResourceHandlerRegistration resourceHandlerRegistration;

    @InjectMocks
    private SwaggerConfig swaggerConfig;

    @Test
    public void greetingApi_shouldReturnNotNull() {
        // when
        Docket result = swaggerConfig.greetingApi();

        // then
        assertNotNull(result);
    }

    @Test
    public void addResourceHandlers_shouldRAddResourcesSuccessfully() {
        //given
        given(resourceHandlerRegistry.addResourceHandler(any(String.class))).willReturn(resourceHandlerRegistration);
        given(resourceHandlerRegistration.addResourceLocations(any(String.class))).willReturn(resourceHandlerRegistration);

        // when
        swaggerConfig.addResourceHandlers(resourceHandlerRegistry);

        // then
        verify(resourceHandlerRegistry, times(2)).addResourceHandler(any(String.class));
        verify(resourceHandlerRegistration, times(2)).addResourceLocations(any(String.class));
    }
}

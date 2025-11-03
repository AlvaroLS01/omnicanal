package com.comerzzia.bricodepot.api.omnichannel.api.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración básica de Jersey que delega en el escaneo estándar de Spring Boot.
 */
@Configuration
public class JerseyConfiguration extends ResourceConfig {

    public JerseyConfiguration() {
        packages("com.comerzzia.bricodepot.api.omnichannel.api");
    }
}

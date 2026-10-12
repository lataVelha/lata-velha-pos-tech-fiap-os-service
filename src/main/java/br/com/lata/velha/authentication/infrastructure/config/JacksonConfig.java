package br.com.lata.velha.authentication.infrastructure.config;

import br.com.lata.velha.shared.application.logging.Logger;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class JacksonConfig {

    private final Logger logger;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        logger.logInfo("Configurando Jackson2ObjectMapperBuilderCustomizer");
        return builder -> builder.serializers(
                new LocalDateTimeSerializer(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                )
        );
    }
}

package br.com.lata.velha.authentication.infrastructure.security.config;

import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class PasswordEncoderConfig {

    private final Logger logger;

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.logInfo("Configurando PasswordEncoder");
        return new BCryptPasswordEncoder();
    }
}

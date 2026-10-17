package br.com.lata.velha.authentication.infrastructure.security.config;

import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final Logger logger;
    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";
    private static final String MECANICO = "MECANICO";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.logInfo("Configurando SecurityFilterChain");
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // público
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/ordens-servico/*/aprovacao-orcamento").permitAll()

                        // CRUD — USER e ADMIN
                        .requestMatchers("/proprietarios/**").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/veiculos/**").hasAnyRole(USER, ADMIN)
                        .requestMatchers("/funcionarios/**").hasAnyRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/ordens-servico").hasAnyRole(USER, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/aprovar").hasAnyRole(USER, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/reprovar").hasAnyRole(USER, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/retirar-veiculo").hasAnyRole(USER, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/iniciar-diagnostico").hasAnyRole(MECANICO, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/adicionar-servico").hasAnyRole(MECANICO, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/finalizar-diagnostico").hasAnyRole(MECANICO, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/iniciar-servico/*").hasAnyRole(MECANICO, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/finalizar-servico/*").hasAnyRole(MECANICO, ADMIN)
                        .requestMatchers(HttpMethod.GET, "/ordens-servico/metricas/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET,"/ordens-servico/**").hasAnyRole(USER, MECANICO, ADMIN)
                        .requestMatchers(HttpMethod.GET,"/ordens-servico/status-service").hasAnyRole(USER, MECANICO, ADMIN)
                        .requestMatchers(HttpMethod.GET, "/historico/**").hasRole(ADMIN)


                        // qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                        .authenticationEntryPoint((request, response, authException) -> {
                            logger.logWarn("Autenticação rejeitada - status=401, causa={}", authException.getClass().getSimpleName());
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Token inválido ou expirado\"}");
                        })
                );

        return http.build();
    }
}

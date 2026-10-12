package br.com.lata.velha.authentication.api.controllers;

import br.com.lata.velha.authentication.application.use_cases.LoginUseCase;
import br.com.lata.velha.authentication.domain.exceptions.InvalidLoginException;
import br.com.lata.velha.shared.application.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(SecurityConfig.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockBean
    private Logger logger;

    @Test
    @DisplayName("POST /auth/login deve retornar 200 e token com credenciais válidas")
    void shouldReturn200WithTokenOnValidCredentials() throws Exception {
        var requestBody = Map.of("username", "admin@example.com", "password", "Senha1@!");
        var output = new LoginUseCase.Output("jwt-token-value", 3600L);

        when(loginUseCase.execute(any())).thenReturn(output);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-value"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 401 com credenciais inválidas")
    void shouldReturn401OnInvalidCredentials() throws Exception {
        var requestBody = Map.of("username", "admin@example.com", "password", "wrongPassword");

        when(loginUseCase.execute(any())).thenThrow(new InvalidLoginException("Usuário ou senha inválidos"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 401 para usuário inativo")
    void shouldReturn401ForInactiveUser() throws Exception {
        var requestBody = Map.of("username", "inativo@example.com", "password", "Senha1@!");

        when(loginUseCase.execute(any())).thenThrow(new InvalidLoginException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());
    }
}

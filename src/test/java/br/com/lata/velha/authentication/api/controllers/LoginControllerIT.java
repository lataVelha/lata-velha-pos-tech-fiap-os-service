package br.com.lata.velha.authentication.api.controllers;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.CadastrarFuncionarioGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.CadastrarFuncionarioUseCase;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.shared.application.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:login-controller-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CadastrarFuncionarioGateway cadastrarGateway;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private EntityManager em;

    @Autowired
    private Logger logger;

    private static final String USERNAME = "funcionario@example.com";
    private static final String SENHA = "Senha1@!";

    @BeforeEach
    void setUp() {
        RoleEntity role = new RoleEntity(null, "USER");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("ATENDENTE");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        em.flush();

        var input = new CadastrarFuncionarioUseCase.Input("Funcionário Teste", USERNAME, SENHA, cargo.getId(), "66778899042");
        new CadastrarFuncionarioUseCase(cadastrarGateway, authService, logger).execute(input);

        em.flush();
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 200 com token JWT válido")
    void shouldReturn200WithJwtTokenOnValidLogin() throws Exception {
        var body = Map.of("username", USERNAME, "password", SENHA);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 401 com senha incorreta")
    void shouldReturn401WithWrongPassword() throws Exception {
        var body = Map.of("username", USERNAME, "password", "senhaErrada123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("POST /auth/login deve retornar 404 com usuário inexistente")
    void shouldReturn400WithUnknownUsername() throws Exception {
        var body = Map.of("username", "desconhecido@example.com", "password", SENHA);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }
}

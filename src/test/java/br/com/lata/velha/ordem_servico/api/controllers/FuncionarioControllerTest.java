package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordem_servico.application.controllers.funcionario.FuncionarioCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.FuncionarioNotFoundException;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuncionarioController.class)
@Import(SecurityConfig.class)
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FuncionarioCleanController cleanController;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockBean
    private Logger logger;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /funcionarios deve retornar 201 e o funcionario criado")
    void shouldReturn201OnCreate() throws Exception {
        var request = new CadastrarFuncionarioRequest("Carlos Tecnico", "carlos@example.com", "Senha1@!", 1L, "77788899941");
        var response = new FuncionarioResponse(1L, "Carlos Tecnico", "MECANICO", UUID.randomUUID());
        when(cleanController.cadastrar(any())).thenReturn(response);
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Carlos Tecnico"))
                .andExpect(jsonPath("$.cargo").value("MECANICO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /funcionarios com body invalido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalid = new CadastrarFuncionarioRequest("", "", "", null, "");
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /funcionarios com username duplicado deve retornar 409")
    void shouldReturn409OnDuplicateUsername() throws Exception {
        var request = new CadastrarFuncionarioRequest("Carlos", "duplicado@example.com", "Senha1@!", 1L, "88899900078");
        when(cleanController.cadastrar(any()))
                .thenThrow(new ResourceAlreadyExistsException("Username ja cadastrado"));
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /funcionarios/{id} deve retornar 200 com o funcionario")
    void shouldReturn200OnFindById() throws Exception {
        var response = new FuncionarioResponse(1L, "Carlos Tecnico", "MECANICO", UUID.randomUUID());
        when(cleanController.buscarPorId(1L)).thenReturn(response);
        mockMvc.perform(get("/funcionarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Carlos Tecnico"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /funcionarios/{id} deve retornar 404 quando nao encontrado")
    void shouldReturn400WhenFuncionarioNotFound() throws Exception {
        when(cleanController.buscarPorId(99L)).thenThrow(FuncionarioNotFoundException.fromId(99L));
        mockMvc.perform(get("/funcionarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /funcionarios/{id} deve retornar 200 com o funcionario atualizado")
    void shouldReturn200OnUpdate() throws Exception {
        var request = new AtualizarFuncionarioRequest("Carlos Atualizado", 2L);
        var updated = new FuncionarioResponse(1L, "Carlos Atualizado", "ADMIN", UUID.randomUUID());
        when(cleanController.atualizar(any())).thenReturn(updated);
        mockMvc.perform(put("/funcionarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Atualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /funcionarios/{id} deve retornar 404 quando funcionario nao encontrado")
    void shouldReturn400WhenFuncionarioNotFoundOnUpdate() throws Exception {
        var request = new AtualizarFuncionarioRequest("Carlos Atualizado", 2L);
        when(cleanController.atualizar(any())).thenThrow(FuncionarioNotFoundException.fromId(99L));
        mockMvc.perform(put("/funcionarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /funcionarios/{id} deve retornar 422 quando usuario inativo")
    void shouldReturn400WhenUserIsInactiveOnUpdate() throws Exception {
        var request = new AtualizarFuncionarioRequest("Carlos Atualizado", 2L);
        when(cleanController.atualizar(any())).thenThrow(InactiveUserException.fromEntityName("Funcionario"));
        mockMvc.perform(put("/funcionarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /funcionarios/{id} com body invalido deve retornar 400")
    void shouldReturn400OnInvalidUpdateRequest() throws Exception {
        var invalid = new AtualizarFuncionarioRequest("", null);
        mockMvc.perform(put("/funcionarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /funcionarios/{id}/desativar deve retornar 204")
    void shouldReturn204OnDesativar() throws Exception {
        doNothing().when(cleanController).desativar(1L);
        mockMvc.perform(patch("/funcionarios/1/desativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /funcionarios/{id}/desativar deve retornar 404 quando nao encontrado")
    void shouldReturn400OnDesativarWhenNotFound() throws Exception {
        doThrow(FuncionarioNotFoundException.fromId(99L)).when(cleanController).desativar(99L);
        mockMvc.perform(patch("/funcionarios/99/desativar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /funcionarios com role USER deve retornar 403")
    void shouldReturn403ForUserRole() throws Exception {
        var request = new CadastrarFuncionarioRequest("Carlos", "carlos@example.com", "Senha1@!", 1L, "99900011112");
        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /funcionarios sem autenticacao deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/funcionarios/1"))
                .andExpect(status().isUnauthorized());
    }
}

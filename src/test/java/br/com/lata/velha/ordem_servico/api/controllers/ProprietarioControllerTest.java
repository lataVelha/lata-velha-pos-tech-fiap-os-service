package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordem_servico.application.controllers.proprietario.ProprietarioCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.EnderecoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.EnderecoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ProprietarioNotFoundException;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProprietarioController.class)
@Import(SecurityConfig.class)
class ProprietarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProprietarioCleanController cleanController;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockBean
    private Logger logger;

    private ProprietarioResponse buildResponse() {
        var endereco = new EnderecoResponse("Rua das Flores", "01234-567", "123");
        return new ProprietarioResponse(1L, "Joao da Silva", "joao@email.com", "123.456.789-00",
                "(11) 99999-9999", endereco, List.of());
    }

    private ProprietarioRequest buildRequest() {
        var endereco = new EnderecoRequest("Rua das Flores", "01234-567", "123");
        return new ProprietarioRequest("Joao da Silva", "joao@email.com", "123.456.789-00",
                "(11) 99999-9999", endereco);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /proprietarios deve retornar 201 e o proprietario criado")
    void shouldReturn201OnCreate() throws Exception {
        when(cleanController.criar(any())).thenReturn(buildResponse());
        mockMvc.perform(post("/proprietarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Joao da Silva"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /proprietarios com body invalido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalidRequest = new ProprietarioRequest("", "", "", "", null);
        mockMvc.perform(post("/proprietarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /proprietarios com documento duplicado deve retornar 409")
    void shouldReturn409OnDuplicateDocumento() throws Exception {
        when(cleanController.criar(any()))
                .thenThrow(new ResourceAlreadyExistsException("Proprietario ja cadastrado"));
        mockMvc.perform(post("/proprietarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /proprietarios deve retornar 200 com lista paginada")
    void shouldReturn200OnList() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildResponse()), 0, 10, 1L, 1);
        when(cleanController.listar(0, 10)).thenReturn(paginated);
        mockMvc.perform(get("/proprietarios")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /proprietarios/{id} deve retornar 200 com o proprietario")
    void shouldReturn200OnFindById() throws Exception {
        when(cleanController.buscarPorId(1L)).thenReturn(buildResponse());
        mockMvc.perform(get("/proprietarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Joao da Silva"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /proprietarios/{id} deve retornar 404 quando nao encontrado")
    void shouldReturn404WhenProprietarioNotFound() throws Exception {
        when(cleanController.buscarPorId(99L)).thenThrow(ProprietarioNotFoundException.fromId(99L));
        mockMvc.perform(get("/proprietarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /proprietarios/documento/{doc} deve retornar 200 com o proprietario")
    void shouldReturn200OnFindByDocumento() throws Exception {
        when(cleanController.buscarPorDocumento("123.456.789-00")).thenReturn(buildResponse());
        mockMvc.perform(get("/proprietarios/documento/123.456.789-00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documento").value("123.456.789-00"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /proprietarios/{id} deve retornar 200 com o proprietario atualizado")
    void shouldReturn200OnUpdate() throws Exception {
        when(cleanController.atualizar(eq(1L), any())).thenReturn(buildResponse());
        mockMvc.perform(put("/proprietarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /proprietarios/{id}/desativar deve retornar 204")
    void shouldReturn204OnDesativar() throws Exception {
        doNothing().when(cleanController).desativar(1L);
        mockMvc.perform(patch("/proprietarios/1/desativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /proprietarios/{id}/desativar deve retornar 404 quando nao encontrado")
    void shouldReturn404OnDesativarWhenNotFound() throws Exception {
        doThrow(ProprietarioNotFoundException.fromId(99L)).when(cleanController).desativar(99L);
        mockMvc.perform(patch("/proprietarios/99/desativar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /proprietarios/{id}/reativar deve retornar 200")
    void shouldReturn200OnReativar() throws Exception {
        when(cleanController.reativar(1L)).thenReturn(buildResponse());
        mockMvc.perform(patch("/proprietarios/1/reativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /proprietarios sem autenticacao deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/proprietarios"))
                .andExpect(status().isUnauthorized());
    }
}

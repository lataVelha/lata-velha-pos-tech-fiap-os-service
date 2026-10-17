package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordem_servico.application.controllers.servico.ServicoCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ServicoNotFoundException;
import br.com.lata.velha.shared.application.logging.Logger;
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

@WebMvcTest(ServicoController.class)
@Import(SecurityConfig.class)
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicoCleanController cleanController;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockBean
    private Logger logger;

    private ServicoResponse buildResponse() {
        return new ServicoResponse(1L, "Troca de Oleo", "Troca de oleo e filtro");
    }

    private CadastrarServicoRequest buildRequest() {
        return new CadastrarServicoRequest("Troca de Oleo", "Troca de oleo e filtro");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /servicos deve retornar 201 e o servico criado")
    void shouldReturn201OnCreate() throws Exception {
        when(cleanController.cadastrar(any())).thenReturn(buildResponse());
        mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Troca de Oleo"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /servicos com body invalido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalid = new CadastrarServicoRequest("", "");
        mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /servicos deve retornar 200 com lista paginada")
    void shouldReturn200OnList() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildResponse()), 0, 10, 1L, 1);
        when(cleanController.buscarTodos(0, 10)).thenReturn(paginated);
        mockMvc.perform(get("/servicos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /servicos/{id} deve retornar 200 com o servico")
    void shouldReturn200OnFindById() throws Exception {
        when(cleanController.buscarPorId(1L)).thenReturn(buildResponse());
        mockMvc.perform(get("/servicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Troca de Oleo"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /servicos/{id} deve retornar 404 quando nao encontrado")
    void shouldReturn404WhenServicoNotFound() throws Exception {
        when(cleanController.buscarPorId(99L)).thenThrow(ServicoNotFoundException.fromId(99L));
        mockMvc.perform(get("/servicos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /servicos/{id} deve retornar 200 com o servico atualizado")
    void shouldReturn200OnUpdate() throws Exception {
        var request = new AtualizarServicoRequest("Troca de Oleo Completa", "Inclui filtro e verificacao");
        var updated = new ServicoResponse(1L, "Troca de Oleo Completa", "Inclui filtro e verificacao");
        when(cleanController.atualizar(eq(1L), any())).thenReturn(updated);
        mockMvc.perform(put("/servicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Troca de Oleo Completa"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /servicos/{id} com body invalido deve retornar 400")
    void shouldReturn400OnInvalidUpdateRequest() throws Exception {
        var invalid = new AtualizarServicoRequest("", "");
        mockMvc.perform(put("/servicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /servicos/{id}/desativar deve retornar 204")
    void shouldReturn204OnDesativar() throws Exception {
        doNothing().when(cleanController).desativar(1L);
        mockMvc.perform(patch("/servicos/1/desativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /servicos/{id}/desativar deve retornar 404 quando nao encontrado")
    void shouldReturn404OnDesativarWhenNotFound() throws Exception {
        doThrow(ServicoNotFoundException.fromId(99L)).when(cleanController).desativar(99L);
        mockMvc.perform(patch("/servicos/99/desativar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /servicos sem autenticacao deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/servicos"))
                .andExpect(status().isUnauthorized());
    }
}

package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordem_servico.application.controllers.veiculo.VeiculoCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.VeiculoNotFoundException;
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

@WebMvcTest(VeiculoController.class)
@Import(SecurityConfig.class)
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VeiculoCleanController cleanController;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockBean
    private Logger logger;

    private VeiculoResponse buildResponse() {
        return new VeiculoResponse(1L, 10L, "ABC1D23", "Fiat", "Uno", 2020, "Prata");
    }

    private VeiculoRequest buildRequest() {
        return new VeiculoRequest(10L, "ABC1D23", "Fiat", "Uno", 2020, "Prata");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /veiculos deve retornar 201 e o veiculo criado")
    void shouldReturn201OnCreate() throws Exception {
        when(cleanController.criar(any())).thenReturn(buildResponse());
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.placa").value("ABC1D23"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /veiculos com body invalido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalid = new VeiculoRequest(null, "", "", "", null, "");
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /veiculos com placa duplicada deve retornar 409")
    void shouldReturn409OnDuplicatePlaca() throws Exception {
        when(cleanController.criar(any()))
                .thenThrow(new ResourceAlreadyExistsException("Placa ja cadastrada"));
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /veiculos deve retornar 200 com lista paginada")
    void shouldReturn200OnList() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildResponse()), 0, 10, 1L, 1);
        when(cleanController.listar(0, 10)).thenReturn(paginated);
        mockMvc.perform(get("/veiculos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /veiculos/{id} deve retornar 200 com o veiculo")
    void shouldReturn200OnFindById() throws Exception {
        when(cleanController.buscarPorId(1L)).thenReturn(buildResponse());
        mockMvc.perform(get("/veiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.placa").value("ABC1D23"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /veiculos/{id} deve retornar 404 quando nao encontrado")
    void shouldReturn404WhenVeiculoNotFound() throws Exception {
        when(cleanController.buscarPorId(99L)).thenThrow(VeiculoNotFoundException.fromId(99L));
        mockMvc.perform(get("/veiculos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /veiculos/proprietario/{proprietarioId} deve retornar 200 com lista")
    void shouldReturn200OnListByProprietario() throws Exception {
        when(cleanController.listarPorProprietario(10L)).thenReturn(List.of(buildResponse()));
        mockMvc.perform(get("/veiculos/proprietario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].proprietarioId").value(10L));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /veiculos/{id} deve retornar 200 com o veiculo atualizado")
    void shouldReturn200OnUpdate() throws Exception {
        when(cleanController.atualizar(eq(1L), any())).thenReturn(buildResponse());
        mockMvc.perform(put("/veiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /veiculos/{id}/desativar deve retornar 204")
    void shouldReturn204OnDesativar() throws Exception {
        doNothing().when(cleanController).desativar(1L);
        mockMvc.perform(patch("/veiculos/1/desativar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /veiculos/{id}/desativar deve retornar 404 quando nao encontrado")
    void shouldReturn404OnDesativarWhenNotFound() throws Exception {
        doThrow(VeiculoNotFoundException.fromId(99L)).when(cleanController).desativar(99L);
        mockMvc.perform(patch("/veiculos/99/desativar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PATCH /veiculos/{id}/reativar deve retornar 200")
    void shouldReturn200OnReativar() throws Exception {
        when(cleanController.reativar(1L)).thenReturn(buildResponse());
        mockMvc.perform(patch("/veiculos/1/reativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /veiculos sem autenticacao deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isUnauthorized());
    }
}

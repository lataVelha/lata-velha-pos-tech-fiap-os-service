package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.authentication.infrastructure.security.config.SecurityConfig;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.CriarOrdemServicoCompletaRequest;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.CriarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.ReceberAprovacaoOrcamentoRequest;
import br.com.lata.velha.ordem_servico.application.controllers.ordemservico.OrdemServicoCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoSemProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.*;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.ReceberAprovacaoOrcamentoClientePresenter;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoController.class)
@Import(SecurityConfig.class)
class OrdemServicoControllerTest {

    private static final String TEST_USER_ID = "00000000-0000-0000-0000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdemServicoCleanController cleanController;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockBean
    private Logger logger;

    private OrdemServicoResponse buildOrdemResponse() {
        return new OrdemServicoResponse(
                1L, "RECEBIDA", "Barulho ao frear",
                new FuncionarioResumoResponse(2L, "Maria Atendente"),
                null,
                new ProprietarioResumoResponse(4L, "João Proprietário"),
                new VeiculoResumoResponse(3L, "Fiat Uno 2020"),
                LocalDateTime.now(), null, null, LocalDateTime.now(),
                List.of()
        );
    }

    @Test
    @DisplayName("POST /ordens-servico deve retornar 201 com a ordem criada")
    void shouldReturn201OnCreate() throws Exception {
        var request = new CriarOrdemServicoRequest(3L, 4L, "Barulho ao frear");

        when(cleanController.criar(any())).thenReturn(buildOrdemResponse());

        mockMvc.perform(post("/ordens-servico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("POST /ordens-servico com body inválido deve retornar 400")
    void shouldReturn400OnInvalidCreateRequest() throws Exception {
        var invalid = new CriarOrdemServicoRequest(null, null, "x".repeat(501));

        mockMvc.perform(post("/ordens-servico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /ordens-servico/completa deve retornar 201 com a ordem criada")
    void shouldReturn201OnCreateCompleta() throws Exception {
        var request = new CriarOrdemServicoCompletaRequest(
                new ProprietarioRequest("João da Silva", "joao@example.com", "359.493.430-69", "(11) 99999-9999", null),
                new VeiculoSemProprietarioRequest("ABC1D23", "Fiat", "Uno", 2020, "Prata"),
                "Barulho ao frear",
                List.of()
        );

        when(cleanController.criarCompleta(any())).thenReturn(buildOrdemResponse());

        mockMvc.perform(post("/ordens-servico/completa")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("POST /ordens-servico/completa com body inválido deve retornar 400")
    void shouldReturn400OnInvalidCreateCompletaRequest() throws Exception {
        var invalid = new CriarOrdemServicoCompletaRequest(null, null, "x".repeat(501), List.of());

        mockMvc.perform(post("/ordens-servico/completa")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /ordens-servico deve retornar 200 com lista paginada")
    void shouldReturn200OnList() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildOrdemResponse()), 0, 10, 1L, 1);

        when(cleanController.buscar(null, null, null, null, 0, 10)).thenReturn(paginated);

        mockMvc.perform(get("/ordens-servico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /ordens-servico/metricas/tempo-medio-execucao deve retornar 200 para ADMIN")
    void shouldReturn200OnGetAverageExecutionTimeForAdmin() throws Exception {
        var response = new TempoMedioExecucaoResponse(
                List.of(new TempoMedioExecucaoServicoItemResponse(5L, "Troca freio", new BigDecimal("32.50"))),
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-31"),
                "America/Sao_Paulo"
        );

        when(cleanController.buscarTempoMedioExecucao(any(), any())).thenReturn(response);

        mockMvc.perform(get("/ordens-servico/metricas/tempo-medio-execucao")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .param("dataInicio", "01/01/2026")
                        .param("dataFim", "31/01/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicos[0].servicoId").value(5))
                .andExpect(jsonPath("$.servicos[0].servicoNome").value("Troca freio"))
                .andExpect(jsonPath("$.servicos[0].tempoMedioMinutos").value(32.5));
    }

    @Test
    @DisplayName("GET /ordens-servico/metricas/tempo-medio-execucao deve retornar 403 para USER")
    void shouldReturn403OnGetAverageExecutionTimeForUser() throws Exception {
        mockMvc.perform(get("/ordens-servico/metricas/tempo-medio-execucao")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /ordens-servico com filtros deve retornar 200")
    void shouldReturn200OnListWithFilters() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildOrdemResponse()), 0, 10, 1L, 1);

        when(cleanController.buscar(1L, StatusOrdemServico.RECEBIDA, 4L, null, 0, 10))
                .thenReturn(paginated);

        mockMvc.perform(get("/ordens-servico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .param("id", "1")
                        .param("status", "RECEBIDA")
                        .param("proprietarioId", "4")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /ordens-servico/{idOs}/iniciar-diagnostico deve retornar 200")
    void shouldReturn200OnIniciarDiagnostico() throws Exception {
        mockMvc.perform(patch("/ordens-servico/1/iniciar-diagnostico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_MECANICO"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /ordens-servico/{idOs}/adicionar-servico deve retornar 200")
    void shouldReturn200OnAdicionarServico() throws Exception {
        var request = objectMapper.createObjectNode();
        request.putArray("servicoRequests").addObject()
                .put("servicoId", 10)
                .put("valorMaoDeObra", 150.00)
                .putArray("pecas");

        mockMvc.perform(patch("/ordens-servico/1/adicionar-servico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_MECANICO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /ordens-servico/{idOs}/finalizar-diagnostico deve retornar 200")
    void shouldReturn200OnFinalizarDiagnostico() throws Exception {
        mockMvc.perform(patch("/ordens-servico/1/finalizar-diagnostico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_MECANICO"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /ordens-servico/{idOs}/aprovar deve retornar 200")
    void shouldReturn200OnAprovar() throws Exception {
        var servico = new AprovarOrdemServicoRequest.Servico(10L, "APROVADO");
        var request = new AprovarOrdemServicoRequest(List.of(servico));
        var aprovarResponse = new AprovarOrdemServicoResponse(1L, "APROVADA",
                List.of(new AprovarOrdemServicoResponse.Servico(10L, "APROVADO")));

        when(cleanController.aprovar(any())).thenReturn(aprovarResponse);

        mockMvc.perform(patch("/ordens-servico/1/aprovar")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOs").value(1L))
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    @DisplayName("PATCH /ordens-servico/{idOs}/aprovar com lista vazia deve retornar 400")
    void shouldReturn400OnAprovarWithInvalidRequest() throws Exception {
        var invalid = new AprovarOrdemServicoRequest(List.of());

        mockMvc.perform(patch("/ordens-servico/1/aprovar")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /ordens-servico/{idOs}/reprovar deve retornar 200")
    void shouldReturn200OnReprovar() throws Exception {
        mockMvc.perform(patch("/ordens-servico/1/reprovar")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /ordens-servico/{idOs}/iniciar-diagnostico com role USER deve retornar 403")
    void shouldReturn403OnIniciarDiagnosticoForUserRole() throws Exception {
        mockMvc.perform(patch("/ordens-servico/1/iniciar-diagnostico")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /ordens-servico sem autenticação deve retornar 401")
    void shouldReturn401WhenUnauthenticated() throws Exception {
        var request = new CriarOrdemServicoRequest(3L, 4L, "Test");

        mockMvc.perform(post("/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /ordens-servico/status-service deve retornar 200")
    void shouldReturn200OnGetStatusService() throws Exception {
        var paginated = new PaginatedResult<>(List.of(buildOrdemResponse()), 0, 10, 1L, 1);

        when(cleanController.buscarPorStatus(0, 10)).thenReturn(paginated);

        mockMvc.perform(get("/ordens-servico/status-service")
                        .with(jwt().jwt(b -> b.subject(TEST_USER_ID)).authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("POST /ordens-servico/{id}/aprovacao-orcamento deve retornar 200 sem autenticação")
    void shouldReturn200OnAprovacaoOrcamentoSemAutenticacao() throws Exception {
        var viewModel = new ReceberAprovacaoOrcamentoClientePresenter.ViewModel(1L, "APROVADA");
        when(cleanController.receberAprovacaoOrcamentoCliente(any())).thenReturn(viewModel);

        var servico = new ReceberAprovacaoOrcamentoRequest.ServicoAprovacao(10L, StatusExecucaoServico.APROVADO);
        var request = new ReceberAprovacaoOrcamentoRequest(List.of(servico));

        mockMvc.perform(post("/ordens-servico/1/aprovacao-orcamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOs").value(1L))
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }
}

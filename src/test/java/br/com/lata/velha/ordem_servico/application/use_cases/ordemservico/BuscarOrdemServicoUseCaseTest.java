package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarOrdemServicoUseCaseTest {

    @Mock
    private BuscarOrdemServicoGateway gateway;

    @Mock
    private Logger logger;

    private BuscarOrdemServicoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BuscarOrdemServicoUseCase(gateway, logger);
    }

    private OrdemServicoProjection buildProjection(Long id, String status) {
        return new OrdemServicoProjection() {
            public Long getId() { return id; }
            public Long getAtendenteInicioId() { return 10L; }
            public String getAtendenteNome() { return "Ana"; }
            public Long getVeiculoId() { return 40L; }
            public String getVeiculoDescricao() { return "Honda Civic"; }
            public Long getProprietarioId() { return 30L; }
            public String getProprietarioNome() { return "João"; }
            public Long getMecanicoFinalId() { return 20L; }
            public String getMecanicoNome() { return "Carlos"; }
            public String getStatus() { return status; }
            public String getReclamacaoProprietario() { return "Barulho ao frear"; }
            public LocalDateTime getIniciadoEm() { return null; }
            public LocalDateTime getFinalizadoEm() { return null; }
            public LocalDateTime getEntregueEm() { return null; }
            public LocalDateTime getAtualizadoEm() { return null; }
            public String getServicos() { return "[]"; }
        };
    }

    private PaginatedResult<OrdemServicoProjection> paginatedResultOf(List<OrdemServicoProjection> projections) {
        return new PaginatedResult<>(projections, 0, 10, projections.size(), 1);
    }

    private PaginatedResult<OrdemServicoProjection> paginatedResultOf(List<OrdemServicoProjection> projections, int page, int size, long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        return new PaginatedResult<>(projections, page, size, total, totalPages);
    }

    @Test
    @DisplayName("deve retornar lista paginada quando há OS")
    void deveRetornarListaPaginadaComOs() {
        var projection = buildProjection(1L, "RECEBIDA");
        when(gateway.findByFiltros(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of(projection)));

        var result = useCase.execute(null, null, null, null, 0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getId()).isEqualTo(1L);
        assertThat(result.content().get(0).getStatus()).isEqualTo("RECEBIDA");
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não há OS")
    void deveRetornarListaVaziaQuandoNaoHaOs() {
        when(gateway.findByFiltros(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        var result = useCase.execute(null, null, null, null, 0, 10);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    @DisplayName("deve converter status enum para string ao chamar o gateway")
    void deveConverterStatusEnumParaStringNoGateway() {
        when(gateway.findByFiltros(any(), eq("APROVADA"), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(null, StatusOrdemServico.APROVADA, null, null, 0, 10);

        verify(gateway).findByFiltros(isNull(), eq("APROVADA"), isNull(), isNull(), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve passar null ao gateway quando status é null")
    void devePassarNullAoGatewayQuandoStatusEhNull() {
        when(gateway.findByFiltros(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(null, null, null, null, 0, 10);

        verify(gateway).findByFiltros(isNull(), isNull(), isNull(), isNull(), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve filtrar por id quando informado")
    void deveFiltrarPorId() {
        when(gateway.findByFiltros(eq(5L), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(5L, null, null, null, 0, 10);

        verify(gateway).findByFiltros(eq(5L), isNull(), isNull(), isNull(), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve filtrar por proprietarioId quando informado")
    void deveFiltrarPorProprietarioId() {
        when(gateway.findByFiltros(any(), any(), eq(30L), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(null, null, 30L, null, 0, 10);

        verify(gateway).findByFiltros(isNull(), isNull(), eq(30L), isNull(), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve filtrar por mecanicoId quando informado")
    void deveFiltrarPorMecanicoId() {
        when(gateway.findByFiltros(any(), any(), any(), eq(20L), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(null, null, null, 20L, 0, 10);

        verify(gateway).findByFiltros(isNull(), isNull(), isNull(), eq(20L), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve retornar metadados de paginação corretamente")
    void deveRetornarMetadadosDePaginacao() {
        var projections = List.of(buildProjection(1L, "RECEBIDA"), buildProjection(2L, "APROVADA"));
        when(gateway.findByFiltros(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(projections, 0, 10, 25L));

        var result = useCase.execute(null, null, null, null, 0, 10);

        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(25L);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.content()).hasSize(2);
    }

    @Test
    @DisplayName("deve passar page e size corretos para o gateway")
    void devePassarPageSizeCorreto() {
        when(gateway.findByFiltros(any(), any(), any(), any(), eq(2), eq(5)))
                .thenReturn(paginatedResultOf(List.of(), 2, 5, 0L));

        useCase.execute(null, null, null, null, 2, 5);

        verify(gateway).findByFiltros(isNull(), isNull(), isNull(), isNull(), eq(2), eq(5));
    }

    @Test
    @DisplayName("deve mapear campos do projection corretamente")
    void deveMapearCamposDoProjection() {
        var projection = buildProjection(99L, "EM_DIAGNOSTICO");
        when(gateway.findByFiltros(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of(projection)));

        var result = useCase.execute(null, null, null, null, 0, 10);

        var item = result.content().get(0);
        assertThat(item.getId()).isEqualTo(99L);
        assertThat(item.getStatus()).isEqualTo("EM_DIAGNOSTICO");
        assertThat(item.getReclamacaoProprietario()).isEqualTo("Barulho ao frear");
        assertThat(item.getAtendenteNome()).isEqualTo("Ana");
        assertThat(item.getMecanicoNome()).isEqualTo("Carlos");
        assertThat(item.getProprietarioNome()).isEqualTo("João");
        assertThat(item.getVeiculoDescricao()).isEqualTo("Honda Civic");
    }

    @Test
    @DisplayName("deve retornar múltiplas OS na lista")
    void deveRetornarMultiplasOs() {
        var projections = List.of(
                buildProjection(1L, "RECEBIDA"),
                buildProjection(2L, "APROVADA"),
                buildProjection(3L, "FINALIZADA")
        );
        when(gateway.findByFiltros(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(projections));

        var result = useCase.execute(null, null, null, null, 0, 10);

        assertThat(result.content()).hasSize(3);
        assertThat(result.content()).extracting(OrdemServicoProjection::getStatus)
                .containsExactly("RECEBIDA", "APROVADA", "FINALIZADA");
    }

    @Test
    @DisplayName("deve filtrar combinando id, status, proprietarioId e mecanicoId")
    void deveFiltrarComTodosOsParametros() {
        when(gateway.findByFiltros(eq(1L), eq("EM_EXECUCAO"), eq(30L), eq(20L), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(1L, StatusOrdemServico.EM_EXECUCAO, 30L, 20L, 0, 10);

        verify(gateway).findByFiltros(1L, "EM_EXECUCAO", 30L, 20L, 0, 10);
    }
}

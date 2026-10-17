package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarTempoMedioExecucaoServicosFinalizadosUseCaseTest {

    @Mock
    private BuscarTempoMedioExecucaoGateway gateway;

    @Mock
    private Logger logger;

    private BuscarTempoMedioExecucaoServicosFinalizadosUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BuscarTempoMedioExecucaoServicosFinalizadosUseCase(gateway, logger);
    }

    @Test
    void deveRetornarTempoMedioAgrupadoPorServico() {
        LocalDate dataInicio = LocalDate.parse("2026-01-01");
        LocalDate dataFim = LocalDate.parse("2026-01-31");

        when(gateway.buscarTempoMedioExecucao(
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX)
        )).thenReturn(List.of(
                new TempoMedioExecucaoPorServico(1L, "Balanceamento", 12.345),
                new TempoMedioExecucaoPorServico(5L, "Troca freio", 37.2)
        ));

        var result = useCase.execute(dataInicio, dataFim);

        assertThat(result.itens()).hasSize(2);
        assertThat(result.itens().get(0).servicoId()).isEqualTo(1L);
        assertThat(result.itens().get(0).servicoNome()).isEqualTo("Balanceamento");
        assertThat(result.itens().get(0).tempoMedioMinutos()).isEqualTo(12.345);
        assertThat(result.itens().get(1).tempoMedioMinutos()).isEqualTo(37.2);
        assertThat(result.dataInicio()).isEqualTo(dataInicio);
        assertThat(result.dataFim()).isEqualTo(dataFim);

        verify(gateway).buscarTempoMedioExecucao(
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX)
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverDados() {
        LocalDate dataInicio = LocalDate.parse("2026-01-01");
        LocalDate dataFim = LocalDate.parse("2026-01-31");

        when(gateway.buscarTempoMedioExecucao(
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX)
        )).thenReturn(List.of());

        var result = useCase.execute(dataInicio, dataFim);

        assertThat(result.itens()).isEmpty();
        assertThat(result.dataInicio()).isEqualTo(dataInicio);
        assertThat(result.dataFim()).isEqualTo(dataFim);
    }

    @Test
    void deveLancarExcecaoQuandoPeriodoForInvalido() {
        LocalDate dataInicio = LocalDate.parse("2026-02-01");
        LocalDate dataFim = LocalDate.parse("2026-01-31");

        assertThatThrownBy(() -> useCase.execute(dataInicio, dataFim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Data inicial não pode ser maior que data final");
    }

    @Test
    void deveLancarExcecaoQuandoPeriodoExcederLimite() {
        LocalDate dataInicio = LocalDate.parse("2026-01-01");
        LocalDate dataFim = LocalDate.parse("2026-07-31");

        assertThatThrownBy(() -> useCase.execute(dataInicio, dataFim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Período máximo permitido é de 180 dias");
    }

    @Test
    void deveAplicarPeriodoPadraoQuandoNaoInformado() {
        when(gateway.buscarTempoMedioExecucao(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new TempoMedioExecucaoPorServico(1L, "Balanceamento", 20.0)));

        var result = useCase.execute(null, null);

        assertThat(result.itens()).hasSize(1);
        assertThat(result.itens().get(0).tempoMedioMinutos()).isEqualTo(20.0);
        assertThat(result.dataFim()).isNotNull();
        assertThat(result.dataInicio()).isEqualTo(result.dataFim().minusDays(29));
    }
}

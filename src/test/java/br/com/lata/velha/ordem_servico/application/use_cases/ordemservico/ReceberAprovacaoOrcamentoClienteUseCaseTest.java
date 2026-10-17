package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceberAprovacaoOrcamentoClienteUseCaseTest {

    @Mock private ReceberAprovacaoOrcamentoClienteGateway gateway;
    @Mock private NotificarOrdemServicoService notificarService;
    @Mock private Logger logger;

    private ReceberAprovacaoOrcamentoClienteUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long EXEC_ID_1 = 10L;
    private static final Long EXEC_ID_2 = 11L;

    @BeforeEach
    void setUp() {
        useCase = new ReceberAprovacaoOrcamentoClienteUseCase(gateway, notificarService, logger);
    }

    private ExecucaoServico buildExecPendente(Long id) {
        return new ExecucaoServico(id, 99L, OS_ID, StatusExecucaoServico.PENDENTE,
                new BigDecimal("150"), new HashSet<>(), null, null,
                null, null, LocalDateTime.now());
    }

    private OrdemServico buildOsAguardandoAprovacao(List<ExecucaoServico> execucoes) {
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                StatusOrdemServico.AGUARDANDO_APROVACAO,
                LocalDateTime.now(), null, null, null, null,
                3L, null, new ArrayList<>(execucoes));
    }

    @Test
    @DisplayName("deve aprovar OS quando pelo menos um serviço é aprovado")
    void deveAprovarOsQuandoPeloMenosUmServicoAprovado() {
        var exec1 = buildExecPendente(EXEC_ID_1);
        var exec2 = buildExecPendente(EXEC_ID_2);
        var os = buildOsAguardandoAprovacao(List.of(exec1, exec2));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.APROVADO),
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_2, StatusExecucaoServico.RECUSADO)
        ));

        useCase.execute(input);

        assertThat(exec1.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        assertThat(exec2.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.APROVADA);
        verify(notificarService).execute(os);
    }

    @Test
    @DisplayName("deve reprovar OS quando todos os serviços são recusados")
    void deveReprovarOsQuandoTodosServicosRecusados() {
        var exec1 = buildExecPendente(EXEC_ID_1);
        var exec2 = buildExecPendente(EXEC_ID_2);
        var os = buildOsAguardandoAprovacao(List.of(exec1, exec2));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.RECUSADO),
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_2, StatusExecucaoServico.RECUSADO)
        ));

        useCase.execute(input);

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.REPROVADA);
        verify(notificarService).execute(os);
    }

    @Test
    @DisplayName("deve salvar OS após processar aprovação")
    void deveSalvarOsAposProcessarAprovacao() {
        var exec = buildExecPendente(EXEC_ID_1);
        var os = buildOsAguardandoAprovacao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.APROVADO)
        ));

        useCase.execute(input);

        verify(gateway).salvarOrdemServico(os);
    }

    @Test
    @DisplayName("deve lançar exceção quando serviço não pertence à OS")
    void deveLancarExcecaoQuandoServicoNaoPertenceAOs() {
        var exec = buildExecPendente(EXEC_ID_1);
        var os = buildOsAguardandoAprovacao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);

        Long execIdInvalido = 999L;
        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(execIdInvalido, StatusExecucaoServico.APROVADO)
        ));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(OS_ID));

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.APROVADO)
        ));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(gateway, never()).salvarOrdemServico(any());
    }
}

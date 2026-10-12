package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
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
class ReprovarOrdemServicoUseCaseTest {

    @Mock private ReprovarOrdemServicoGateway gateway;
    @Mock private NotificarOrdemServicoService notificarService;
    @Mock private Logger logger;

    private ReprovarOrdemServicoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long ATENDENTE_ID = 2L;

    private Funcionario atendente;
    private UserId userId;

    @BeforeEach
    void setUp() {
        useCase = new ReprovarOrdemServicoUseCase(gateway, notificarService, logger);
        atendente = new Funcionario(ATENDENTE_ID, "Ana Atendente", null, null);
        userId = UserId.random();
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
                3L, 4L, new ArrayList<>(execucoes));
    }

    @Test
    @DisplayName("deve reprovar OS e marcar todos os serviços como RECUSADO")
    void deveReprovarOrdemServicoComSucesso() {
        var exec1 = buildExecPendente(10L);
        var exec2 = buildExecPendente(11L);
        var os = buildOsAguardandoAprovacao(List.of(exec1, exec2));

        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(atendente);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new ReprovarOrdemServicoUseCase.Input(OS_ID, userId));

        assertThat(exec1.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
        assertThat(exec2.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.REPROVADA);
    }

    @Test
    @DisplayName("deve atribuir atendente como aprovador dos serviços recusados")
    void deveAtribuirAtendenteAosServicosRecusados() {
        var exec = buildExecPendente(10L);
        var os = buildOsAguardandoAprovacao(List.of(exec));

        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(atendente);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new ReprovarOrdemServicoUseCase.Input(OS_ID, userId));

        assertThat(exec.getAtendenteAprovacaoId()).isEqualTo(ATENDENTE_ID);
    }

    @Test
    @DisplayName("deve salvar OS após reprovar")
    void deveSalvarOsAposReprovar() {
        var os = buildOsAguardandoAprovacao(List.of(buildExecPendente(10L)));

        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(atendente);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new ReprovarOrdemServicoUseCase.Input(OS_ID, userId));

        verify(gateway).salvarOrdemServico(os);
    }

    @Test
    @DisplayName("deve enviar notificação após reprovar")
    void deveEnviarNotificacaoAposReprovar() {
        var os = buildOsAguardandoAprovacao(List.of(buildExecPendente(10L)));

        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(atendente);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new ReprovarOrdemServicoUseCase.Input(OS_ID, userId));

        verify(notificarService).execute(os);
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(gateway.getOrdemServicoPorId(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        var input = new ReprovarOrdemServicoUseCase.Input(OS_ID, userId);
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando funcionário não encontrado")
    void devePropagateExcecaoQuandoFuncionarioNaoEncontrado() {
        var os = buildOsAguardandoAprovacao(List.of(buildExecPendente(10L)));
        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenThrow(new RuntimeException("Funcionário não encontrado"));

        var input = new ReprovarOrdemServicoUseCase.Input(OS_ID, userId);
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Funcionário não encontrado");

        verify(gateway, never()).salvarOrdemServico(any());
    }
}

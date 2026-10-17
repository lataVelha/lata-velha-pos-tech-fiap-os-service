package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalizarDiagnosticoUseCaseTest {

    @Mock private FinalizarDiagnosticoGateway gateway;
    @Mock private NotificarOrdemServicoService notificarService;
    @Mock private Logger logger;

    private FinalizarDiagnosticoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long ATENDENTE_ID = 10L;
    private static final Long MECANICO_ID = 20L;
    private static final Long PROPRIETARIO_ID = 30L;
    private static final Long VEICULO_ID = 40L;

    private UserId userId;
    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        useCase = new FinalizarDiagnosticoUseCase(gateway, notificarService, logger);
        userId = UserId.random();
        funcionario = new Funcionario(MECANICO_ID, "Carlos Mecânico", null, null);
    }

    private OrdemServico buildOs(StatusOrdemServico status) {
        return new OrdemServico(
                OS_ID, PROPRIETARIO_ID, VEICULO_ID, "Barulho ao frear",
                status,
                LocalDateTime.now(), null, null, null, null,
                ATENDENTE_ID, MECANICO_ID,
                new ArrayList<>()
        );
    }

    private FinalizarDiagnosticoUseCase.Input input() {
        return new FinalizarDiagnosticoUseCase.Input(OS_ID, userId);
    }

    @Test
    @DisplayName("deve finalizar diagnóstico sem serviços e ir direto para FINALIZADA")
    void deveFinalizarDiagnosticoSemServicosParaFinalizada() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(gateway.getOrdemServicoComServicos(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(funcionario);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(input());

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
    }

    @Test
    @DisplayName("deve salvar OS após finalizar diagnóstico")
    void deveSalvarOsAposFinalizarDiagnostico() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(gateway.getOrdemServicoComServicos(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(funcionario);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(input());

        verify(gateway).salvarOrdemServico(os);
    }

    @Test
    @DisplayName("deve notificar após finalizar diagnóstico")
    void deveNotificarAposFinalizarDiagnostico() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(gateway.getOrdemServicoComServicos(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(funcionario);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(input());

        verify(notificarService).execute(os);
    }

    @Test
    @DisplayName("deve propagar exceção quando usuário não é funcionário")
    void devePropagateExcecaoQuandoUsuarioNaoEFuncionario() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(gateway.getOrdemServicoComServicos(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenThrow(new RuntimeException("Usuário não é funcionário"));
        var input = input();

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class);

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS não está EM_DIAGNOSTICO")
    void deveLancarExcecaoQuandoOsNaoEstaEmDiagnostico() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(gateway.getOrdemServicoComServicos(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(funcionario);
        var input = input();

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS já está AGUARDANDO_APROVACAO")
    void deveLancarExcecaoQuandoOsJaAguardandoAprovacao() {
        var os = buildOs(StatusOrdemServico.AGUARDANDO_APROVACAO);
        when(gateway.getOrdemServicoComServicos(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(funcionario);
        var input = input();

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalStateException.class);

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(gateway.getOrdemServicoComServicos(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));
        var input = input();

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }
}

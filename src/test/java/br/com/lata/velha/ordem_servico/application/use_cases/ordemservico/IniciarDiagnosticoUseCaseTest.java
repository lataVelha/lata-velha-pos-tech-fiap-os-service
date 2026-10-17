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
class IniciarDiagnosticoUseCaseTest {

    @Mock private IniciarDiagnosticoGateway gateway;
    @Mock private NotificarOrdemServicoService notificarService;
    @Mock private Logger logger;

    private IniciarDiagnosticoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long MECANICO_ID = 2L;

    private Funcionario mecanico;
    private UserId userId;

    @BeforeEach
    void setUp() {
        useCase = new IniciarDiagnosticoUseCase(gateway, notificarService, logger);
        mecanico = new Funcionario(MECANICO_ID, "Carlos Mecânico", null, null);
        userId = UserId.random();
    }

    private OrdemServico buildOs(StatusOrdemServico status) {
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                status, LocalDateTime.now(), null, null, LocalDateTime.now(), null,
                3L, null, new ArrayList<>());
    }

    @Test
    @DisplayName("deve iniciar diagnóstico e alterar status para EM_DIAGNOSTICO")
    void deveIniciarDiagnosticoComSucesso() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId));

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        assertThat(os.getMecanicoResponsavelId()).isEqualTo(MECANICO_ID);
    }

    @Test
    @DisplayName("deve persistir a OS após iniciar diagnóstico")
    void deveSalvarOsAposIniciarDiagnostico() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId));

        verify(gateway).salvarOrdemServico(os);
    }

    @Test
    @DisplayName("deve notificar após iniciar diagnóstico")
    void deveNotificarAposIniciarDiagnostico() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId));

        verify(notificarService).execute(os);
    }

    @Test
    @DisplayName("deve lançar IllegalStateException quando OS não está em status RECEBIDA")
    void deveLancarExcecaoQuandoStatusInvalido() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);

        var input = new IniciarDiagnosticoUseCase.Input(OS_ID, userId);
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RECEBIDA");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagarExcecaoQuandoOsNaoEncontrada() {
        when(gateway.getOrdemServicoPorId(OS_ID)).thenThrow(new RuntimeException("OS não encontrada"));

        var input = new IniciarDiagnosticoUseCase.Input(OS_ID, userId);
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando mecânico não encontrado")
    void devePropagarExcecaoQuandoMecanicoNaoEncontrado() {
        when(gateway.getOrdemServicoPorId(OS_ID)).thenReturn(buildOs(StatusOrdemServico.RECEBIDA));
        when(gateway.getFuncionarioPorUserId(userId)).thenThrow(new RuntimeException("Funcionário não encontrado"));

        var input = new IniciarDiagnosticoUseCase.Input(OS_ID, userId);
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Funcionário não encontrado");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }
}

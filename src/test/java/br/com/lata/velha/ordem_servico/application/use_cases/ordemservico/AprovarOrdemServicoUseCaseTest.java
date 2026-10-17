package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.*;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AprovarOrdemServicoUseCaseTest {

    @Mock private AprovarOrdemServicoGateway gateway;
    @Mock private NotificarOrdemServicoService notificarService;
    @Mock private Logger logger;

    private AprovarOrdemServicoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long FUNCIONARIO_ID = 2L;
    private static final Long EXECUCAO_ID = 10L;
    private static final Long SERVICO_ID = 99L;

    private Funcionario funcionario;
    private UserId userId;

    @BeforeEach
    void setUp() {
        useCase = new AprovarOrdemServicoUseCase(gateway, notificarService, logger);
        funcionario = new Funcionario(FUNCIONARIO_ID, "Ana Atendente", null, null);
        userId = UserId.random();
        lenient().when(gateway.getServicosAtivosPorIds(any()))
                .thenReturn(List.of(new Servico(SERVICO_ID, "Troca de óleo", "desc")));
    }

    private void stubFuncionario() {
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(funcionario);
    }

    private ExecucaoServico buildExecucao(Long id) {
        return new ExecucaoServico(id, SERVICO_ID, OS_ID, StatusExecucaoServico.PENDENTE, new BigDecimal("200"),
                new HashSet<>(), null, null, null, null, LocalDateTime.now());
    }

    private OrdemServico buildOs(List<ExecucaoServico> execucoes) {
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                StatusOrdemServico.AGUARDANDO_APROVACAO,
                LocalDateTime.now(), null, null, LocalDateTime.now(), null,
                3L, 4L, execucoes);
    }

    private void stubSave(OrdemServico os) {
        when(gateway.salvarOrdemServico(any(OrdemServico.class))).thenReturn(os);
    }

    @Test
    @DisplayName("deve aprovar OS e execuções informadas como APROVADO")
    void deveAprovarOsEExecucoes() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        OrdemServico os = buildOs(List.of(exec));
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId,
                List.of(new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(EXECUCAO_ID, StatusExecucaoServico.APROVADO)));

        var output = useCase.execute(input);

        assertThat(output.getStatus()).isEqualTo(StatusOrdemServico.APROVADA);
        assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
    }

    @Test
    @DisplayName("deve recusar execução quando não informada no input")
    void deveRecusarExecucaoNaoInformadaNoInput() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        ExecucaoServico execAprovada = buildExecucao(11L);
        OrdemServico os = buildOs(List.of(exec, execAprovada));
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId,
                List.of(new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(11L, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
    }

    @Test
    @DisplayName("deve recusar execução explicitamente informada como RECUSADO")
    void deveRecusarExecucaoExplicitamente() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        ExecucaoServico execAprovada = buildExecucao(11L);
        OrdemServico os = buildOs(List.of(exec, execAprovada));
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId,
                List.of(
                        new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(EXECUCAO_ID, StatusExecucaoServico.RECUSADO),
                        new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(11L, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
    }

    @Test
    @DisplayName("deve chamar notificação após aprovar OS")
    void deveChamarNotificacaoAposAprovar() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        OrdemServico os = buildOs(List.of(exec));
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId,
                List.of(new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(EXECUCAO_ID, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        verify(notificarService).execute(os);
    }

    @Test
    @DisplayName("deve retornar OrdemServico com id, status e lista de serviços")
    void deveRetornarOrdemServicoComDadosCorretos() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        OrdemServico os = buildOs(List.of(exec));
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);

        OrdemServico savedOs = buildOs(List.of(exec));
        when(gateway.salvarOrdemServico(any())).thenReturn(savedOs);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId,
                List.of(new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(EXECUCAO_ID, StatusExecucaoServico.APROVADO)));

        var output = useCase.execute(input);

        assertThat(output.getId()).isEqualTo(OS_ID);
        assertThat(output.getExecucaoServicos()).hasSize(1);
        assertThat(output.getExecucaoServicos().get(0).getId()).isEqualTo(EXECUCAO_ID);
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOsNaoEncontrada() {
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId, List.of());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando funcionário não encontrado")
    void deveLancarExcecaoQuandoFuncionarioNaoEncontrado() {
        when(gateway.getFuncionarioPorUserId(userId))
                .thenThrow(new RuntimeException("Funcionário não encontrado"));
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID))
                .thenReturn(buildOs(List.of()));

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId, List.of());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Funcionário não encontrado");

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve lançar IllegalArgumentException quando serviço do input não pertence à OS")
    void deveLancarExcecaoQuandoServicoNaoPertenceAOs() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(buildOs(List.of(exec)));

        Long idInvalido = 999L;
        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId,
                List.of(new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(idInvalido, StatusExecucaoServico.APROVADO)));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(OS_ID))
                .hasMessageContaining(String.valueOf(idInvalido));

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve lançar IllegalArgumentException quando um dos serviços do input é inválido e outro é válido")
    void deveLancarExcecaoQuandoAlgumServicoNaoPertenceAOs() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(buildOs(List.of(exec)));

        Long idInvalido = 999L;
        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId, List.of(
                new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(EXECUCAO_ID, StatusExecucaoServico.APROVADO),
                new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(idInvalido, StatusExecucaoServico.APROVADO)));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(idInvalido));

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve lançar IllegalStateException quando todos os serviços do input são RECUSADO")
    void deveLancarExcecaoQuandoTodosServicosForemRecusados() {
        stubFuncionario();
        ExecucaoServico exec1 = buildExecucao(EXECUCAO_ID);
        ExecucaoServico exec2 = buildExecucao(11L);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(buildOs(List.of(exec1, exec2)));

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId, List.of(
                new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(EXECUCAO_ID, StatusExecucaoServico.RECUSADO),
                new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(11L, StatusExecucaoServico.RECUSADO)));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pelo menos um serviço aprovado");

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve aprovar múltiplas execuções com tratamentos distintos")
    void deveProcessarMultiplasExecucoesComStatusDistintos() {
        stubFuncionario();
        Long execId2 = 11L;
        ExecucaoServico execAprovada = buildExecucao(EXECUCAO_ID);
        ExecucaoServico execRecusada = buildExecucao(execId2);

        OrdemServico os = buildOs(new ArrayList<>(List.of(execAprovada, execRecusada)));
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, userId, List.of(
                new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(EXECUCAO_ID, StatusExecucaoServico.APROVADO),
                new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(execId2, StatusExecucaoServico.RECUSADO)
        ));

        useCase.execute(input);

        assertThat(execAprovada.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        assertThat(execRecusada.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
    }
}

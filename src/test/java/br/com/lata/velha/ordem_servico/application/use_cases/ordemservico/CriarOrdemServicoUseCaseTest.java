package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarOrdemServicoUseCaseTest {

    @Mock private CriarOrdemServicoGateway gateway;
    @Mock private NotificarOrdemServicoService notificarService;
    @Mock private Logger logger;

    private CriarOrdemServicoUseCase useCase;

    private Veiculo veiculo;
    private Proprietario proprietario;
    private Funcionario funcionario;
    private OrdemServico savedOs;
    private OrdemServicoProjection projection;
    private UserId userId;
    private CriarOrdemServicoUseCase.Input input;

    @BeforeEach
    void setUp() {
        useCase = new CriarOrdemServicoUseCase(gateway, notificarService, logger);

        userId = UserId.random();

        input = new CriarOrdemServicoUseCase.Input(
                3L, 4L, userId, "Barulho ao frear"
        );

        veiculo = mock(Veiculo.class);
        proprietario = mock(Proprietario.class);

        funcionario = new Funcionario(2L, "Maria Atendente", null, null);

        savedOs = new OrdemServico(
                1L, 4L, 3L, "Barulho ao frear",
                StatusOrdemServico.RECEBIDA,
                LocalDateTime.now(), null, null, null, null,
                2L, null, new ArrayList<>()
        );

        projection = mock(OrdemServicoProjection.class);
        lenient().when(projection.getId()).thenReturn(1L);
    }

    private void stubHappyPath() {
        when(veiculo.getId()).thenReturn(3L);
        when(proprietario.getId()).thenReturn(4L);

        when(gateway.getProprietarioAtivoPorId(4L)).thenReturn(proprietario);
        when(gateway.getVeiculoAtivoDoProprietario(3L, 4L)).thenReturn(veiculo);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(funcionario);
        when(gateway.salvarOrdemServico(any(OrdemServico.class))).thenReturn(savedOs);
        when(gateway.getOrdemServicoProjectionById(1L)).thenReturn(projection);
    }

    @Test
    @DisplayName("deve criar OrdemServico com sucesso")
    void deveCriarOrdemServicoComSucesso() {
        stubHappyPath();

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isEqualTo(1L);

        verify(gateway).salvarOrdemServico(any(OrdemServico.class));
        verify(notificarService).execute(savedOs);
    }

    @Test
    @DisplayName("deve criar entidade com dados corretos")
    void deveCriarEntidadeComDadosCorretos() {
        stubHappyPath();

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);

        useCase.execute(input);

        verify(gateway).salvarOrdemServico(captor.capture());

        OrdemServico created = captor.getValue();

        assertThat(created.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(created.getProprietarioId()).isEqualTo(4L);
        assertThat(created.getVeiculoId()).isEqualTo(3L);
        assertThat(created.getAtendenteInicioId()).isEqualTo(2L);
        assertThat(created.getReclamacaoProprietario()).isEqualTo("Barulho ao frear");
    }

    @Test
    @DisplayName("deve chamar notificação após salvar")
    void deveChamarNotificacaoAposSalvar() {
        stubHappyPath();

        useCase.execute(input);

        verify(notificarService).execute(savedOs);
    }

    @Test
    @DisplayName("deve propagar exceção quando proprietário não encontrado")
    void deveLancarExcecaoQuandoProprietarioNaoEncontrado() {
        when(gateway.getProprietarioAtivoPorId(4L))
                .thenThrow(new RuntimeException("Proprietário não encontrado"));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Proprietário não encontrado");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando veículo não pertence ao proprietário")
    void deveLancarExcecaoQuandoVeiculoNaoPertenceAoProprietario() {
        when(proprietario.getId()).thenReturn(4L);
        when(gateway.getProprietarioAtivoPorId(4L)).thenReturn(proprietario);
        when(gateway.getVeiculoAtivoDoProprietario(3L, 4L))
                .thenThrow(new RuntimeException("Veículo não encontrado"));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Veículo não encontrado");

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando funcionário não encontrado")
    void deveLancarExcecaoQuandoFuncionarioNaoEncontrado() {
        when(proprietario.getId()).thenReturn(4L);
        when(gateway.getProprietarioAtivoPorId(4L)).thenReturn(proprietario);
        when(gateway.getVeiculoAtivoDoProprietario(3L, 4L)).thenReturn(veiculo);
        when(gateway.getFuncionarioPorUserId(userId))
                .thenThrow(new RuntimeException("Funcionário não encontrado"));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Funcionário não encontrado");

        verify(gateway, never()).salvarOrdemServico(any());
    }
}

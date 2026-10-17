package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoSemProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.CriarProprietarioGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.NotificarCadastroProprietarioUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.CriarVeiculoGateway;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarOrdemServicoCompletaUseCaseTest {

    @Mock private CriarOrdemServicoGateway criarOrdemServicoGateway;
    @Mock private CriarProprietarioGateway criarProprietarioGateway;
    @Mock private CriarVeiculoGateway criarVeiculoGateway;
    @Mock private AdicionarServicoGateway adicionarServicoGateway;
    @Mock private NotificarOrdemServicoService notificarService;
    @Mock private NotificarCadastroProprietarioUseCase notificarCadastroProprietarioUseCase;
    @Mock private Logger logger;

    private CriarOrdemServicoCompletaUseCase useCase;

    private Proprietario proprietarioSalvo;
    private Veiculo veiculoSalvo;
    private Funcionario funcionario;
    private OrdemServico savedOs;
    private OrdemServicoProjection projection;
    private UserId userId;
    private CriarOrdemServicoCompletaUseCase.Input input;

    @BeforeEach
    void setUp() {
        useCase = new CriarOrdemServicoCompletaUseCase(
                criarOrdemServicoGateway, criarProprietarioGateway, criarVeiculoGateway,
                adicionarServicoGateway, notificarService, notificarCadastroProprietarioUseCase, logger);

        userId = UserId.random();

        var proprietarioRequest = new ProprietarioRequest(
                "João da Silva", "joao@example.com", "359.493.430-69", "(11) 99999-9999", null);
        var veiculoRequest = new VeiculoSemProprietarioRequest("ABC1D23", "Fiat", "Uno", 2020, "Prata");

        input = new CriarOrdemServicoCompletaUseCase.Input(
                proprietarioRequest, veiculoRequest, userId, "Barulho ao frear", List.of());

        proprietarioSalvo = mock(Proprietario.class);
        veiculoSalvo = mock(Veiculo.class);
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
        when(proprietarioSalvo.getId()).thenReturn(4L);
        when(veiculoSalvo.getId()).thenReturn(3L);

        when(criarProprietarioGateway.salvarProprietario(any(Proprietario.class))).thenReturn(proprietarioSalvo);
        when(criarVeiculoGateway.getProprietarioAtivoPorId(4L)).thenReturn(proprietarioSalvo);
        when(criarVeiculoGateway.salvarVeiculo(any(Veiculo.class))).thenReturn(veiculoSalvo);
        when(criarOrdemServicoGateway.getFuncionarioPorUserId(userId)).thenReturn(funcionario);
        when(criarOrdemServicoGateway.salvarOrdemServico(any(OrdemServico.class))).thenReturn(savedOs);
        when(criarOrdemServicoGateway.getOrdemServicoProjectionById(1L)).thenReturn(projection);
    }

    @Test
    @DisplayName("deve cadastrar proprietário, veículo e criar OS com sucesso")
    void deveCriarOrdemServicoCompletaComSucesso() {
        stubHappyPath();

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isEqualTo(1L);

        verify(criarProprietarioGateway).salvarProprietario(any(Proprietario.class));
        verify(criarVeiculoGateway).salvarVeiculo(any(Veiculo.class));
        verify(criarOrdemServicoGateway).salvarOrdemServico(any(OrdemServico.class));
        verify(notificarCadastroProprietarioUseCase).execute(proprietarioSalvo);
        verify(notificarService).execute(savedOs);
    }

    @Test
    @DisplayName("deve vincular o veículo ao proprietário recém-criado")
    void deveVincularVeiculoAoProprietarioCriado() {
        stubHappyPath();

        useCase.execute(input);

        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        verify(criarVeiculoGateway).salvarVeiculo(captor.capture());
        assertThat(captor.getValue().getProprietarioId()).isEqualTo(4L);
    }

    @Test
    @DisplayName("deve criar entidade OS com dados corretos")
    void deveCriarEntidadeComDadosCorretos() {
        stubHappyPath();

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);

        useCase.execute(input);

        verify(criarOrdemServicoGateway).salvarOrdemServico(captor.capture());

        OrdemServico created = captor.getValue();
        assertThat(created.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(created.getProprietarioId()).isEqualTo(4L);
        assertThat(created.getVeiculoId()).isEqualTo(3L);
        assertThat(created.getAtendenteInicioId()).isEqualTo(2L);
        assertThat(created.getReclamacaoProprietario()).isEqualTo("Barulho ao frear");
    }

    @Test
    @DisplayName("deve delegar para AdicionarServicoUseCase quando serviços são informados")
    void deveAdicionarServicosQuandoInformados() {
        stubHappyPath();

        var servicos = List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(20L, List.of(), BigDecimal.TEN),
                new AdicionarServicoUseCase.Input.ServicoAdicionar(21L, List.of(), BigDecimal.valueOf(50))
        );
        var inputComServicos = new CriarOrdemServicoCompletaUseCase.Input(
                input.proprietario(), input.veiculo(), userId, "Barulho ao frear", servicos);

        // simula o estado já persistido, com as ExecucaoServico já com Id atribuído pelo banco
        var osComExecucoes = new OrdemServico(1L, 4L, 3L, "Barulho ao frear",
                StatusOrdemServico.RECEBIDA, LocalDateTime.now(), null, null, null, null, 2L, null,
                new ArrayList<>(List.of(
                        new ExecucaoServico(100L, 20L, 1L, StatusExecucaoServico.PENDENTE, BigDecimal.TEN,
                                new HashSet<>(), null, null, null, null, LocalDateTime.now()),
                        new ExecucaoServico(101L, 21L, 1L, StatusExecucaoServico.PENDENTE, BigDecimal.valueOf(50),
                                new HashSet<>(), null, null, null, null, LocalDateTime.now())
                )));

        when(adicionarServicoGateway.getOrdemServicoPorId(1L)).thenReturn(savedOs);
        when(adicionarServicoGateway.getServicosAtivosPorIds(any())).thenReturn(List.of(
                new Servico(20L, "Troca de óleo", "desc"),
                new Servico(21L, "Alinhamento", "desc")
        ));
        when(adicionarServicoGateway.salvarOrdemServico(any(OrdemServico.class))).thenReturn(osComExecucoes);

        useCase.execute(inputComServicos);

        verify(adicionarServicoGateway, times(2)).salvarOrdemServico(any(OrdemServico.class));
    }
}

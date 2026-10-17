package br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoGateway;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.FinalizarDiagnosticoGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.FinalizarDiagnosticoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.IniciarDiagnosticoGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.IniciarDiagnosticoUseCase;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.*;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:historico-estado-os-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class HistoricoEstadoOsIT {

    @Autowired private IniciarDiagnosticoGateway iniciarDiagnosticoGateway;
    @Autowired private FinalizarDiagnosticoGateway finalizarDiagnosticoGateway;
    @Autowired private NotificarOrdemServicoGateway notificarGateway;
    @Autowired private BuscarHistoricoEstadoOsGateway buscarHistoricoEstadoOsGateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase;
    private BuscarHistoricoEstadoUseCase buscarHistoricoEstadoUseCase;

    private Long mecanicoId;
    private UUID mecanicoUserId;
    private Long osId;

    @BeforeEach
    void setUp() {
        var notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, emailTemplateProvider, logger);
        iniciarDiagnosticoUseCase = new IniciarDiagnosticoUseCase(iniciarDiagnosticoGateway, notificarService, logger);
        finalizarDiagnosticoUseCase = new FinalizarDiagnosticoUseCase(finalizarDiagnosticoGateway, notificarService, logger);
        buscarHistoricoEstadoUseCase = new BuscarHistoricoEstadoUseCase(buscarHistoricoEstadoOsGateway, logger);

        RoleEntity role = new RoleEntity(null, "MECANICO");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("MECANICO");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        FuncionarioEntity mecanico = new FuncionarioEntity();
        mecanico.setNome("Carlos Mecânico");
        mecanico.setCargo(cargo);
        mecanicoUserId = UUID.randomUUID();
        mecanico.setUserId(mecanicoUserId);
        em.persist(mecanico);
        mecanicoId = mecanico.getId();

        ProprietarioEntity proprietario = new ProprietarioEntity();
        proprietario.setNome("João Proprietário");
        proprietario.setEmail("joao@example.com");
        proprietario.setDocumento("35949343069");
        proprietario.setNumeroCelular("11999999999");
        proprietario.setAtivo(true);
        em.persist(proprietario);

        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setProprietario(proprietario);
        veiculo.setPlaca("ABC1D23");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2021);
        veiculo.setCor("Branco");
        veiculo.setAtivo(true);
        em.persist(veiculo);

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setProprietarioId(proprietario.getId());
        os.setVeiculoId(veiculo.getId());
        os.setReclamacaoProprietario("Barulho ao frear");
        os.setStatus(StatusOrdemServico.RECEBIDA);
        os.setAtendenteInicioId(mecanicoId);
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);
        osId = os.getId();

        ServicoEntity servico = new ServicoEntity();
        servico.setNome("Troca de óleo");
        servico.setDescricao("Troca completa");
        em.persist(servico);

        ExecucaoServicoEntity execucao = new ExecucaoServicoEntity();
        execucao.setOrdemServico(os);
        execucao.setServico(servico);
        execucao.setStatusExecucaoServico(StatusExecucaoServico.PENDENTE);
        execucao.setValorMaoDeObra(new BigDecimal("100.00"));
        execucao.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve abrir um registro de histórico ao transicionar o estado da OS")
    void deveAbrirHistoricoAoTransicionar() {
        iniciarDiagnosticoUseCase.execute(new IniciarDiagnosticoUseCase.Input(osId, UserId.create(mecanicoUserId)));
        em.flush();
        em.clear();

        List<HistoricoEstadoOsEntity> historico = historicoDaOs();
        assertThat(historico).hasSize(1);
        assertThat(historico.get(0).getEstadoOs()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO.name());
        assertThat(historico.get(0).getOsId()).isEqualTo(osId);
        assertThat(historico.get(0).getDataInicio()).isNotNull();
        assertThat(historico.get(0).getDataFim()).isNull();
    }

    @Test
    @DisplayName("deve fechar o estado anterior e abrir o novo de forma contígua")
    void deveFecharEstadoAnteriorEAbrirNovo() {
        iniciarDiagnosticoUseCase.execute(new IniciarDiagnosticoUseCase.Input(osId, UserId.create(mecanicoUserId)));
        em.flush();
        em.clear();

        finalizarDiagnosticoUseCase.execute(new FinalizarDiagnosticoUseCase.Input(osId, UserId.create(mecanicoUserId)));
        em.flush();
        em.clear();

        List<HistoricoEstadoOsEntity> historico = historicoDaOs();
        assertThat(historico).hasSize(2);

        var emDiagnostico = historico.stream()
                .filter(h -> h.getEstadoOs().equals(StatusOrdemServico.EM_DIAGNOSTICO.name()))
                .findFirst().orElseThrow();
        var aguardando = historico.stream()
                .filter(h -> h.getEstadoOs().equals(StatusOrdemServico.AGUARDANDO_APROVACAO.name()))
                .findFirst().orElseThrow();

        assertThat(emDiagnostico.getDataFim()).isNotNull();
        assertThat(aguardando.getDataFim()).isNull();
        assertThat(emDiagnostico.getDataFim()).isEqualTo(aguardando.getDataInicio());
    }

    @Test
    @DisplayName("deve calcular o tempo médio por estado no intervalo informado")
    void deveCalcularTempoMedioPorEstado() {
        var base = LocalDateTime.of(2026, 1, 10, 8, 0, 0);
        // EM_DIAGNOSTICO: 60s e 120s -> média 90s
        persistirHistorico("EM_DIAGNOSTICO", base, base.plusSeconds(60));
        persistirHistorico("EM_DIAGNOSTICO", base.plusMinutes(10), base.plusMinutes(10).plusSeconds(120));
        // APROVADA: 30s -> média 30s
        persistirHistorico("APROVADA", base.plusHours(1), base.plusHours(1).plusSeconds(30));
        // fora do intervalo consultado
        persistirHistorico("EM_DIAGNOSTICO", base.minusDays(5), base.minusDays(5).plusSeconds(9999));
        em.flush();
        em.clear();

        var resultado = buscarHistoricoEstadoUseCase.execute(
                new BuscarHistoricoEstadoUseCase.Input(base.minusMinutes(1), base.plusHours(2)));

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).estadoOs()).isEqualTo("APROVADA");
        assertThat(resultado.get(0).segundosMedios()).isEqualTo(30.0);
        assertThat(resultado.get(1).estadoOs()).isEqualTo("EM_DIAGNOSTICO");
        assertThat(resultado.get(1).segundosMedios()).isEqualTo(90.0);
    }

    @Test
    @DisplayName("deve exigir ao menos uma data (inicio ou fim)")
    void deveExigirAoMenosUmaData() {
        var input = new BuscarHistoricoEstadoUseCase.Input(null, null);
        assertThatThrownBy(() -> buscarHistoricoEstadoUseCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ao menos uma data");
    }

    private List<HistoricoEstadoOsEntity> historicoDaOs() {
        return em.createQuery(
                        "SELECT h FROM HistoricoEstadoOsEntity h WHERE h.osId = :osId ORDER BY h.dataInicio",
                        HistoricoEstadoOsEntity.class)
                .setParameter("osId", osId)
                .getResultList();
    }

    private void persistirHistorico(String estado, LocalDateTime inicio, LocalDateTime fim) {
        var entity = new HistoricoEstadoOsEntity();
        entity.setOsId(osId);
        entity.setEstadoOs(estado);
        entity.setDataInicio(inicio);
        entity.setDataFim(fim);
        em.persist(entity);
    }
}

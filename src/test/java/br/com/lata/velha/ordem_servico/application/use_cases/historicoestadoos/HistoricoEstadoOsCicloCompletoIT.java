package br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.ordem_servico.application.controllers.ordemservico.OrdemServicoCleanController;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AdicionarServicoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AprovarOrdemServicoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.FinalizarDiagnosticoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.FinalizarServicoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.IniciarDiagnosticoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.IniciarServicoUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.TempoMedioPorEstado;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.*;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways.OrdemServicoGatewayImpl;
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

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:historico-ciclo-completo-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class HistoricoEstadoOsCicloCompletoIT {

    /** Sequência de estados esperada de um ciclo de vida completo da OS. */
    private static final List<String> FLUXO_COMPLETO = List.of(
            StatusOrdemServico.RECEBIDA.name(),
            StatusOrdemServico.EM_DIAGNOSTICO.name(),
            StatusOrdemServico.AGUARDANDO_APROVACAO.name(),
            StatusOrdemServico.APROVADA.name(),
            StatusOrdemServico.EM_EXECUCAO.name(),
            StatusOrdemServico.FINALIZADA.name(),
            StatusOrdemServico.ENTREGUE.name()
    );

    @Autowired private OrdemServicoCleanController cleanController;
    @Autowired private OrdemServicoGatewayImpl ordemServicoGateway;
    @Autowired private BuscarHistoricoEstadoOsGateway buscarHistoricoEstadoOsGateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private BuscarHistoricoEstadoUseCase buscarHistoricoEstadoUseCase;

    private Long proprietarioId;
    private Long veiculoId;
    private Long servicoId;
    private Long funcionarioId;
    private UserId funcionarioUserId;

    @BeforeEach
    void setUp() {
        buscarHistoricoEstadoUseCase = new BuscarHistoricoEstadoUseCase(buscarHistoricoEstadoOsGateway, logger);

        RoleEntity role = new RoleEntity(null, "MECANICO");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("MECANICO");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setNome("Carlos Mecânico");
        funcionario.setCargo(cargo);
        UUID userId = UUID.randomUUID();
        funcionario.setUserId(userId);
        em.persist(funcionario);
        funcionarioId = funcionario.getId();
        funcionarioUserId = UserId.create(userId);

        ProprietarioEntity proprietario = new ProprietarioEntity();
        proprietario.setNome("João Proprietário");
        proprietario.setEmail("joao@example.com");
        proprietario.setDocumento("35949343069");
        proprietario.setNumeroCelular("11999999999");
        proprietario.setAtivo(true);
        em.persist(proprietario);
        proprietarioId = proprietario.getId();

        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setProprietario(proprietario);
        veiculo.setPlaca("ABC1D23");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2021);
        veiculo.setCor("Branco");
        veiculo.setAtivo(true);
        em.persist(veiculo);
        veiculoId = veiculo.getId();

        ServicoEntity servico = new ServicoEntity();
        servico.setNome("Troca de óleo");
        servico.setDescricao("Troca completa");
        em.persist(servico);
        servicoId = servico.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve registrar todo o histórico de estados ao longo do ciclo de vida da OS")
    void deveRegistrarHistoricoCompletoDuranteOCicloDeVida() {
        Long osId = rodarCicloCompleto();

        List<HistoricoEstadoOsEntity> historico = historicoDaOs(osId);

        assertThat(historico).extracting(HistoricoEstadoOsEntity::getEstadoOs)
                .containsExactlyElementsOf(FLUXO_COMPLETO);
        assertThat(historico).allSatisfy(h -> assertThat(h.getOsId()).isEqualTo(osId));

        // Todos os estados, menos o último, estão fechados e são contíguos.
        for (int i = 0; i < historico.size() - 1; i++) {
            var atual = historico.get(i);
            var proximo = historico.get(i + 1);
            assertThat(atual.getDataFim())
                    .as("estado %s deve estar fechado", atual.getEstadoOs())
                    .isNotNull();
            assertThat(atual.getDataFim())
                    .as("fim de %s deve coincidir com o início de %s", atual.getEstadoOs(), proximo.getEstadoOs())
                    .isEqualTo(proximo.getDataInicio());
        }
        // O estado atual (ENTREGUE) permanece aberto.
        assertThat(historico.get(historico.size() - 1).getDataFim()).isNull();
    }

    @Test
    @DisplayName("deve calcular o tempo médio por estado a partir do histórico gerado por ciclos completos")
    void deveCalcularTempoMedioPorEstadoAPartirDoHistoricoDoCiclo() {
        Long os1 = rodarCicloCompleto();
        Long os2 = rodarCicloCompleto();

        // Reescreve os instantes para durações conhecidas: 60s por estado na os1,
        // 120s por estado na os2 -> média esperada de 90s em cada estado.
        var base = LocalDateTime.of(2026, 3, 1, 8, 0, 0);
        reescreverDuracoes(os1, base, 60);
        reescreverDuracoes(os2, base, 120);
        em.flush();
        em.clear();

        List<TempoMedioPorEstado> resultado = buscarHistoricoEstadoUseCase.execute(
                new BuscarHistoricoEstadoUseCase.Input(base.minusDays(1), base.plusDays(1)));

        assertThat(resultado).extracting(TempoMedioPorEstado::estadoOs)
                .containsExactlyInAnyOrderElementsOf(FLUXO_COMPLETO);
        assertThat(resultado).allSatisfy(tempo ->
                assertThat(tempo.segundosMedios())
                        .as("tempo médio do estado %s", tempo.estadoOs())
                        .isEqualTo(90.0));
    }

    /* ================== helpers ================== */

    private Long rodarCicloCompleto() {
        var ordemServico = OrdemServico.create(proprietarioId, veiculoId, "Barulho ao frear", funcionarioId);
        Long osId = ordemServicoGateway.salvarOrdemServico(ordemServico).getId();
        flushClear();

        cleanController.iniciarDiagnostico(new IniciarDiagnosticoUseCase.Input(osId, funcionarioUserId));
        flushClear();

        cleanController.adicionarServico(new AdicionarServicoUseCase.Input(osId, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId, List.of(), new BigDecimal("150.00")))));
        flushClear();

        cleanController.finalizarDiagnostico(new FinalizarDiagnosticoUseCase.Input(osId, funcionarioUserId));
        flushClear();

        Long execucaoId = execucaoDaOs(osId);
        cleanController.aprovar(new AprovarOrdemServicoUseCase.Input(osId, funcionarioUserId,
                List.of(new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(execucaoId, StatusExecucaoServico.APROVADO))));
        flushClear();

        cleanController.iniciarServico(new IniciarServicoUseCase.Input(osId, execucaoId, funcionarioUserId));
        flushClear();

        cleanController.finalizarServico(new FinalizarServicoUseCase.Input(osId, execucaoId, funcionarioUserId));
        flushClear();

        cleanController.retirarVeiculo(osId, funcionarioUserId);
        flushClear();

        var osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
        return osId;
    }

    private void reescreverDuracoes(Long osId, LocalDateTime base, int segundosPorEstado) {
        var historico = historicoDaOs(osId);
        for (int i = 0; i < historico.size(); i++) {
            var entrada = historico.get(i);
            entrada.setDataInicio(base.plusSeconds((long) i * segundosPorEstado));
            entrada.setDataFim(base.plusSeconds((long) (i + 1) * segundosPorEstado));
            em.merge(entrada);
        }
    }

    private Long execucaoDaOs(Long osId) {
        return em.createQuery(
                        "SELECT e.id FROM ExecucaoServicoEntity e WHERE e.ordemServicoId = :osId",
                        Long.class)
                .setParameter("osId", osId)
                .getSingleResult();
    }

    private List<HistoricoEstadoOsEntity> historicoDaOs(Long osId) {
        return em.createQuery(
                        "SELECT h FROM HistoricoEstadoOsEntity h WHERE h.osId = :osId ORDER BY h.dataInicio, h.id",
                        HistoricoEstadoOsEntity.class)
                .setParameter("osId", osId)
                .getResultList();
    }

    private void flushClear() {
        em.flush();
        em.clear();
    }
}

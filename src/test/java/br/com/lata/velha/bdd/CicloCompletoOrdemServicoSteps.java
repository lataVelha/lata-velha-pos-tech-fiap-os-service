package br.com.lata.velha.bdd;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.ordem_servico.application.controllers.ordemservico.OrdemServicoCleanController;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AdicionarServicoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.FinalizarDiagnosticoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.FinalizarServicoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.IniciarDiagnosticoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.IniciarServicoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberInventoryCheckedUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberOrcamentoAprovadoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberOrcamentoReprovadoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberPaymentDoneUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.RegistrarCobrancaEmitidaUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ExecucaoServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.FuncionarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ProprietarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.VeiculoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways.OrdemServicoGatewayImpl;
import br.com.lata.velha.shared.application.gateways.EventPublisher;
import br.com.lata.velha.shared.domain.events.CobrancaEmitidaEvent;
import br.com.lata.velha.shared.domain.events.DomainEvent;
import br.com.lata.velha.shared.domain.events.InventoryCheckedEvent;
import br.com.lata.velha.shared.domain.events.OrcamentoAprovadoEvent;
import br.com.lata.velha.shared.domain.events.OrcamentoReprovadoEvent;
import br.com.lata.velha.shared.domain.events.PaymentDoneEvent;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import jakarta.persistence.EntityManager;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class CicloCompletoOrdemServicoSteps {

    @Autowired private OrdemServicoCleanController cleanController;
    @Autowired private OrdemServicoGatewayImpl ordemServicoGateway;
    @Autowired private ReceberOrcamentoAprovadoUseCase receberOrcamentoAprovado;
    @Autowired private ReceberOrcamentoReprovadoUseCase receberOrcamentoReprovado;
    @Autowired private ReceberInventoryCheckedUseCase receberInventoryChecked;
    @Autowired private RegistrarCobrancaEmitidaUseCase registrarCobrancaEmitida;
    @Autowired private ReceberPaymentDoneUseCase receberPaymentDone;
    @Autowired private EventPublisher eventPublisher;
    @Autowired private EntityManager em;
    @Autowired private TransactionTemplate tx;

    private Long proprietarioId;
    private Long veiculoId;
    private Long servicoId;
    private Long funcionarioId;
    private UserId funcionarioUserId;
    private Long osId;
    private Long execucaoId;

    @Dado("um proprietário com um veículo cadastrado")
    public void umProprietarioComVeiculo() {
        var sequencia = SEQUENCIA.incrementAndGet();
        tx.executeWithoutResult(status -> {
            var proprietario = new ProprietarioEntity();
            proprietario.setNome("João Proprietário");
            proprietario.setEmail("joao" + sequencia + "@example.com");
            proprietario.setDocumento(documentoUnico(sequencia));
            proprietario.setNumeroCelular("11999999999");
            proprietario.setAtivo(true);
            em.persist(proprietario);
            proprietarioId = proprietario.getId();

            var veiculo = new VeiculoEntity();
            veiculo.setProprietario(proprietario);
            veiculo.setPlaca(placaUnica(sequencia));
            veiculo.setMarca("Toyota");
            veiculo.setModelo("Corolla");
            veiculo.setAno(2021);
            veiculo.setCor("Branco");
            veiculo.setAtivo(true);
            em.persist(veiculo);
            veiculoId = veiculo.getId();
        });
    }

    @Dado("um serviço de {string} disponível no catálogo")
    public void umServicoDisponivel(String nome) {
        tx.executeWithoutResult(status -> {
            var servico = new ServicoEntity();
            servico.setNome(nome);
            servico.setDescricao("Cadastrado pelo cenário BDD");
            em.persist(servico);
            servicoId = servico.getId();
        });
    }

    @Dado("um mecânico responsável pelo atendimento")
    public void umMecanicoResponsavel() {
        tx.executeWithoutResult(status -> {
            var cargo = em.createQuery(
                            "SELECT c FROM CargoEntity c WHERE c.nome = :nome", CargoEntity.class)
                    .setParameter("nome", "MECANICO")
                    .getResultStream()
                    .findFirst()
                    .orElseGet(() -> {
                        var role = new RoleEntity(null, "MECANICO");
                        em.persist(role);
                        var novo = new CargoEntity();
                        novo.setNome("MECANICO");
                        novo.setRoles(Set.of(role));
                        em.persist(novo);
                        return novo;
                    });

            var funcionario = new FuncionarioEntity();
            funcionario.setNome("Carlos Mecânico");
            funcionario.setCargo(cargo);
            var userId = UUID.randomUUID();
            funcionario.setUserId(userId);
            em.persist(funcionario);
            funcionarioId = funcionario.getId();
            funcionarioUserId = UserId.create(userId);
        });
    }

    @Quando("o atendente registra a entrada do veículo na oficina")
    public void registraEntradaDoVeiculo() {
        tx.executeWithoutResult(status -> {
            var ordemServico = OrdemServico.create(proprietarioId, veiculoId, "Barulho ao frear", funcionarioId);
            osId = ordemServicoGateway.salvarOrdemServico(ordemServico).getId();
        });
    }

    @Quando("o mecânico inicia o diagnóstico")
    public void iniciaDiagnostico() {
        tx.executeWithoutResult(status ->
                cleanController.iniciarDiagnostico(new IniciarDiagnosticoUseCase.Input(osId, funcionarioUserId)));
    }

    @Quando("o mecânico adiciona o serviço com {int} peças e mão de obra de {bigdecimal}")
    public void adicionaServico(int quantidade, BigDecimal valorMaoDeObra) {
        tx.executeWithoutResult(status -> cleanController.adicionarServico(
                new AdicionarServicoUseCase.Input(osId, List.of(
                        new AdicionarServicoUseCase.Input.ServicoAdicionar(
                                servicoId,
                                List.of(new AdicionarServicoUseCase.Input.PecaNecessaria(PECA_ID, quantidade)),
                                valorMaoDeObra)))));
    }

    @Quando("o mecânico finaliza o diagnóstico")
    public void finalizaDiagnostico() {
        tx.executeWithoutResult(status ->
                cleanController.finalizarDiagnostico(new FinalizarDiagnosticoUseCase.Input(osId, funcionarioUserId)));
        execucaoId = execucaoDaOs();
    }

    @Quando("o BillingService informa que o cliente aprovou o orçamento")
    public void billingAprovaOrcamento() {
        tx.executeWithoutResult(status -> receberOrcamentoAprovado.execute(
                new OrcamentoAprovadoEvent(osId, new BigDecimal("240.00"),
                        List.of(new OrcamentoAprovadoEvent.Item(execucaoId, PECA_ID, 2)))));
    }

    @Quando("o BillingService informa que o cliente reprovou o orçamento")
    public void billingReprovaOrcamento() {
        tx.executeWithoutResult(status ->
                receberOrcamentoReprovado.execute(new OrcamentoReprovadoEvent(osId)));
    }

    @Quando("o InventoryService confirma que há peças disponíveis")
    public void inventoryConfirmaDisponibilidade() {
        tx.executeWithoutResult(status -> receberInventoryChecked.execute(
                new InventoryCheckedEvent(osId,
                        List.of(new InventoryCheckedEvent.ItemDisponibilidade(execucaoId, true)))));
    }

    @Quando("o mecânico inicia a execução do serviço")
    public void iniciaExecucaoDoServico() {
        tx.executeWithoutResult(status -> cleanController.iniciarServico(
                new IniciarServicoUseCase.Input(osId, execucaoId, funcionarioUserId)));
    }

    @Quando("o mecânico finaliza a execução do serviço")
    public void finalizaExecucaoDoServico() {
        tx.executeWithoutResult(status -> cleanController.finalizarServico(
                new FinalizarServicoUseCase.Input(osId, execucaoId, funcionarioUserId)));
    }

    @Quando("o BillingService informa que a cobrança foi emitida")
    public void billingEmiteCobranca() {
        registrarCobrancaEmitida.execute(new CobrancaEmitidaEvent(osId));
    }

    @Quando("o BillingService informa que o pagamento foi concluído")
    public void billingConfirmaPagamento() {
        tx.executeWithoutResult(status -> receberPaymentDone.execute(new PaymentDoneEvent(osId)));
    }

    @Quando("o cliente retira o veículo")
    public void clienteRetiraVeiculo() {
        tx.executeWithoutResult(status -> cleanController.retirarVeiculo(osId, funcionarioUserId));
    }

    @Entao("a ordem de serviço fica {string}")
    public void aOrdemDeServicoFica(String status) {
        assertThat(statusAtual()).isEqualTo(StatusOrdemServico.valueOf(status));
    }

    @Entao("a ordem de serviço continua {string}")
    public void aOrdemDeServicoContinua(String status) {
        aOrdemDeServicoFica(status);
    }

    @Entao("a execução do serviço fica {string}")
    public void aExecucaoDoServicoFica(String status) {
        var atual = tx.execute(ignored ->
                em.find(ExecucaoServicoEntity.class, execucaoId).getStatusExecucaoServico().name());
        assertThat(atual).isEqualTo(status);
    }

    @Entao("o veículo não pode ser retirado")
    public void oVeiculoNaoPodeSerRetirado() {
        assertThatThrownBy(() -> tx.executeWithoutResult(status ->
                cleanController.retirarVeiculo(osId, funcionarioUserId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("antes do pagamento");
        assertThat(statusAtual()).isEqualTo(StatusOrdemServico.FINALIZADA);
    }

    @Entao("o evento {string} é publicado para o BillingService")
    public void oEventoEhPublicadoParaOBilling(String eventType) {
        assertThat(eventosPublicados()).contains(eventType);
    }

    @Entao("o evento {string} é publicado para o InventoryService")
    public void oEventoEhPublicadoParaOInventory(String eventType) {
        assertThat(eventosPublicados()).contains(eventType);
    }

    @Entao("o histórico de estados registra a sequência completa do ciclo de vida")
    public void oHistoricoRegistraSequenciaCompleta() {
        var estados = tx.execute(ignored -> em.createQuery(
                        "SELECT h.estadoOs FROM HistoricoEstadoOsEntity h WHERE h.osId = :osId ORDER BY h.dataInicio, h.id",
                        String.class)
                .setParameter("osId", osId)
                .getResultList());

        assertThat(estados).containsExactly(
                "RECEBIDA", "EM_DIAGNOSTICO", "AGUARDANDO_APROVACAO", "APROVADA",
                "EM_EXECUCAO", "FINALIZADA", "PAGA", "ENTREGUE");
    }

    /* ================== helpers ================== */

    private static final String PECA_ID = "6915f3a1c2b4d5e6f7a8b9c0";

    private StatusOrdemServico statusAtual() {
        return tx.execute(ignored -> em.find(OrdemServicoEntity.class, osId).getStatus());
    }

    private Long execucaoDaOs() {
        return tx.execute(ignored -> em.createQuery(
                        "SELECT e.id FROM ExecucaoServicoEntity e WHERE e.ordemServicoId = :osId", Long.class)
                .setParameter("osId", osId)
                .getSingleResult());
    }

    private List<String> eventosPublicados() {
        var captor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher, atLeastOnce()).publish(captor.capture());
        return captor.getAllValues().stream().map(DomainEvent::eventType).toList();
    }

    private static final AtomicInteger SEQUENCIA = new AtomicInteger();

    private String documentoUnico(int sequencia) {
        return String.format("%011d", 35949343000L + sequencia);
    }

    private String placaUnica(int sequencia) {
        return String.format("BDD%04d", sequencia);
    }
}

package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoGateway;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:iniciar-servico-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class IniciarServicoUseCaseIT {

    @Autowired private IniciarServicoGateway gateway;
    @Autowired private NotificarOrdemServicoGateway notificarGateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private IniciarServicoUseCase useCase;

    private Long osId;
    private Long execucaoId;
    private Long mecanicoId;
    private UUID mecanicoUserId;

    @BeforeEach
    void setUp() {
        var notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, emailTemplateProvider, logger);
        useCase = new IniciarServicoUseCase(gateway, notificarService, logger);

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
        veiculo.setPlaca("XYZ9A99");
        veiculo.setMarca("Honda");
        veiculo.setModelo("Civic");
        veiculo.setAno(2022);
        veiculo.setCor("Prata");
        veiculo.setAtivo(true);
        em.persist(veiculo);

        ServicoEntity servico = new ServicoEntity();
        servico.setNome("Troca de óleo");
        servico.setDescricao("Troca completa de óleo");
        em.persist(servico);

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setProprietarioId(proprietario.getId());
        os.setVeiculoId(veiculo.getId());
        os.setReclamacaoProprietario("Troca de óleo periódica");
        os.setStatus(StatusOrdemServico.APROVADA);
        os.setAtendenteInicioId(1L);
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);
        osId = os.getId();

        ExecucaoServicoEntity execucao = new ExecucaoServicoEntity();
        execucao.setOrdemServico(os);
        execucao.setServico(servico);
        execucao.setStatusExecucaoServico(StatusExecucaoServico.APROVADO);
        execucao.setValorMaoDeObra(new BigDecimal("150.00"));
        execucao.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao);
        execucaoId = execucao.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve iniciar execução e transicionar para EM_EXECUCAO no banco")
    void deveIniciarExecucaoEPersistir() {
        useCase.execute(new IniciarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        ExecucaoServicoEntity execEntity = em.find(ExecucaoServicoEntity.class, execucaoId);
        assertThat(execEntity.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.EM_EXECUCAO);
        assertThat(execEntity.getMecanicoResponsavelId()).isEqualTo(mecanicoId);
        assertThat(execEntity.getIniciadoEm()).isNotNull();
    }

    @Test
    @DisplayName("deve transicionar OS de APROVADA para EM_EXECUCAO no banco")
    void deveTransicionarOsParaEmExecucao() {
        useCase.execute(new IniciarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    @DisplayName("deve manter OS EM_EXECUCAO ao iniciar segundo serviço")
    void deveMantarOsEmExecucaoAoIniciarSegundoServico() {
        ServicoEntity servico2 = new ServicoEntity();
        servico2.setNome("Alinhamento");
        servico2.setDescricao("Alinhamento completo");
        em.persist(servico2);

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        osEntity.setStatus(StatusOrdemServico.EM_EXECUCAO);

        ExecucaoServicoEntity execucao2 = new ExecucaoServicoEntity();
        execucao2.setOrdemServico(osEntity);
        execucao2.setServico(servico2);
        execucao2.setStatusExecucaoServico(StatusExecucaoServico.APROVADO);
        execucao2.setValorMaoDeObra(new BigDecimal("80.00"));
        execucao2.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao2);
        em.flush();

        useCase.execute(new IniciarServicoUseCase.Input(osId, execucao2.getId(), UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        ExecucaoServicoEntity exec2Entity = em.find(ExecucaoServicoEntity.class, execucao2.getId());
        assertThat(exec2Entity.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.EM_EXECUCAO);

        OrdemServicoEntity osAtualizada = em.find(OrdemServicoEntity.class, osId);
        assertThat(osAtualizada.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    @DisplayName("deve iniciar serviço com peças necessárias registradas")
    void deveIniciarServicoComPecasNecessarias() {
        // TODO: a disponibilidade de peças (antes "RESERVADA"/"AGUARDANDO_PECA") agora
        // é responsabilidade do InventoryService (general-flow.md passo 8-10) - a peça
        // necessária registrada aqui não bloqueia mais o início do serviço.
        PecaNecessariaEntity pecaNecessaria = new PecaNecessariaEntity();
        pecaNecessaria.setExecucaoServicoId(execucaoId);
        pecaNecessaria.setPecaId(1L);
        pecaNecessaria.setQuantidade(2);
        pecaNecessaria.setAtualizado(LocalDateTime.now());
        em.persist(pecaNecessaria);
        em.flush();

        useCase.execute(new IniciarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        ExecucaoServicoEntity updatedExec = em.find(ExecucaoServicoEntity.class, execucaoId);
        assertThat(updatedExec.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.EM_EXECUCAO);
    }
}

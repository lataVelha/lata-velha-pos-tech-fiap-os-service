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
        "spring.datasource.url=jdbc:h2:mem:reprovar-os-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class ReprovarOrdemServicoUseCaseIT {

    @Autowired private ReprovarOrdemServicoGateway gateway;
    @Autowired private NotificarOrdemServicoGateway notificarGateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private ReprovarOrdemServicoUseCase useCase;

    private Long osId;
    private Long execucaoId1;
    private Long execucaoId2;
    private Long atendenteId;
    private UUID atendenteUserId;

    @BeforeEach
    void setUp() {
        var notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, emailTemplateProvider, logger);
        useCase = new ReprovarOrdemServicoUseCase(gateway, notificarService, logger);

        RoleEntity role = new RoleEntity(null, "ATENDENTE");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("ATENDENTE");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        FuncionarioEntity atendente = new FuncionarioEntity();
        atendente.setNome("Ana Atendente");
        atendente.setCargo(cargo);
        atendenteUserId = UUID.randomUUID();
        atendente.setUserId(atendenteUserId);
        em.persist(atendente);
        atendenteId = atendente.getId();

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

        ServicoEntity servico1 = new ServicoEntity();
        servico1.setNome("Troca de óleo");
        servico1.setDescricao("Troca completa de óleo");
        em.persist(servico1);

        ServicoEntity servico2 = new ServicoEntity();
        servico2.setNome("Alinhamento");
        servico2.setDescricao("Alinhamento completo");
        em.persist(servico2);

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setProprietarioId(proprietario.getId());
        os.setVeiculoId(veiculo.getId());
        os.setReclamacaoProprietario("Manutenção periódica");
        os.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        os.setAtendenteInicioId(atendenteId);
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);
        osId = os.getId();

        ExecucaoServicoEntity execucao1 = new ExecucaoServicoEntity();
        execucao1.setOrdemServico(os);
        execucao1.setServico(servico1);
        execucao1.setStatusExecucaoServico(StatusExecucaoServico.PENDENTE);
        execucao1.setValorMaoDeObra(new BigDecimal("150.00"));
        execucao1.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao1);
        execucaoId1 = execucao1.getId();

        ExecucaoServicoEntity execucao2 = new ExecucaoServicoEntity();
        execucao2.setOrdemServico(os);
        execucao2.setServico(servico2);
        execucao2.setStatusExecucaoServico(StatusExecucaoServico.PENDENTE);
        execucao2.setValorMaoDeObra(new BigDecimal("80.00"));
        execucao2.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao2);
        execucaoId2 = execucao2.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve reprovar OS e persistir status REPROVADA")
    void deveReprovarOsEPersistir() {
        useCase.execute(new ReprovarOrdemServicoUseCase.Input(osId, UserId.create(atendenteUserId)));

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.REPROVADA);
    }

    @Test
    @DisplayName("deve reprovar todos os serviços com status RECUSADO")
    void deveRecusarTodosOsServicos() {
        useCase.execute(new ReprovarOrdemServicoUseCase.Input(osId, UserId.create(atendenteUserId)));

        em.flush();
        em.clear();

        ExecucaoServicoEntity exec1 = em.find(ExecucaoServicoEntity.class, execucaoId1);
        ExecucaoServicoEntity exec2 = em.find(ExecucaoServicoEntity.class, execucaoId2);
        assertThat(exec1.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.RECUSADO);
        assertThat(exec2.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.RECUSADO);
    }

    @Test
    @DisplayName("deve registrar finalizadoEm ao reprovar OS")
    void deveRegistrarFinalizadoEmAoReprovar() {
        useCase.execute(new ReprovarOrdemServicoUseCase.Input(osId, UserId.create(atendenteUserId)));

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getFinalizadoEm()).isNotNull();
    }
}

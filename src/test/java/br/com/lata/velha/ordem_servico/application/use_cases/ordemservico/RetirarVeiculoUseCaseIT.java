package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoGateway;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
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

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:retirar-veiculo-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class RetirarVeiculoUseCaseIT {

    @Autowired private RetirarVeiculoGateway gateway;
    @Autowired private NotificarOrdemServicoGateway notificarGateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private RetirarVeiculoUseCase useCase;

    private Long atendenteId;
    private UUID atendenteUserId;
    private Long osId;

    @BeforeEach
    void setUp() {
        var notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, emailTemplateProvider, logger);
        useCase = new RetirarVeiculoUseCase(gateway, notificarService, logger);

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

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setProprietarioId(proprietario.getId());
        os.setVeiculoId(veiculo.getId());
        os.setReclamacaoProprietario("Manutenção periódica");
        os.setStatus(StatusOrdemServico.FINALIZADA);
        os.setAtendenteInicioId(atendenteId);
        os.setMecanicoResponsavelId(atendenteId);
        os.setFinalizadoEm(LocalDateTime.now());
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);
        osId = os.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve alterar status para ENTREGUE quando OS está FINALIZADA e persistir no banco")
    void deveRetirarVeiculoDeOsFinalizadaEPersistir() {
        useCase.execute(osId, UserId.create(atendenteUserId));

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
        assertThat(osEntity.getEntregueEm()).isNotNull();
    }

    @Test
    @DisplayName("deve alterar status para ENTREGUE quando OS está REPROVADA e persistir no banco")
    void deveRetirarVeiculoDeOsReprovadaEPersistir() {
        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        osEntity.setStatus(StatusOrdemServico.REPROVADA);
        em.flush();
        em.clear();

        useCase.execute(osId, UserId.create(atendenteUserId));

        em.flush();
        em.clear();

        OrdemServicoEntity updated = em.find(OrdemServicoEntity.class, osId);
        assertThat(updated.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
        assertThat(updated.getEntregueEm()).isNotNull();
    }

    @Test
    @DisplayName("deve lançar IllegalStateException quando OS não está FINALIZADA nem REPROVADA")
    void deveLancarExcecaoQuandoStatusInvalido() {
        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        osEntity.setStatus(StatusOrdemServico.EM_EXECUCAO);
        em.flush();
        em.clear();

        var atendenteUserIdVo = UserId.create(atendenteUserId);
        assertThatThrownBy(() -> useCase.execute(osId, atendenteUserIdVo))
                .isInstanceOf(IllegalStateException.class);
    }
}

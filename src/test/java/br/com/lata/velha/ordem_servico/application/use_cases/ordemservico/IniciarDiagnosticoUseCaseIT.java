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
        "spring.datasource.url=jdbc:h2:mem:iniciar-diagnostico-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class IniciarDiagnosticoUseCaseIT {

    @Autowired private IniciarDiagnosticoGateway gateway;
    @Autowired private NotificarOrdemServicoGateway notificarGateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private IniciarDiagnosticoUseCase useCase;

    private Long mecanicoId;
    private UUID mecanicoUserId;
    private Long osId;

    @BeforeEach
    void setUp() {
        var notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, emailTemplateProvider, logger);
        useCase = new IniciarDiagnosticoUseCase(gateway, notificarService, logger);

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
        os.setIniciadoEm(LocalDateTime.now());
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);
        osId = os.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve alterar status para EM_DIAGNOSTICO e persistir mecânico responsável")
    void deveIniciarDiagnosticoEPersistirNobanco() {
        useCase.execute(new IniciarDiagnosticoUseCase.Input(osId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        assertThat(osEntity.getMecanicoResponsavelId()).isEqualTo(mecanicoId);
    }

    @Test
    @DisplayName("deve lançar IllegalStateException quando OS não está em status RECEBIDA")
    void deveLancarExcecaoQuandoStatusNaoERecebida() {
        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        osEntity.setStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        em.flush();
        em.clear();

        var input = new IniciarDiagnosticoUseCase.Input(osId, UserId.create(mecanicoUserId));
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RECEBIDA");
    }
}

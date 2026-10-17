package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoGateway;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.FuncionarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ProprietarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.VeiculoEntity;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:criar-os-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class CriarOrdemServicoUseCaseIT {

    @SpyBean
    private OrdemServicoGatewayImpl gatewayImpl;

    @Autowired private CriarOrdemServicoGateway criarGateway;
    @Autowired private NotificarOrdemServicoGateway notificarGateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private CriarOrdemServicoUseCase useCase;

    private Long veiculoId;
    private Long proprietarioId;
    private Long funcionarioId;
    private FuncionarioEntity funcionario;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            OrdemServicoProjection proj = mock(OrdemServicoProjection.class);
            when(proj.getId()).thenReturn(id);
            return proj;
        }).when(gatewayImpl).getOrdemServicoProjectionById(anyLong());

        var notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, emailTemplateProvider, logger);
        useCase = new CriarOrdemServicoUseCase(criarGateway, notificarService, logger);

        RoleEntity role = new RoleEntity(null, "ATENDENTE");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("ATENDENTE");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

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
        veiculo.setMarca("Fiat");
        veiculo.setModelo("Uno");
        veiculo.setAno(2020);
        veiculo.setCor("Branco");
        veiculo.setAtivo(true);
        em.persist(veiculo);
        veiculoId = veiculo.getId();

        funcionario = new FuncionarioEntity();
        funcionario.setNome("Maria Atendente");
        funcionario.setCargo(cargo);
        funcionario.setUserId(UUID.randomUUID());
        em.persist(funcionario);
        funcionarioId = funcionario.getId();

        em.flush();
    }

    @Test
    @DisplayName("deve criar OrdemServico com sucesso e persistir no banco")
    void deveCriarOrdemServicoComSucesso() {
        var funcionarioUserId = UserId.create(funcionario.getUserId());

        var input = new CriarOrdemServicoUseCase.Input(
                veiculoId,
                proprietarioId,
                funcionarioUserId,
                "Barulho ao frear"
        );

        OrdemServicoProjection output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isNotNull();

        em.flush();
        em.clear();

        OrdemServicoEntity entity =
                em.find(OrdemServicoEntity.class, output.getId());

        assertThat(entity).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(entity.getVeiculoId()).isEqualTo(veiculoId);
        assertThat(entity.getProprietarioId()).isEqualTo(proprietarioId);
        assertThat(entity.getAtendenteInicioId()).isEqualTo(funcionarioId);
        assertThat(entity.getReclamacaoProprietario()).isEqualTo("Barulho ao frear");
    }

    @Test
    @DisplayName("deve persistir reclamação do proprietário corretamente")
    void devePersistirReclamacaoProprietario() {
        var funcionarioUserId = UserId.create(funcionario.getUserId());

        var input = new CriarOrdemServicoUseCase.Input(
                veiculoId,
                proprietarioId,
                funcionarioUserId,
                "Motor superaquecendo"
        );

        OrdemServicoProjection output = useCase.execute(input);

        em.flush();
        em.clear();

        OrdemServicoEntity entity =
                em.find(OrdemServicoEntity.class, output.getId());

        assertThat(entity).isNotNull();
        assertThat(entity.getReclamacaoProprietario())
                .isEqualTo("Motor superaquecendo");
    }

    @Test
    @DisplayName("deve persistir atendente e status RECEBIDA corretamente")
    void devePersistirAtendenteEStatus() {
        var funcionarioUserId = UserId.create(funcionario.getUserId());

        var input = new CriarOrdemServicoUseCase.Input(
                veiculoId,
                proprietarioId,
                funcionarioUserId,
                "Freio falhando"
        );

        OrdemServicoProjection output = useCase.execute(input);

        em.flush();
        em.clear();

        OrdemServicoEntity entity =
                em.find(OrdemServicoEntity.class, output.getId());

        assertThat(entity).isNotNull();
        assertThat(entity.getAtendenteInicioId()).isEqualTo(funcionarioId);
        assertThat(entity.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
    }
}

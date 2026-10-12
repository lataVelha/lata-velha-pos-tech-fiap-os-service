package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoGateway;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.FuncionarioNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:finalizar-diagnostico-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class FinalizarDiagnosticoUseCaseIT {

    @Autowired private FinalizarDiagnosticoGateway gateway;
    @Autowired private NotificarOrdemServicoGateway notificarGateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private FinalizarDiagnosticoUseCase useCase;

    private Long funcionarioId;
    private UUID funcionarioUserId;
    private Long osId;

    @BeforeEach
    void setUp() {
        var notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, emailTemplateProvider, logger);
        useCase = new FinalizarDiagnosticoUseCase(gateway, notificarService, logger);

        RoleEntity role = new RoleEntity(null, "MECANICO");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("MECANICO");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setNome("Carlos Mecânico");
        funcionario.setCargo(cargo);
        funcionarioUserId = UUID.randomUUID();
        funcionario.setUserId(funcionarioUserId);
        em.persist(funcionario);
        funcionarioId = funcionario.getId();

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
        os.setStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        os.setAtendenteInicioId(funcionarioId);
        os.setMecanicoResponsavelId(funcionarioId);
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
    @DisplayName("deve alterar status para AGUARDANDO_APROVACAO e persistir no banco")
    void deveFinalizarDiagnosticoEPersistir() {
        useCase.execute(new FinalizarDiagnosticoUseCase.Input(osId, UserId.create(funcionarioUserId)));

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.AGUARDANDO_APROVACAO);
    }

    @Test
    @DisplayName("deve lançar IllegalArgumentException quando userId não corresponde a nenhum funcionário")
    void deveLancarExcecaoQuandoUsuarioNaoEFuncionario() {
        var input = new FinalizarDiagnosticoUseCase.Input(osId, UserId.create(UUID.randomUUID()));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(FuncionarioNotFoundException.class)
                .hasMessageContaining("Funcionario");
    }

    @Test
    @DisplayName("deve lançar IllegalStateException quando OS não está em EM_DIAGNOSTICO")
    void deveLancarExcecaoQuandoStatusNaoEEmDiagnostico() {
        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        osEntity.setStatus(StatusOrdemServico.RECEBIDA);
        em.flush();
        em.clear();
        var input = new FinalizarDiagnosticoUseCase.Input(osId, UserId.create(funcionarioUserId));

        assertThatThrownBy(() ->
                useCase.execute(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");
    }
}

package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.CargoNotFoundException;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:cadastrar-funcionario-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class CadastrarFuncionarioUseCaseIT {

    @Autowired
    private CadastrarFuncionarioGateway gateway;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private Logger logger;

    private CadastrarFuncionarioUseCase useCase;
    private Long cargoId;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarFuncionarioUseCase(gateway, authService, logger);

        RoleEntity role = new RoleEntity(null, "MECANICO");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("MECANICO");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        em.flush();
        cargoId = cargo.getId();
    }

    @Test
    @DisplayName("deve cadastrar funcionário com sucesso e retornar output com dados corretos")
    void shouldRegisterFuncionarioSuccessfully() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "joao@example.com", "Senha1@!", cargoId, "12345678909");
        Funcionario output = useCase.execute(input);

        assertNotNull(output);
        assertNotNull(output.getId());
        assertEquals("João Silva", output.getNome());
        assertEquals("MECANICO", output.getCargo().getNome());
        assertNotNull(output.getUserId());
    }

    @Test
    @DisplayName("deve persistir usuário no banco com o email correto ao cadastrar funcionário")
    void shouldPersistUserWithCorrectEmail() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "joao@example.com", "Senha1@!", cargoId, "12345678909");
        Funcionario output = useCase.execute(input);
        em.flush();

        var savedUser = userJpaRepository.findById(output.getUserId().getValue());

        assertTrue(savedUser.isPresent());
        assertEquals("joao@example.com", savedUser.get().getEmail());
    }

    @Test
    @DisplayName("deve associar o role do cargo ao usuário criado")
    void shouldAssignCargoRolesToUser() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "joao@example.com", "Senha1@!", cargoId, "12345678909");
        useCase.execute(input);
        em.flush();
        em.clear();

        var userWithRoles = userJpaRepository.findByUsernameWithRoles("joao@example.com");

        assertTrue(userWithRoles.isPresent());
        var roles = userWithRoles.get().getRoles();
        assertFalse(roles.isEmpty(), "Usuário deve ter pelo menos uma role");
        assertTrue(
                roles.stream().anyMatch(r -> "MECANICO".equals(r.getNome())),
                "Usuário deve ter a role MECANICO"
        );
    }

    @Test
    @DisplayName("o userId retornado no output deve corresponder ao usuário salvo no banco")
    void shouldReturnUserIdMatchingPersistedUser() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "joao@example.com", "Senha1@!", cargoId, "12345678909");
        Funcionario output = useCase.execute(input);
        em.flush();

        assertTrue(userJpaRepository.existsById(output.getUserId().getValue()));
    }

    @Test
    @DisplayName("deve lançar ResourceAlreadyExistsException ao cadastrar com email duplicado")
    void shouldThrowWhenEmailAlreadyExists() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "duplicado@example.com", "Senha1@!", cargoId, "98765432100");
        useCase.execute(input);

        var duplicate = new CadastrarFuncionarioUseCase.Input("Maria Santos", "duplicado@example.com", "OutraSenha2#", cargoId, "11223344517");

        assertThrows(ResourceAlreadyExistsException.class, () -> useCase.execute(duplicate));
    }

    @Test
    @DisplayName("deve lançar CargoNotFoundException quando cargoId não existe")
    void shouldThrowWhenCargoNotFound() {
        var input = new CadastrarFuncionarioUseCase.Input("Pedro", "pedro@example.com", "Senha1@!", 9999L, "22334455628");

        assertThrows(CargoNotFoundException.class, () -> useCase.execute(input));
    }
}

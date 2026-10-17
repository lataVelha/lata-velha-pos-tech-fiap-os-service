package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.value_objects.Credential;
import br.com.lata.velha.authentication.domain.value_objects.Senha;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.FuncionarioEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioPersistenceMapperTest {

    private FuncionarioPersistenceMapper mapper;
    private PasswordHasher passwordHasher;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        passwordHasher = new PasswordHasher() {
            @Override
            public String hashSenha(Senha senha) {
                return passwordEncoder.encode(senha.getValor());
            }

            @Override
            public boolean match(Credential credential, String rawPassword) {
                return passwordEncoder.matches(rawPassword, credential.getHash());
            }
        };
        mapper = new FuncionarioPersistenceMapper(passwordHasher);
    }

    @Test
    @DisplayName("deve retornar null ao converter entity nula para domain")
    void shouldReturnNullWhenFuncionarioEntityIsNull() {
        assertNull(mapper.toDomain((FuncionarioEntity) null));
    }

    @Test
    @DisplayName("deve converter funcionário com cargo e roles para domain")
    void shouldMapFuncionarioWithCargoAndRolesToDomain() {
        UUID userId = UUID.randomUUID();
        FuncionarioEntity entity = buildFuncionarioEntity(userId, buildCargoEntity());

        Funcionario result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Carlos", result.getNome());
        assertNotNull(result.getUserId());
        assertEquals(userId, result.getUserId().getValue());

        Cargo cargo = result.getCargo();
        assertNotNull(cargo);
        assertEquals(10L, cargo.getId());
        assertEquals("Gerente", cargo.getNome());
        assertEquals(1, cargo.getRoles().size());

        Role role = cargo.getRoles().iterator().next();
        assertNotNull(role.getRoleId());
        assertEquals("ROLE_ADMIN", role.getNome());
    }

    @Test
    @DisplayName("deve retornar null ao converter CargoEntity nula para domain")
    void shouldReturnNullWhenCargoEntityIsNull() {
        assertNull(mapper.toDomain((CargoEntity) null));
    }

    @Test
    @DisplayName("deve converter cargo com roles para domain")
    void shouldMapCargoWithRolesToDomain() {
        Cargo result = mapper.toDomain(buildCargoEntity());

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Gerente", result.getNome());
        assertFalse(result.getRoles().isEmpty());
    }

    @Test
    @DisplayName("deve retornar null ao converter RoleEntity nula para domain")
    void shouldReturnNullWhenRoleEntityIsNull() {
        assertNull(mapper.toDomain((RoleEntity) null));
    }

    @Test
    @DisplayName("deve converter role para domain")
    void shouldMapRoleToDomain() {
        RoleEntity entity = new RoleEntity(UUID.randomUUID(), "ROLE_USER");

        Role result = mapper.toDomain(entity);

        assertNotNull(result);
        assertNotNull(result.getRoleId());
        assertEquals("ROLE_USER", result.getNome());
    }

    @Test
    @DisplayName("deve converter domain para entity")
    void shouldMapDomainToEntity() {
        UUID userId = UUID.randomUUID();
        FuncionarioEntity entity = buildFuncionarioEntity(userId, buildCargoEntity());
        Funcionario domain = mapper.toDomain(entity);

        FuncionarioEntity result = mapper.toEntity(domain);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Carlos", result.getNome());
        assertEquals(userId, result.getUserId());
    }

    private CargoEntity buildCargoEntity() {
        RoleEntity role = new RoleEntity(UUID.randomUUID(), "ROLE_ADMIN");

        CargoEntity cargo = new CargoEntity();
        cargo.setId(10L);
        cargo.setNome("Gerente");
        cargo.setRoles(Set.of(role));
        return cargo;
    }

    private FuncionarioEntity buildFuncionarioEntity(UUID userId, CargoEntity cargo) {
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(100L);
        entity.setNome("Carlos");
        entity.setUserId(userId);
        entity.setCargo(cargo);
        return entity;
    }
}

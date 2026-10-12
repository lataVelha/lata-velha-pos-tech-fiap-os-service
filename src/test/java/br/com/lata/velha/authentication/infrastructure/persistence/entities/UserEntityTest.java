package br.com.lata.velha.authentication.infrastructure.persistence.entities;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.value_objects.Credential;
import br.com.lata.velha.authentication.domain.value_objects.Senha;
import br.com.lata.velha.authentication.domain.value_objects.UserData;
import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.RoleId;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    private static final PasswordHasher PLAIN_HASHER = new PasswordHasher() {
        @Override public String hashSenha(Senha s) { return s.getValor(); }
        @Override public boolean match(Credential cred, String raw) { return raw.equals(cred.getHash()); }
    };

    private UserId userId;
    private Email email;
    private Credential credential;
    private Role role;
    private LocalDateTime criacaoDate;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UserId.random();
        email = Email.fromString("joao@example.com");
        credential = Credential.fromHash("hashed-password", PLAIN_HASHER);
        role = new Role(RoleId.create(UUID.randomUUID()), "ADMIN");
        criacaoDate = LocalDateTime.of(2025, Month.JANUARY, 1, 12, 0);
        var userData = new UserData(userId, "joao", email, true);
        user = new User(userData, credential, Set.of(role), criacaoDate, null);
    }

    @Nested
    @DisplayName("fromDomain")
    class FromDomain {

        @Test
        @DisplayName("deve mapear todos os campos corretamente")
        void shouldMapAllFields() {
            UserEntity entity = UserEntity.fromDomain(user);

            assertNotNull(entity);
            assertEquals(userId.getValue(), entity.getId());
            assertEquals("joao", entity.getUsername());
            assertEquals("joao@example.com", entity.getEmail());
            assertEquals("hashed-password", entity.getCredential());
            assertTrue(entity.isAtivo());
            assertEquals(criacaoDate, entity.getCriacaoDate());
        }

        @Test
        @DisplayName("deve mapear roles corretamente")
        void shouldMapRoles() {
            UserEntity entity = UserEntity.fromDomain(user);

            assertNotNull(entity.getRoles());
            assertEquals(1, entity.getRoles().size());
            assertEquals("ADMIN", entity.getRoles().iterator().next().getNome());
        }

        @Test
        @DisplayName("deve mapear usuário inativo corretamente")
        void shouldMapInactiveUser() {
            user.desativar();
            UserEntity entity = UserEntity.fromDomain(user);

            assertFalse(entity.isAtivo());
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("deve reconstruir o User com os campos corretos")
        void shouldReconstructUserWithCorrectFields() {
            UserEntity entity = UserEntity.fromDomain(user);

            User result = entity.toDomain(PLAIN_HASHER);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            assertEquals("joao", result.getUsername());
            assertEquals(email, result.getEmail());
            assertEquals(criacaoDate, result.getCriacaoDate());
            assertTrue(result.isAtivo());
        }

        @Test
        @DisplayName("deve reconstruir roles corretamente")
        void shouldReconstructRoles() {
            UserEntity entity = UserEntity.fromDomain(user);
            User result = entity.toDomain(PLAIN_HASHER);

            assertNotNull(result.getRoles());
            assertEquals(1, result.getRoles().size());
            assertEquals("ADMIN", result.getRoles().iterator().next().getNome());
        }

        @Test
        @DisplayName("credencial reconstruída deve validar senha original")
        void shouldReconstructValidCredential() {
            UserEntity entity = UserEntity.fromDomain(user);
            User result = entity.toDomain(PLAIN_HASHER);

            assertTrue(result.getCredential().match("hashed-password"));
        }

        @Test
        @DisplayName("roundtrip fromDomain -> toDomain deve preservar todos os campos")
        void roundtripShouldPreserveFields() {
            UserEntity entity = UserEntity.fromDomain(user);
            User reconstructed = entity.toDomain(PLAIN_HASHER);

            assertEquals(user.getId(), reconstructed.getId());
            assertEquals(user.getUsername(), reconstructed.getUsername());
            assertEquals(user.getEmail(), reconstructed.getEmail());
            assertEquals(user.isAtivo(), reconstructed.isAtivo());
            assertEquals(user.getCriacaoDate(), reconstructed.getCriacaoDate());
        }
    }
}

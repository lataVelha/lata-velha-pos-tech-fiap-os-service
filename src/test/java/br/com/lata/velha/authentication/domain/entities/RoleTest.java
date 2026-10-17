package br.com.lata.velha.authentication.domain.entities;

import br.com.lata.velha.shared.domain.value_objects.RoleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private static final RoleId ROLE_ID = RoleId.create(UUID.randomUUID());

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar role com id e nome")
        void shouldCreateRoleWithIdAndNome() {
            Role role = new Role(ROLE_ID, "ADMIN");

            assertEquals(ROLE_ID, role.getRoleId());
            assertEquals("ADMIN", role.getNome());
        }

        @Test
        @DisplayName("deve criar role com id randômico via factory method")
        void shouldCreateRoleWithRandomIdViaFactoryMethod() {
            Role role = Role.create("ADMIN");

            assertNotNull(role.getRoleId());
            assertEquals("ADMIN", role.getNome());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve lançar exceção quando nome é nulo")
        void shouldThrowWhenNomeIsNull() {
            assertThrows(IllegalArgumentException.class, () -> new Role(ROLE_ID, null));
        }

        @Test
        @DisplayName("deve lançar exceção quando nome é vazio")
        void shouldThrowWhenNomeIsEmpty() {
            assertThrows(IllegalArgumentException.class, () -> new Role(ROLE_ID, ""));
        }

        @Test
        @DisplayName("deve lançar exceção quando nome é em branco")
        void shouldThrowWhenNomeIsBlank() {
            assertThrows(IllegalArgumentException.class, () -> new Role(ROLE_ID, "   "));
        }

        @Test
        @DisplayName("deve aceitar nome válido via changeNome")
        void shouldAcceptValidNomeViaChangeNome() {
            Role role = new Role(ROLE_ID, "ADMIN");
            role.changeNome("USER");
            assertEquals("USER", role.getNome());
        }
    }

    // ==================== EQUALS / HASHCODE / TOSTRING ====================

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("roles com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Role role1 = new Role(ROLE_ID, "ADMIN");
            Role role2 = new Role(ROLE_ID, "USER");

            assertEquals(role1, role2);
            assertEquals(role1.hashCode(), role2.hashCode());
        }

        @Test
        @DisplayName("roles com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            var roleId1 = RoleId.create(UUID.randomUUID());
            var roleId2 = RoleId.create(UUID.randomUUID());
            Role role1 = new Role(roleId1, "ADMIN");
            Role role2 = new Role(roleId2, "ADMIN");

            assertNotEquals(role1, role2);
        }

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToSelf() {
            Role role = new Role(ROLE_ID, "ADMIN");
            assertEquals(role, role);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            Role role = new Role(ROLE_ID, "ADMIN");

            var result = role.equals(null);

            assertFalse(result);
        }

        @Test
        @DisplayName("não deve ser igual a tipo diferente")
        void shouldNotBeEqualToDifferentType() {
            Role role = new Role(ROLE_ID, "ADMIN");
            assertNotEquals("string", role);
        }

        @Test
        @DisplayName("toString deve conter roleId e nome")
        void shouldContainRoleIdAndNomeInToString() {
            Role role = new Role(ROLE_ID, "ADMIN");
            String result = role.toString();

            assertTrue(result.contains(ROLE_ID.toString()));
            assertTrue(result.contains("ADMIN"));
        }
    }
}
package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.authentication.domain.entities.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CargoTest {

    private Cargo cargo;
    private Role roleAdmin;
    private Role roleUser;

    @BeforeEach
    void setUp() {
        roleAdmin = Role.create("ADMIN");
        roleUser = Role.create("USER");
        cargo = new Cargo(1L, "Administrador", Set.of(roleAdmin, roleUser));
    }

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar cargo com roles")
        void shouldCreateCargoWithRoles() {
            assertEquals(1L, cargo.getId());
            assertEquals("Administrador", cargo.getNome());
            assertEquals(2, cargo.getRoles().size());
        }

        @Test
        @DisplayName("deve criar cargo vazio com set de roles inicializado")
        void shouldCreateEmptyCargoWithInitializedRoles() {
            Cargo empty = new Cargo();

            assertNull(empty.getId());
            assertNull(empty.getNome());
            assertNotNull(empty.getRoles());
            assertTrue(empty.getRoles().isEmpty());
        }

        @Test
        @DisplayName("deve criar cargo com roles nulas como set vazio")
        void shouldCreateCargoWithNullRolesAsEmptySet() {
            Cargo newCargo = new Cargo(2L, "Teste", null);

            assertNotNull(newCargo.getRoles());
            assertTrue(newCargo.getRoles().isEmpty());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve rejeitar nome nulo")
        void shouldRejectNullNome() {
            assertThrows(IllegalArgumentException.class, () -> cargo.setNome(null));
        }

        @Test
        @DisplayName("deve rejeitar nome vazio")
        void shouldRejectEmptyNome() {
            assertThrows(IllegalArgumentException.class, () -> cargo.setNome(""));
        }

        @Test
        @DisplayName("deve rejeitar nome em branco")
        void shouldRejectBlankNome() {
            assertThrows(IllegalArgumentException.class, () -> cargo.setNome("   "));
        }

        @Test
        @DisplayName("deve aceitar nome válido")
        void shouldAcceptValidNome() {
            cargo.setNome("Gerente");
            assertEquals("Gerente", cargo.getNome());
        }
    }

    // ==================== ROLES ====================

    @Nested
    @DisplayName("Gestão de roles")
    class RoleManagement {

        @Test
        @DisplayName("deve adicionar role")
        void shouldAddRole() {
            Cargo newCargo = new Cargo(2L, "Teste", null);
            Role role = Role.create("MECANICO");

            newCargo.addRole(role);

            assertEquals(1, newCargo.getRoles().size());
        }

        @Test
        @DisplayName("deve remover role")
        void shouldRemoveRole() {
            cargo.removeRole(roleUser);

            assertEquals(1, cargo.getRoles().size());
        }

        @Test
        @DisplayName("deve rejeitar role nula ao adicionar")
        void shouldRejectNullRole() {
            assertThrows(NullPointerException.class, () -> cargo.addRole(null));
        }

        @Test
        @DisplayName("deve verificar se possui role")
        void shouldCheckHasRole() {
            assertTrue(cargo.hasRole("ADMIN"));
            assertTrue(cargo.hasRole("admin"));
            assertFalse(cargo.hasRole("MECANICO"));
        }

        @Test
        @DisplayName("roles devem ser imutáveis via getRoles")
        void shouldBeImmutableViaGetRoles() {
            var roles = cargo.getRoles();
            var role = Role.create("MECANICO");
            assertThrows(UnsupportedOperationException.class, () -> roles.add(role));
        }
    }

    // ==================== GETTERS / SETTERS ====================

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("deve setar e obter id")
        void shouldSetAndGetId() {
            cargo.setId(99L);
            assertEquals(99L, cargo.getId());
        }
    }

    // ==================== EQUALS / HASHCODE / TOSTRING ====================

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("cargos com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Cargo other = new Cargo();
            other.setId(1L);

            assertEquals(cargo, other);
            assertEquals(cargo.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("cargos com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Cargo other = new Cargo();
            other.setId(2L);

            assertNotEquals(cargo, other);
        }

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToSelf() {
            assertEquals(cargo, cargo);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, cargo);
        }

        @Test
        @DisplayName("não deve ser igual a tipo diferente")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", cargo);
        }

        @Test
        @DisplayName("toString deve conter id e nome")
        void shouldContainIdAndNomeInToString() {
            String result = cargo.toString();

            assertTrue(result.contains("1"));
            assertTrue(result.contains("Administrador"));
        }
    }
}

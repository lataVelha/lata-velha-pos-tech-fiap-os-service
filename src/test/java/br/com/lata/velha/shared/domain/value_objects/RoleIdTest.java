package br.com.lata.velha.shared.domain.value_objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleIdTest {

    @Test
    @DisplayName("create deve criar RoleId a partir de um UUID")
    void createShouldCreateRoleIdFromUUID() {
        var id = UUID.randomUUID();

        var roleId = assertDoesNotThrow(() -> RoleId.create(id));
        assertEquals(id, roleId.getValue());
    }

    @Test
    @DisplayName("random deve criar um RoleId randômico")
    void randomShouldCreateRandomRoleId() {
        var roleId = assertDoesNotThrow(RoleId::random);
        assertNotNull(roleId);
    }

    @Test
    @DisplayName("getValue deve retornar Id sem alterações")
    void getValueShouldReturnId() {
        var id = UUID.randomUUID();
        var roleId = RoleId.create(id);

        var value = roleId.getValue();

        assertEquals(id, value);
    }

    @Nested
    @DisplayName("equals")
    class EqualsTests {
        @Test
        @DisplayName("deve retornar true quando ids são iguais")
        void shouldReturnTrueWhenSameIds() {
            var id = UUID.randomUUID();
            var roleId1 = RoleId.create(id);
            var roleId2 = RoleId.create(id);

            assertEquals(roleId1, roleId2);
        }

        @Test
        @DisplayName("deve retornar false quando ids diferem")
        void shouldReturnFalseWhenDifferentIds() {
            var roleId1 = RoleId.create(UUID.randomUUID());
            var roleId2 = RoleId.create(UUID.randomUUID());

            assertNotEquals(roleId1, roleId2);
        }

        @Test
        @DisplayName("deve igualar a ele mesmo")
        void shouldEqualToSelf() {
            var id = UUID.randomUUID();
            var roleId = RoleId.create(id);

            assertEquals(roleId, roleId);
        }
    }

    @Test
    @DisplayName("toString deve retornar representação do id")
    void toStringShouldReturnIdStringRepresentation() {
        var id = UUID.randomUUID();
        var roleId = RoleId.create(id);

        assertEquals(id.toString(), roleId.toString());
    }
}

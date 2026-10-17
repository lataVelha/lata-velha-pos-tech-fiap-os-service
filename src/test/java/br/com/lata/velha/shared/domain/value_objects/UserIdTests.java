package br.com.lata.velha.shared.domain.value_objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTests {

    @Test
    @DisplayName("create deve criar UserId a partir de um UUID")
    void createShouldCreateUserIdFromUUID() {
        var id = UUID.randomUUID();

        var userId = assertDoesNotThrow(() -> UserId.create(id));
        assertEquals(id, userId.getValue());
    }

    @Test
    @DisplayName("random deve criar um UserId randômico")
    void randomShouldCreateRandomUserId() {
        var userId = assertDoesNotThrow(UserId::random);
        assertNotNull(userId);
    }

    @Test
    @DisplayName("getValue deve retornar Id sem alterações")
    void getValueShouldReturnId() {
        var id = UUID.randomUUID();
        var userId = UserId.create(id);

        var value = userId.getValue();

        assertEquals(id, value);
    }

    @Nested
    @DisplayName("equals")
    class EqualsTests {
        @Test
        @DisplayName("deve retornar true quando ids são iguais")
        void shouldReturnTrueWhenSameIds() {
            var id = UUID.randomUUID();
            var userId1 = UserId.create(id);
            var userId2 = UserId.create(id);

            assertEquals(userId1, userId2);
        }

        @Test
        @DisplayName("deve retornar false quando ids diferem")
        void shouldReturnFalseWhenDifferentIds() {
            var userId1 = UserId.create(UUID.randomUUID());
            var userId2 = UserId.create(UUID.randomUUID());

            assertNotEquals(userId1, userId2);
        }

        @Test
        @DisplayName("deve igualar a ele mesmo")
        void shouldEqualToSelf() {
            var id = UUID.randomUUID();
            var userId = UserId.create(id);

            assertEquals(userId, userId);
        }
    }

    @Test
    @DisplayName("toString deve retornar representação do id")
    void toStringShouldReturnIdStringRepresentation() {
        var id = UUID.randomUUID();
        var userId = UserId.create(id);

        assertEquals(id.toString(), userId.toString());
    }
}

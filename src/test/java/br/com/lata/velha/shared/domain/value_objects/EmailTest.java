package br.com.lata.velha.shared.domain.value_objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    private static final String VALID_EMAIL = "usuario@email.com";

    @Test
    @DisplayName("deve criar email válido")
    void shouldCreateValidEmail() {
        Email email = assertDoesNotThrow(() -> Email.fromString(VALID_EMAIL));

        assertNotNull(email);
        assertEquals(VALID_EMAIL, email.getValor());
    }

    @Test
    @DisplayName("deve normalizar email para lowercase")
    void shouldNormalizeEmailToLowercase() {
        Email email = Email.fromString("Usuario@Email.COM");

        assertEquals("usuario@email.com", email.getValor());
    }

    @Test
    @DisplayName("deve remover espaços do email")
    void shouldTrimEmail() {
        Email email = Email.fromString("  usuario@email.com  ");

        assertEquals("usuario@email.com", email.getValor());
    }

    @Nested
    @DisplayName("validação")
    class ValidationTests {
        @Test
        @DisplayName("deve lançar exceção quando email é nulo")
        void shouldThrowWhenEmailIsNull() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Email.fromString(null));

            assertEquals("Email não pode ser vazio", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando email é vazio")
        void shouldThrowWhenEmailIsEmpty() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Email.fromString(""));

            assertEquals("Email não pode ser vazio", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando email tem apenas espaços")
        void shouldThrowWhenEmailIsBlank() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Email.fromString("   "));

            assertEquals("Email não pode ser vazio", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando email não tem @")
        void shouldThrowWhenEmailHasNoAtSymbol() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Email.fromString("usuarioemail.com"));

            assertEquals("Formato de email inválido", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando email não tem domínio")
        void shouldThrowWhenEmailHasNoDomain() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Email.fromString("usuario@"));

            assertEquals("Formato de email inválido", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando email não tem parte local")
        void shouldThrowWhenEmailHasNoLocalPart() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Email.fromString("@email.com"));

            assertEquals("Formato de email inválido", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando email não tem extensão de domínio")
        void shouldThrowWhenEmailHasNoDomainExtension() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Email.fromString("usuario@email"));

            assertEquals("Formato de email inválido", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsHashCodeTests {
        @Test
        @DisplayName("emails iguais devem ser equals")
        void equalEmailsShouldBeEquals() {
            Email email1 = Email.fromString(VALID_EMAIL);
            Email email2 = Email.fromString(VALID_EMAIL);

            assertEquals(email1, email2);
            assertEquals(email1.hashCode(), email2.hashCode());
        }

        @Test
        @DisplayName("emails com case diferente devem ser equals")
        void emailsWithDifferentCaseShouldBeEquals() {
            Email email1 = Email.fromString("usuario@email.com");
            Email email2 = Email.fromString("USUARIO@EMAIL.COM");

            assertEquals(email1, email2);
        }

        @Test
        @DisplayName("emails diferentes não devem ser equals")
        void differentEmailsShouldNotBeEquals() {
            Email email1 = Email.fromString("usuario1@email.com");
            Email email2 = Email.fromString("usuario2@email.com");

            assertNotEquals(email1, email2);
        }

        @Test
        @DisplayName("deve igualar a ele mesmo")
        void shouldEqualItself() {
            Email email = Email.fromString(VALID_EMAIL);

            assertEquals(email, email);
        }
    }

    @Test
    @DisplayName("toString deve retornar o valor do email")
    void toStringShouldReturnEmailValue() {
        Email email = Email.fromString(VALID_EMAIL);

        assertEquals(VALID_EMAIL, email.toString());
    }
}

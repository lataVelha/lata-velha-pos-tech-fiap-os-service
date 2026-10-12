package br.com.lata.velha.authentication.domain.value_objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaTest {

    private static final String VALID_SENHA = "Senha123!";

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar senha válida e expor o valor")
        void shouldCreateValidSenhaAndExposeValor() {
            Senha senha = assertDoesNotThrow(() -> Senha.fromString(VALID_SENHA));

            assertNotNull(senha);
            assertEquals(VALID_SENHA, senha.getValor());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve lançar exceção quando senha é nula")
        void shouldThrowWhenNull() {
            var ex = assertThrows(IllegalArgumentException.class, () -> Senha.fromString(null));
            assertEquals("Senha não pode ser vazia", ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha é vazia")
        void shouldThrowWhenEmpty() {
            var ex = assertThrows(IllegalArgumentException.class, () -> Senha.fromString(""));
            assertEquals("Senha não pode ser vazia", ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha é apenas espaços")
        void shouldThrowWhenBlank() {
            var ex = assertThrows(IllegalArgumentException.class, () -> Senha.fromString("   "));
            assertEquals("Senha não pode ser vazia", ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha é menor que o mínimo")
        void shouldThrowWhenTooShort() {
            var ex = assertThrows(IllegalArgumentException.class, () -> Senha.fromString("Ab1!"));
            assertEquals(
                    String.format("Senha deve ter no mínimo %d caracteres", Senha.MIN_LENGTH),
                    ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha excede o máximo")
        void shouldThrowWhenTooLong() {
            String longa = "A1!" + "a".repeat(Senha.MAX_LENGTH);
            var ex = assertThrows(IllegalArgumentException.class, () -> Senha.fromString(longa));
            assertEquals(
                    String.format("Senha deve ter no máximo %d caracteres", Senha.MAX_LENGTH),
                    ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha não contém números")
        void shouldThrowWhenNoDigits() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString("SemNumero!"));
            assertEquals(
                    String.format("Senha deve conter no mínimo %d número(s)", Senha.MIN_NUMBERS),
                    ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha não contém caractere especial")
        void shouldThrowWhenNoSpecialChar() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString("SenhaValida123"));
            assertEquals(
                    String.format("Senha deve conter no mínimo %d caractere(s) especial(is)", Senha.MIN_SPECIAL_CHARACTERS),
                    ex.getMessage());
        }

        @Test
        @DisplayName("deve aceitar senha com exatamente o tamanho mínimo")
        void shouldAcceptSenhaAtMinLength() {
            // exactly 8 chars with 1 digit and 1 special char
            String atMin = "Ab1!efgh";
            assertEquals(Senha.MIN_LENGTH, atMin.length());
            assertDoesNotThrow(() -> Senha.fromString(atMin));
        }

        @Test
        @DisplayName("deve aceitar senha com exatamente o tamanho máximo")
        void shouldAcceptSenhaAtMaxLength() {
            // "A1!" prefix (3 chars) + (MAX_LENGTH - 3) lowercase letters = MAX_LENGTH total
            String atMax = "A1!" + "a".repeat(Senha.MAX_LENGTH - 3);
            assertEquals(Senha.MAX_LENGTH, atMax.length());
            assertDoesNotThrow(() -> Senha.fromString(atMax));
        }
    }

    // ==================== EQUALS / HASHCODE / TOSTRING ====================

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("senhas com mesmo valor devem ser iguais")
        void shouldBeEqualWithSameValue() {
            Senha s1 = Senha.fromString(VALID_SENHA);
            Senha s2 = Senha.fromString(VALID_SENHA);

            assertEquals(s1, s2);
            assertEquals(s1.hashCode(), s2.hashCode());
        }

        @Test
        @DisplayName("senhas com valores diferentes não devem ser iguais")
        void shouldNotBeEqualWithDifferentValues() {
            Senha s1 = Senha.fromString("Senha123!");
            Senha s2 = Senha.fromString("Outra456@");

            assertNotEquals(s1, s2);
        }

        @Test
        @DisplayName("deve ser igual a si mesma")
        void shouldEqualItself() {
            Senha senha = Senha.fromString(VALID_SENHA);
            assertEquals(senha, senha);
        }

        @Test
        @DisplayName("não deve ser igual outro objeto")
        void shouldNotEqualObject() {
            Senha senha = Senha.fromString(VALID_SENHA);
            assertNotEquals(new Object(), senha);
        }

        @Test
        @DisplayName("toString não deve expor o valor da senha")
        void toStringShouldNotExposeValue() {
            Senha senha = Senha.fromString(VALID_SENHA);
            assertFalse(senha.toString().contains(VALID_SENHA));
            assertTrue(senha.toString().contains("***"));
        }
    }
}

package br.com.lata.velha.ordem_servico.domain.value_objects;

import br.com.lata.velha.authentication.domain.value_objects.Senha;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaTest {

    private static final String VALID_SENHA = "Senha123!";

    @Test
    @DisplayName("deve criar senha válida")
    void shouldCreateValidSenha() {
        Senha senha = assertDoesNotThrow(() -> Senha.fromString(VALID_SENHA));

        assertNotNull(senha);
        assertEquals(VALID_SENHA, senha.getValor());
    }

    @Nested
    @DisplayName("validação")
    class ValidationTests {
        @Test
        @DisplayName("deve lançar exceção quando senha é nula")
        void shouldThrowWhenSenhaIsNull() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString(null));

            assertEquals("Senha não pode ser vazia", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha é vazia")
        void shouldThrowWhenSenhaIsEmpty() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString(""));

            assertEquals("Senha não pode ser vazia", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha tem apenas espaços")
        void shouldThrowWhenSenhaIsBlank() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString("   "));

            assertEquals("Senha não pode ser vazia", exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha é menor que o tamanho mínimo")
        void shouldThrowWhenSenhaIsShorterThanMinLength() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString("Ab1!"));

            assertEquals(
                    String.format("Senha deve ter no mínimo %d caracteres", Senha.MIN_LENGTH),
                    exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha é maior que o tamanho máximo")
        void shouldThrowWhenSenhaIsLongerThanMaxLength() {
            String largeSenha = "A1!" + "a".repeat(Senha.MAX_LENGTH);

            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString(largeSenha));

            assertEquals(
                    String.format("Senha deve ter no máximo %d caracteres", Senha.MAX_LENGTH),
                    exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha não tem números")
        void shouldThrowWhenSenhaHasNoNumbers() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString("SenhaWithoutNum!"));

            assertEquals(
                    String.format("Senha deve conter no mínimo %d número(s)", Senha.MIN_NUMBERS),
                    exception.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção quando senha não tem caracteres especiais")
        void shouldThrowWhenSenhaHasNoSpecialCharacters() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Senha.fromString("SenhaWithout123"));

            assertEquals(
                    String.format("Senha deve conter no mínimo %d caractere(s) especial(is)", Senha.MIN_SPECIAL_CHARACTERS),
                    exception.getMessage());
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsHashCodeTests {
        @Test
        @DisplayName("senhas iguais devem ser equals")
        void equalSenhasShouldBeEquals() {
            Senha senha1 = Senha.fromString(VALID_SENHA);
            Senha senha2 = Senha.fromString(VALID_SENHA);

            assertEquals(senha1, senha2);
            assertEquals(senha1.hashCode(), senha2.hashCode());
        }

        @Test
        @DisplayName("senhas diferentes não devem ser equals")
        void differentSenhasShouldNotBeEquals() {
            Senha senha1 = Senha.fromString("Senha123!");
            Senha senha2 = Senha.fromString("OutraSenha456@");

            assertNotEquals(senha1, senha2);
        }

        @Test
        @DisplayName("deve igualar a ela mesma")
        void shouldEqualItself() {
            Senha senha = Senha.fromString(VALID_SENHA);

            assertEquals(senha, senha);
        }
    }

    @Test
    @DisplayName("toString não deve expor o valor da senha")
    void toStringShouldNotExposeSenhaValue() {
        Senha senha = Senha.fromString(VALID_SENHA);

        assertFalse(senha.toString().contains(VALID_SENHA));
        assertTrue(senha.toString().contains("***"));
    }
}
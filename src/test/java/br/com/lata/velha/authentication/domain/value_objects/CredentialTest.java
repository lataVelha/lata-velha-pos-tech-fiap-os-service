package br.com.lata.velha.authentication.domain.value_objects;

import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CredentialTest {
    private static final PasswordHasher PLAIN_HASHER = new PasswordHasher() {
        @Override public String hashSenha(Senha senha) { return senha.getValor(); }
        @Override public boolean match(Credential cred, String raw) { return raw.equals(cred.getHash()); }
    };

    private static final String VALID_SENHA = "Senha123!";

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("fromSenha deve armazenar o hash gerado pelo hasher")
        void fromSenhaShouldStoreHashFromHasher() {
            Senha senha = Senha.fromString(VALID_SENHA);

            Credential credential = Credential.fromSenha(senha, PLAIN_HASHER);

            assertEquals(VALID_SENHA, credential.getHash());
        }

        @Test
        @DisplayName("fromHash deve armazenar o hash fornecido diretamente")
        void fromHashShouldStoreProvidedHash() {
            Credential credential = Credential.fromHash("algumHash", PLAIN_HASHER);

            assertEquals("algumHash", credential.getHash());
        }
    }

    // ==================== MATCH ====================

    @Nested
    @DisplayName("match")
    class Match {

        @Test
        @DisplayName("deve retornar true para senha correta")
        void shouldReturnTrueForCorrectPassword() {
            Credential credential = Credential.fromHash(VALID_SENHA, PLAIN_HASHER);

            assertTrue(credential.match(VALID_SENHA));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SenhaErrada1@", "   "})
        @DisplayName("deve retornar false para senha incorreta")
        void shouldReturnFalseForWrongPassword(String senha) {
            Credential credential = Credential.fromHash(VALID_SENHA, PLAIN_HASHER);

            assertFalse(credential.match(senha));
        }

        @Test
        @DisplayName("deve retornar false para senha nula")
        void shouldReturnFalseForNullPassword() {
            Credential credential = Credential.fromHash(VALID_SENHA, PLAIN_HASHER);

            assertFalse(credential.match(null));
        }
    }

    // ==================== EQUALS / HASHCODE ====================

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("credentials com mesmo hash devem ser iguais")
        void shouldBeEqualWithSameHash() {
            Credential c1 = Credential.fromHash("hash", PLAIN_HASHER);
            Credential c2 = Credential.fromHash("hash", PLAIN_HASHER);

            assertEquals(c1, c2);
            assertEquals(c1.hashCode(), c2.hashCode());
        }

        @Test
        @DisplayName("credentials com hashes diferentes não devem ser iguais")
        void shouldNotBeEqualWithDifferentHash() {
            Credential c1 = Credential.fromHash("hash1", PLAIN_HASHER);
            Credential c2 = Credential.fromHash("hash2", PLAIN_HASHER);

            assertNotEquals(c1, c2);
        }

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldEqualItself() {
            Credential credential = Credential.fromHash("hash", PLAIN_HASHER);
            assertEquals(credential, credential);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotEqualNull() {
            Credential credential = Credential.fromHash("hash", PLAIN_HASHER);
            assertNotEquals(null, credential);
        }

        @Test
        @DisplayName("não deve ser igual outro objeto")
        void shouldNotEqualObject() {
            Credential credential = Credential.fromHash("hash", PLAIN_HASHER);
            assertNotEquals(new Object(), credential);
        }
    }
}

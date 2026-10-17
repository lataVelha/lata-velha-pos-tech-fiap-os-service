package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest {

    private Funcionario funcionario;
    private Cargo cargo;
    private UserId userId;

    @BeforeEach
    void setUp() {
        cargo = new Cargo(1L, "ADMIN", null);
        userId = UserId.random();
        funcionario = new Funcionario(1L, "João", cargo, userId);
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {
        @Test
        @DisplayName("deve atualizar dados")
        void shouldUpdateData() {
            var novoNome = "Novo nome";
            var novoCargo = new Cargo(2L, "NOVO", null);

            funcionario.update(novoNome, novoCargo);

            assertEquals(novoNome, funcionario.getNome());
            assertEquals(novoCargo, funcionario.getCargo());
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando nome for null")
        void shouldThrowIllegalArgumentExceptionWhenNomeIsNull() {
            var novoCargo = new Cargo(2L, "NOVO", null);

            assertThrows(IllegalArgumentException.class, () -> funcionario.update(null, novoCargo));
        }

        @ParameterizedTest
        @DisplayName("deve lançar IllegalArgumentException quando nome for invalido")
        @ValueSource(strings = {"", "   "})
        void shouldThrowIllegalArgumentExceptionWhenNomeIsInvalid(String novoNome) {
            var novoCargo = new Cargo(2L, "NOVO", null);

            assertThrows(IllegalArgumentException.class, () -> funcionario.update(novoNome, novoCargo));
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando cargo for null")
        void shouldThrowIllegalArgumentExceptionWhenCargoIsNull() {
            var novoNome = "Novo nome";
            assertThrows(IllegalArgumentException.class, () -> funcionario.update(novoNome, null));
        }
    }

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("deve retornar id correto")
        void shouldGetId() {
            assertEquals(1L, funcionario.getId());
        }

        @Test
        @DisplayName("deve retornar nome correto")
        void shouldGetNome() {
            assertEquals("João", funcionario.getNome());
        }

        @Test
        @DisplayName("deve retornar userId correto")
        void shouldGetUserId() {
            assertEquals(userId, funcionario.getUserId());
        }
    }

    @Nested
    @DisplayName("Factory method")
    class FactoryMethod {

        @Test
        @DisplayName("create deve criar funcionário sem id")
        void shouldCreateFuncionarioWithNullId() {
            Funcionario created = Funcionario.create("Maria", cargo, userId);
            assertNull(created.getId());
            assertEquals("Maria", created.getNome());
            assertEquals(cargo, created.getCargo());
            assertEquals(userId, created.getUserId());
        }
    }

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("funcionários com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Funcionario other = new Funcionario(1L, "Outro Nome", new Cargo(2L, "USER", null), UserId.random());
            assertEquals(funcionario, other);
            assertEquals(funcionario.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("funcionários com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Funcionario other = new Funcionario(2L, "João", cargo, userId);
            assertNotEquals(funcionario, other);
        }

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToSelf() {
            assertEquals(funcionario, funcionario);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, funcionario);
        }

        @Test
        @DisplayName("não deve ser igual a tipo diferente")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", funcionario);
        }
    }
}

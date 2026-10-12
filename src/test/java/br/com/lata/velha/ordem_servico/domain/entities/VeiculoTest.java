package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoTest {

    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo(1L, 1L, Placa.of("ABC1D23"), "Fiat", "Uno", 2020, "Prata");
    }

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar veículo com todos os campos")
        void shouldCreateWithAllFields() {
            assertEquals(1L, veiculo.getId());
            assertEquals(1L, veiculo.getProprietarioId());
            assertEquals("ABC1D23", veiculo.getPlaca().getValor());
            assertEquals("Fiat", veiculo.getMarca());
            assertEquals("Uno", veiculo.getModelo());
            assertEquals(2020, veiculo.getAno());
            assertEquals("Prata", veiculo.getCor());
            assertTrue(veiculo.isAtivo());
        }

        @Test
        @DisplayName("deve criar veículo vazio")
        void shouldCreateEmpty() {
            Veiculo empty = new Veiculo();

            assertNull(empty.getId());
            assertNull(empty.getPlaca());
            assertNull(empty.getMarca());
            assertTrue(empty.isAtivo());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve rejeitar marca nula")
        void shouldRejectNullMarca() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setMarca(null));
        }

        @Test
        @DisplayName("deve rejeitar marca vazia")
        void shouldRejectEmptyMarca() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setMarca(""));
        }

        @Test
        @DisplayName("deve rejeitar marca em branco")
        void shouldRejectBlankMarca() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setMarca("   "));
        }

        @Test
        @DisplayName("deve aceitar marca válida")
        void shouldAcceptValidMarca() {
            veiculo.setMarca("Toyota");
            assertEquals("Toyota", veiculo.getMarca());
        }

        @Test
        @DisplayName("deve rejeitar modelo nulo")
        void shouldRejectNullModelo() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setModelo(null));
        }

        @Test
        @DisplayName("deve rejeitar modelo vazio")
        void shouldRejectEmptyModelo() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setModelo(""));
        }

        @Test
        @DisplayName("deve rejeitar modelo em branco")
        void shouldRejectBlankModelo() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setModelo("   "));
        }

        @Test
        @DisplayName("deve aceitar modelo válido")
        void shouldAcceptValidModelo() {
            veiculo.setModelo("Corolla");
            assertEquals("Corolla", veiculo.getModelo());
        }

        @Test
        @DisplayName("deve rejeitar cor nula")
        void shouldRejectNullCor() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setCor(null));
        }

        @Test
        @DisplayName("deve rejeitar cor vazia")
        void shouldRejectEmptyCor() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setCor(""));
        }

        @Test
        @DisplayName("deve rejeitar cor em branco")
        void shouldRejectBlankCor() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setCor("   "));
        }

        @Test
        @DisplayName("deve aceitar cor válida")
        void shouldAcceptValidCor() {
            veiculo.setCor("Azul");
            assertEquals("Azul", veiculo.getCor());
        }

        @Test
        @DisplayName("deve rejeitar ano nulo")
        void shouldRejectNullAno() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(null));
        }

        @Test
        @DisplayName("deve rejeitar ano anterior a 1886")
        void shouldRejectAnoBefore1886() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(1885));
        }

        @Test
        @DisplayName("deve rejeitar ano futuro além do próximo")
        void shouldRejectFutureAno() {
            int futureYear = java.time.Year.now().getValue() + 2;
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(futureYear));
        }

        @Test
        @DisplayName("deve aceitar ano atual + 1")
        void shouldAcceptNextYearAno() {
            int nextYear = java.time.Year.now().getValue() + 1;
            veiculo.setAno(nextYear);
            assertEquals(nextYear, veiculo.getAno());
        }

        @Test
        @DisplayName("deve aceitar ano 1886 (primeiro carro)")
        void shouldAcceptMinimumAno() {
            veiculo.setAno(1886);
            assertEquals(1886, veiculo.getAno());
        }

        @Test
        @DisplayName("deve aceitar ano atual")
        void shouldAcceptCurrentYearAno() {
            int currentYear = java.time.Year.now().getValue();
            veiculo.setAno(currentYear);
            assertEquals(currentYear, veiculo.getAno());
        }
    }

    // ==================== SOFT DELETE ====================

    @Nested
    @DisplayName("Soft Delete")
    class SoftDelete {

        @Test
        @DisplayName("deve desativar veículo")
        void shouldDeactivate() {
            veiculo.deactivate();
            assertFalse(veiculo.isAtivo());
        }

        @Test
        @DisplayName("deve reativar veículo")
        void shouldActivate() {
            veiculo.deactivate();
            veiculo.activate();
            assertTrue(veiculo.isAtivo());
        }

        @Test
        @DisplayName("deve setar ativo diretamente")
        void shouldSetAtivo() {
            veiculo.setAtivo(false);
            assertFalse(veiculo.isAtivo());
            veiculo.setAtivo(true);
            assertTrue(veiculo.isAtivo());
        }
    }

    // ==================== GETTERS / SETTERS ====================

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("deve setar e obter id")
        void shouldSetAndGetId() {
            veiculo.setId(99L);
            assertEquals(99L, veiculo.getId());
        }

        @Test
        @DisplayName("deve setar e obter proprietarioId")
        void shouldSetAndGetProprietarioId() {
            veiculo.setProprietarioId(5L);
            assertEquals(5L, veiculo.getProprietarioId());
        }

        @Test
        @DisplayName("deve setar e obter placa")
        void shouldSetAndGetPlaca() {
            Placa newPlaca = Placa.of("XYZ9876");
            veiculo.setPlaca(newPlaca);
            assertEquals(newPlaca, veiculo.getPlaca());
        }
    }

    // ==================== EQUALS / HASHCODE / TOSTRING ====================

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("veículos com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Veiculo other = new Veiculo();
            other.setId(1L);

            assertEquals(veiculo, other);
            assertEquals(veiculo.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("veículos com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Veiculo other = new Veiculo();
            other.setId(2L);

            assertNotEquals(veiculo, other);
        }

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToSelf() {
            assertEquals(veiculo, veiculo);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, veiculo);
        }

        @Test
        @DisplayName("não deve ser igual a tipo diferente")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", veiculo);
        }

        @Test
        @DisplayName("toString deve conter placa e marca")
        void shouldContainPlacaAndMarcaInToString() {
            String result = veiculo.toString();

            assertTrue(result.contains("Fiat"));
            assertTrue(result.contains("placa"));
        }
    }
}
package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.Endereco;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProprietarioTest {

    private Proprietario proprietario;

    @BeforeEach
    void setUp() {
        proprietario = new Proprietario(
                1L,
                "João da Silva",
                "joao@email.com",
                Documento.of("52998224725"),
                NumeroCelular.of("11999990001"),
                new Endereco("Rua das Flores", "01234567", "100")
        );
    }

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar proprietário com todos os campos")
        void shouldCreateWithAllFields() {
            assertEquals(1L, proprietario.getId());
            assertEquals("João da Silva", proprietario.getNome());
            assertEquals("joao@email.com", proprietario.getEmail());
            assertEquals("52998224725", proprietario.getDocumento().getValor());
            assertEquals("11999990001", proprietario.getNumeroCelular().getValor());
            assertNotNull(proprietario.getEndereco());
            assertTrue(proprietario.getVeiculos().isEmpty());
            assertTrue(proprietario.isAtivo());
        }

        @Test
        @DisplayName("deve criar proprietário vazio com lista de veículos inicializada")
        void shouldCreateEmptyWithInitializedList() {
            Proprietario empty = new Proprietario();

            assertNull(empty.getId());
            assertNull(empty.getNome());
            assertNotNull(empty.getVeiculos());
            assertTrue(empty.getVeiculos().isEmpty());
            assertTrue(empty.isAtivo());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve rejeitar nome nulo")
        void shouldRejectNullNome() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setNome(null));
        }

        @Test
        @DisplayName("deve rejeitar nome vazio")
        void shouldRejectEmptyNome() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setNome(""));
        }

        @Test
        @DisplayName("deve rejeitar nome em branco")
        void shouldRejectBlankNome() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setNome("   "));
        }

        @Test
        @DisplayName("deve aceitar nome válido")
        void shouldAcceptValidNome() {
            proprietario.setNome("Maria");
            assertEquals("Maria", proprietario.getNome());
        }

        @Test
        @DisplayName("deve rejeitar email inválido")
        void shouldRejectInvalidEmail() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setEmail("invalid"));
        }

        @Test
        @DisplayName("deve rejeitar email nulo")
        void shouldRejectNullEmail() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setEmail(null));
        }

        @Test
        @DisplayName("deve aceitar email válido")
        void shouldAcceptValidEmail() {
            proprietario.setEmail("new@email.com");
            assertEquals("new@email.com", proprietario.getEmail());
        }
    }

    // ==================== SOFT DELETE ====================

    @Nested
    @DisplayName("Soft Delete")
    class SoftDelete {

        @Test
        @DisplayName("deve desativar proprietário")
        void shouldDeactivate() {
            proprietario.deactivate();
            assertFalse(proprietario.isAtivo());
        }

        @Test
        @DisplayName("deve reativar proprietário")
        void shouldActivate() {
            proprietario.deactivate();
            proprietario.activate();
            assertTrue(proprietario.isAtivo());
        }

        @Test
        @DisplayName("deve setar ativo diretamente")
        void shouldSetAtivo() {
            proprietario.setAtivo(false);
            assertFalse(proprietario.isAtivo());
            proprietario.setAtivo(true);
            assertTrue(proprietario.isAtivo());
        }
    }

    // ==================== VEÍCULOS ====================

    @Nested
    @DisplayName("Gestão de veículos")
    class VeiculoManagement {

        @Test
        @DisplayName("deve adicionar veículo")
        void shouldAddVeiculo() {
            Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");

            proprietario.addVeiculo(veiculo);

            assertEquals(1, proprietario.getVeiculos().size());
        }

        @Test
        @DisplayName("deve remover veículo")
        void shouldRemoveVeiculo() {
            Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
            proprietario.addVeiculo(veiculo);

            proprietario.removeVeiculo(veiculo);

            assertTrue(proprietario.getVeiculos().isEmpty());
        }

        @Test
        @DisplayName("deve rejeitar veículo nulo")
        void shouldRejectNullVeiculo() {
            assertThrows(NullPointerException.class, () -> proprietario.addVeiculo(null));
        }

        @Test
        @DisplayName("lista de veículos deve ser imutável")
        void shouldBeImmutableList() {
            var veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
            var veiculos = proprietario.getVeiculos();
            assertThrows(UnsupportedOperationException.class,
                    () -> veiculos.add(veiculo));
        }

        @Test
        @DisplayName("deve setar lista de veículos")
        void shouldSetVeiculos() {
            List<Veiculo> list = new ArrayList<>();
            list.add(new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata"));

            proprietario.setVeiculos(list);

            assertEquals(1, proprietario.getVeiculos().size());
        }

        @Test
        @DisplayName("deve setar lista de veículos nula como lista vazia")
        void shouldSetNullVeiculosAsEmptyList() {
            proprietario.setVeiculos(null);

            assertNotNull(proprietario.getVeiculos());
            assertTrue(proprietario.getVeiculos().isEmpty());
        }
    }

    // ==================== GETTERS / SETTERS ====================

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("deve setar e obter id")
        void shouldSetAndGetId() {
            proprietario.setId(99L);
            assertEquals(99L, proprietario.getId());
        }

        @Test
        @DisplayName("deve setar e obter documento")
        void shouldSetAndGetDocumento() {
            Documento doc = Documento.of("57126491018");
            proprietario.setDocumento(doc);
            assertEquals(doc, proprietario.getDocumento());
        }

        @Test
        @DisplayName("deve setar e obter numeroCelular")
        void shouldSetAndGetNumeroCelular() {
            NumeroCelular cel = NumeroCelular.of("21988887777");
            proprietario.setNumeroCelular(cel);
            assertEquals(cel, proprietario.getNumeroCelular());
        }

        @Test
        @DisplayName("deve setar e obter endereco")
        void shouldSetAndGetEndereco() {
            Endereco end = new Endereco("Rua Nova", "99999999", "50");
            proprietario.setEndereco(end);
            assertEquals(end, proprietario.getEndereco());
        }
    }

    // ==================== EQUALS / HASHCODE / TOSTRING ====================

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("proprietários com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Proprietario other = new Proprietario();
            other.setId(1L);

            assertEquals(proprietario, other);
            assertEquals(proprietario.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("proprietários com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Proprietario other = new Proprietario();
            other.setId(2L);

            assertNotEquals(proprietario, other);
        }

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToSelf() {
            assertEquals(proprietario, proprietario);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, proprietario);
        }

        @Test
        @DisplayName("não deve ser igual a tipo diferente")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", proprietario);
        }

        @Test
        @DisplayName("toString deve conter id e nome")
        void shouldContainIdAndNomeInToString() {
            String result = proprietario.toString();

            assertTrue(result.contains("1"));
            assertTrue(result.contains("João da Silva"));
        }
    }
}
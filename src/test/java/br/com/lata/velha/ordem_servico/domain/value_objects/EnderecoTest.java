package br.com.lata.velha.ordem_servico.domain.value_objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoTest {

    @Test
    @DisplayName("deve criar endereço válido")
    void shouldCreateValidEndereco() {
        Endereco end = new Endereco("Rua das Flores", "01234567", "100");

        assertEquals("Rua das Flores", end.getRua());
        assertEquals("01234567", end.getCep());
        assertEquals("100", end.getNumeroCasa());
    }

    @Test
    @DisplayName("deve limpar formatação do CEP")
    void shouldCleanCepFormatting() {
        Endereco end = new Endereco("Rua A", "01234-567", "10");

        assertEquals("01234567", end.getCep());
    }

    @Test
    @DisplayName("deve rejeitar rua nula")
    void shouldRejectNullRua() {
        assertThrows(IllegalArgumentException.class, () -> new Endereco(null, "01234567", "100"));
    }

    @Test
    @DisplayName("deve rejeitar rua vazia")
    void shouldRejectEmptyRua() {
        assertThrows(IllegalArgumentException.class, () -> new Endereco("", "01234567", "100"));
    }

    @Test
    @DisplayName("deve rejeitar CEP inválido")
    void shouldRejectInvalidCep() {
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", "123", "100"));
    }

    @Test
    @DisplayName("deve rejeitar CEP nulo")
    void shouldRejectNullCep() {
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", null, "100"));
    }

    @Test
    @DisplayName("deve rejeitar número nulo")
    void shouldRejectNullNumeroCasa() {
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", "01234567", null));
    }

    @Test
    @DisplayName("deve rejeitar número vazio")
    void shouldRejectEmptyNumeroCasa() {
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", "01234567", ""));
    }

    @Test
    @DisplayName("endereços iguais devem ser equals")
    void shouldBeEqualWithSameValues() {
        Endereco end1 = new Endereco("Rua A", "01234567", "100");
        Endereco end2 = new Endereco("Rua A", "01234-567", "100");

        assertEquals(end1, end2);
        assertEquals(end1.hashCode(), end2.hashCode());
    }

    @Test
    @DisplayName("endereços diferentes não devem ser equals")
    void shouldNotBeEqualWithDifferentValues() {
        Endereco end1 = new Endereco("Rua A", "01234567", "100");
        Endereco end2 = new Endereco("Rua B", "01234567", "200");

        assertNotEquals(end1, end2);
    }

    @Test
    @DisplayName("toString deve conter rua, número e CEP")
    void shouldContainDataOnToString() {
        Endereco end = new Endereco("Rua das Flores", "01234567", "100");

        assertTrue(end.toString().contains("Rua das Flores"));
        assertTrue(end.toString().contains("100"));
        assertTrue(end.toString().contains("01234567"));
    }
}
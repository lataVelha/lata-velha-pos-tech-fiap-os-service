package br.com.lata.velha.ordem_servico.domain.value_objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumeroCelularTest {

    @Test
    @DisplayName("deve criar celular com 11 dígitos")
    void shouldCreateWith11Digits() {
        NumeroCelular cel = NumeroCelular.of("11999990001");

        assertEquals("11999990001", cel.getValor());
    }

    @Test
    @DisplayName("deve criar celular com 10 dígitos (fixo)")
    void shouldCreateWith10Digits() {
        NumeroCelular cel = NumeroCelular.of("1133334444");

        assertEquals("1133334444", cel.getValor());
    }

    @Test
    @DisplayName("deve limpar formatação")
    void shouldCleanFormatting() {
        NumeroCelular cel = NumeroCelular.of("(11) 99999-0001");

        assertEquals("11999990001", cel.getValor());
    }

    @Test
    @DisplayName("deve formatar celular 11 dígitos")
    void shouldFormat11Digits() {
        NumeroCelular cel = NumeroCelular.of("11999990001");

        assertEquals("(11) 99999-0001", cel.getFormatted());
    }

    @Test
    @DisplayName("deve formatar celular 10 dígitos")
    void shouldFormat10Digits() {
        NumeroCelular cel = NumeroCelular.of("1133334444");

        assertEquals("(11) 3333-4444", cel.getFormatted());
    }

    @Test
    @DisplayName("deve rejeitar nulo")
    void shouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> NumeroCelular.of(null));
    }

    @Test
    @DisplayName("deve rejeitar vazio")
    void shouldRejectEmpty() {
        assertThrows(IllegalArgumentException.class, () -> NumeroCelular.of(""));
    }

    @Test
    @DisplayName("deve rejeitar menos de 10 dígitos")
    void shouldRejectTooShort() {
        assertThrows(IllegalArgumentException.class, () -> NumeroCelular.of("123456789"));
    }

    @Test
    @DisplayName("deve rejeitar mais de 11 dígitos")
    void shouldRejectTooLong() {
        assertThrows(IllegalArgumentException.class, () -> NumeroCelular.of("123456789012"));
    }

    @Test
    @DisplayName("celulares iguais devem ser equals")
    void shouldBeEqualWithSameValor() {
        NumeroCelular cel1 = NumeroCelular.of("11999990001");
        NumeroCelular cel2 = NumeroCelular.of("(11) 99999-0001");

        assertEquals(cel1, cel2);
        assertEquals(cel1.hashCode(), cel2.hashCode());
    }

    @Test
    @DisplayName("toString deve retornar formatado")
    void shouldReturnFormattedOnToString() {
        NumeroCelular cel = NumeroCelular.of("11999990001");

        assertEquals("(11) 99999-0001", cel.toString());
    }
}
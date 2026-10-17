package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoNotFoundExceptionTest {

    @Test
    @DisplayName("fromId deve criar instância com id na mensagem")
    void fromIdShouldCreateExceptionWithIdInMessage() {
        VeiculoNotFoundException ex = VeiculoNotFoundException.fromId(3L);

        assertNotNull(ex);
        assertInstanceOf(NotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("3"));
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    @DisplayName("fromPlaca deve criar instância com placa na mensagem")
    void fromPlacaShouldCreateExceptionWithPlacaInMessage() {
        Placa placa = Placa.of("ABC1234");

        VeiculoNotFoundException ex = VeiculoNotFoundException.fromPlaca(placa);

        assertNotNull(ex);
        assertInstanceOf(NotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("placa"));
        assertTrue(ex.getMessage().contains("ABC-1234"));
    }
}

package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.shared.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioNotFoundExceptionTest {

    @Test
    @DisplayName("fromId deve criar instância com id na mensagem")
    void fromIdShouldCreateExceptionWithIdInMessage() {
        FuncionarioNotFoundException ex = FuncionarioNotFoundException.fromId(7L);

        assertNotNull(ex);
        assertInstanceOf(NotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("7"));
        assertTrue(ex.getMessage().contains("id"));
    }
}

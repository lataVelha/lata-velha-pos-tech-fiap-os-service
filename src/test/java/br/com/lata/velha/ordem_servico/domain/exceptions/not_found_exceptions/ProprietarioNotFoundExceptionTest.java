package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.shared.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProprietarioNotFoundExceptionTest {

    @Test
    @DisplayName("fromId deve criar instância com id na mensagem")
    void fromIdShouldCreateExceptionWithIdInMessage() {
        ProprietarioNotFoundException ex = ProprietarioNotFoundException.fromId(4L);

        assertNotNull(ex);
        assertInstanceOf(NotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("4"));
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    @DisplayName("fromDocumento deve criar instância com documento na mensagem")
    void fromDocumentoShouldCreateExceptionWithDocumentoInMessage() {
        ProprietarioNotFoundException ex = ProprietarioNotFoundException.fromDocumento("123.456.789-00");

        assertNotNull(ex);
        assertInstanceOf(NotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("documento"));
        assertTrue(ex.getMessage().contains("123.456.789-00"));
    }
}

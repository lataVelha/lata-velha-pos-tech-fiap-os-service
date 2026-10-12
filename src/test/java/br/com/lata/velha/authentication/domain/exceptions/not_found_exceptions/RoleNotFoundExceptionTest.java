package br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.shared.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleNotFoundExceptionTest {

    @Test
    @DisplayName("fromNome deve criar instância com nome na mensagem")
    void fromNomeShouldCreateExceptionWithNomeInMessage() {
        RoleNotFoundException ex = RoleNotFoundException.fromNome("ADMIN");

        assertNotNull(ex);
        assertInstanceOf(NotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("nome"));
        assertTrue(ex.getMessage().contains("ADMIN"));
    }
}

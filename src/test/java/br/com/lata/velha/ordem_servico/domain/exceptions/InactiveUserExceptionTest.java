package br.com.lata.velha.ordem_servico.domain.exceptions;

import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.shared.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InactiveUserExceptionTest {

    @Test
    @DisplayName("mensagem deve referenciar nome da entidade")
    void messageShouldReferenceClass() {
        InactiveUserException ex = InactiveUserException.fromEntityName("Entity");

        assertTrue(ex.getMessage().contains("Entity"));
    }

    @Test
    @DisplayName("deve ser subclasse de DomainException")
    void shouldBeDomainException() {
        InactiveUserException ex = InactiveUserException.fromEntityName("Entity");

        assertInstanceOf(DomainException.class, ex);
    }
}

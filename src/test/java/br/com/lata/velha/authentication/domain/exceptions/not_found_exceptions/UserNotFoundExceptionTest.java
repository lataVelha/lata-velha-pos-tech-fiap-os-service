package br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.shared.domain.exceptions.NotFoundException;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    @Test
    @DisplayName("fromId deve criar instância com id na mensagem")
    void fromIdShouldCreateExceptionWithIdInMessage() {
        UserId userId = UserId.random();

        UserNotFoundException ex = UserNotFoundException.fromId(userId);

        assertNotNull(ex);
        assertInstanceOf(NotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("id"));
        assertTrue(ex.getMessage().contains(userId.toString()));
    }

    @Test
    @DisplayName("fromUsername deve criar instância com username na mensagem")
    void fromUsernameShouldCreateExceptionWithUsernameInMessage() {
        UserNotFoundException ex = UserNotFoundException.fromUsername("joao");

        assertNotNull(ex);
        assertInstanceOf(NotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("username"));
        assertTrue(ex.getMessage().contains("joao"));
    }
}

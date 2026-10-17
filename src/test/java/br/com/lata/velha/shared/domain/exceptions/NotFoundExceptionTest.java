package br.com.lata.velha.shared.domain.exceptions;

import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.FuncionarioNotFoundException;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.CargoNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    @DisplayName("deve conter 'não encontrada' na mensagem")
    void shouldContainNotFoundInMessage() {
        NotFoundException ex = FuncionarioNotFoundException.fromId(1L);
        assertTrue(ex.getMessage().contains("não encontrada"));
    }

    @Test
    @DisplayName("deve incluir o paramName na mensagem quando fornecido")
    void shouldIncludeParamNameInMessage() {
        NotFoundException ex = FuncionarioNotFoundException.fromId(42L);
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    @DisplayName("deve incluir o paramValue na mensagem quando fornecido")
    void shouldIncludeParamValueInMessage() {
        NotFoundException ex = FuncionarioNotFoundException.fromId(99L);
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("deve ser subclasse de RuntimeException")
    void shouldBeRuntimeException() {
        NotFoundException ex = CargoNotFoundException.fromId(1L);
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("mensagem não deve ser nula nem vazia")
    void messageShouldNotBeNullOrEmpty() {
        NotFoundException ex = FuncionarioNotFoundException.fromId(1L);
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isBlank());
    }
}

package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.FuncionarioNotFoundException;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarFuncionarioUseCaseTest {

    @Mock
    private AtualizarFuncionarioGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve atualizar funcionário com sucesso")
    void shouldUpdateFuncionarioSuccessfully() {
        var request = new AtualizarFuncionarioRequest("Novo Nome", 2L);
        var cargoNovo = new Cargo(2L, "ATENDENTE", null);
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Nome Antigo", new Cargo(1L, "MECANICO", null), userId);

        when(gateway.getFuncionarioById(1L)).thenReturn(funcionario);
        when(gateway.isUsuarioAtivo(userId)).thenReturn(true);
        when(gateway.getCargoPorId(2L)).thenReturn(cargoNovo);
        when(gateway.salvarFuncionario(funcionario)).thenReturn(funcionario);

        var useCase = new AtualizarFuncionarioUseCase(gateway, logger);
        var result = useCase.execute(request.toUpdateUseCaseInput(1L));

        assertEquals("Novo Nome", funcionario.getNome());
        assertEquals("ATENDENTE", funcionario.getCargo().getNome());
        assertEquals("Novo Nome", result.getNome());
        assertEquals(userId, result.getUserId());
        verify(gateway).salvarFuncionario(funcionario);
    }

    @Test
    @DisplayName("Deve falhar quando cargo nao existir")
    void shouldFailWhenCargoNotFound() {
        var request = new AtualizarFuncionarioRequest("Nome", 99L);
        var input = request.toUpdateUseCaseInput(1L);
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Nome", new Cargo(1L, "MECANICO", null), userId);

        when(gateway.getFuncionarioById(1L)).thenReturn(funcionario);
        when(gateway.isUsuarioAtivo(userId)).thenReturn(true);
        when(gateway.getCargoPorId(99L)).thenThrow(new IllegalArgumentException("Cargo não encontrado"));

        var useCase = new AtualizarFuncionarioUseCase(gateway, logger);
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));
        verify(gateway, never()).salvarFuncionario(funcionario);
    }

    @Test
    @DisplayName("Deve lançar InactiveUserException quando usuário estiver inativo")
    void shouldThrowInactiveUserExceptionWhenUserIsInactive() {
        var request = new AtualizarFuncionarioRequest("Novo Nome", 2L);
        var input = request.toUpdateUseCaseInput(1L);
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Nome Antigo", new Cargo(1L, "MECANICO", null), userId);

        when(gateway.getFuncionarioById(1L)).thenReturn(funcionario);
        when(gateway.isUsuarioAtivo(userId)).thenReturn(false);

        var useCase = new AtualizarFuncionarioUseCase(gateway, logger);
        assertThrows(InactiveUserException.class, () -> useCase.execute(input));
        verify(gateway, never()).getCargoPorId(any());
        verify(gateway, never()).salvarFuncionario(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando funcionário não for encontrado")
    void shouldPropagateExceptionWhenFuncionarioNotFound() {
        var request = new AtualizarFuncionarioRequest("Novo Nome", 2L);
        var input = request.toUpdateUseCaseInput(99L);

        when(gateway.getFuncionarioById(99L)).thenThrow(FuncionarioNotFoundException.fromId(99L));

        var useCase = new AtualizarFuncionarioUseCase(gateway, logger);
        assertThrows(FuncionarioNotFoundException.class, () -> useCase.execute(input));
        verify(gateway, never()).isUsuarioAtivo(any());
        verify(gateway, never()).salvarFuncionario(any());
    }
}

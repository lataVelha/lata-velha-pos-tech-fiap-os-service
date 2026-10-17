package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesativarFuncionarioUseCaseTest {

    @Mock
    private DesativarFuncionarioGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve desativar funcionario ativo com sucesso")
    void deveDesativarFuncionarioAtivoComSucesso() {
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Fulano", new Cargo(1L, "MECANICO", null), userId);

        when(gateway.getFuncionarioById(1L)).thenReturn(funcionario);

        new DesativarFuncionarioUseCase(gateway, logger).execute(1L);

        verify(gateway).desativarUsuario(userId);
    }

    @Test
    @DisplayName("Deve lançar FuncionarioNotFoundException para id inexistente")
    void deveLancarExcecaoParaFuncionarioInexistente() {
        when(gateway.getFuncionarioById(99L))
                .thenThrow(FuncionarioNotFoundException.fromId(99L));

        var useCase = new DesativarFuncionarioUseCase(gateway, logger);
        assertThrows(FuncionarioNotFoundException.class, () -> useCase.execute(99L));
        verify(gateway, never()).desativarUsuario(any());
    }

    @Test
    @DisplayName("Deve desativar usuário vinculado ao funcionario")
    void deveDesativarUsuarioVinculadoAoFuncionario() {
        UserId userId = UserId.random();
        var funcionario = new Funcionario(2L, "Ciclano", new Cargo(1L, "ATENDENTE", null), userId);

        when(gateway.getFuncionarioById(2L)).thenReturn(funcionario);

        new DesativarFuncionarioUseCase(gateway, logger).execute(2L);

        verify(gateway).getFuncionarioById(2L);
        verify(gateway).desativarUsuario(userId);
    }
}

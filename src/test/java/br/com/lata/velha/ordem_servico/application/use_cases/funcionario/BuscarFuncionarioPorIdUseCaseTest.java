package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarFuncionarioPorIdUseCaseTest {

    @Mock
    private BuscarFuncionarioPorIdGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve buscar funcionario ativo por ID com sucesso")
    void deveBuscarFuncionarioAtivoPorIdComSucesso() {
        var funcionario = new Funcionario(1L, "Fulano", new Cargo(1L, "MECANICO", null), UserId.random());

        when(gateway.getFuncionarioById(1L)).thenReturn(funcionario);

        var useCase = new BuscarFuncionarioPorIdUseCase(gateway, logger);
        var result = useCase.execute(1L);

        assertEquals(1L, result.getId());
        assertEquals("Fulano", result.getNome());
        assertEquals("MECANICO", result.getCargo().getNome());
        verify(gateway).getFuncionarioById(1L);
    }

    @Test
    @DisplayName("Deve falhar ao buscar funcionario inexistente")
    void deveFalharAoBuscarFuncionarioInexistente() {
        when(gateway.getFuncionarioById(99L)).thenThrow(new IllegalArgumentException("Funcionario nao encontrado"));

        var useCase = new BuscarFuncionarioPorIdUseCase(gateway, logger);
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L));
    }
}

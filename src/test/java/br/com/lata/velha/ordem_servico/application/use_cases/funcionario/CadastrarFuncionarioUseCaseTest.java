package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserResponseDto;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.CargoNotFoundException;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarFuncionarioUseCaseTest {

    @Mock
    private CadastrarFuncionarioGateway gateway;

    @Mock
    private AuthenticationService authService;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve cadastrar funcionário com sucesso")
    void deveCadastrarFuncionarioComSucesso() {
        var input = new CadastrarFuncionarioUseCase.Input("Fulano", "fulano@example.com", "Senha123!", 1L, "33445566739");
        var cargo = new Cargo(1L, "MECANICO", null);
        var userId = UserId.random();
        var savedDomain = new Funcionario(10L, "Fulano", cargo, userId);

        when(gateway.getCargoPorId(1L)).thenReturn(cargo);
        when(gateway.salvarFuncionario(any(Funcionario.class))).thenReturn(savedDomain);
        when(authService.getRolesForCargo(1L)).thenReturn(Set.of());
        when(authService.createUser(any())).thenReturn(new CreateAuthUserResponseDto(userId.getValue()));

        var useCase = new CadastrarFuncionarioUseCase(gateway, authService, logger);
        var result = useCase.execute(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNome()).isEqualTo("Fulano");
        assertThat(result.getCargo().getNome()).isEqualTo("MECANICO");
        assertThat(result.getUserId()).isEqualTo(userId);
        verify(gateway).salvarFuncionario(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceAlreadyExistsException ao cadastrar email já existente")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        var input = new CadastrarFuncionarioUseCase.Input("Fulano", "fulano@example.com", "Senha123!", 1L, "44556677840");
        var cargo = new Cargo(1L, "MECANICO", null);

        when(gateway.getCargoPorId(1L)).thenReturn(cargo);
        when(authService.getRolesForCargo(1L)).thenReturn(Set.of());
        when(authService.createUser(any())).thenThrow(new ResourceAlreadyExistsException(""));

        var useCase = new CadastrarFuncionarioUseCase(gateway, authService, logger);
        assertThrows(ResourceAlreadyExistsException.class, () -> useCase.execute(input));
        verify(gateway, never()).salvarFuncionario(any());
    }

    @Test
    @DisplayName("Deve falhar ao tentar cadastrar funcionário com cargo inexistente")
    void deveFalharAoCriarFuncionarioComCargoInexistente() {
        var input = new CadastrarFuncionarioUseCase.Input("Fulano", "fulano@example.com", "Senha123!", 99L, "55667788950");

        when(gateway.getCargoPorId(99L)).thenThrow(CargoNotFoundException.fromId(99L));

        var useCase = new CadastrarFuncionarioUseCase(gateway, authService, logger);
        assertThrows(CargoNotFoundException.class, () -> useCase.execute(input));
        verify(gateway, never()).salvarFuncionario(any(Funcionario.class));
    }
}

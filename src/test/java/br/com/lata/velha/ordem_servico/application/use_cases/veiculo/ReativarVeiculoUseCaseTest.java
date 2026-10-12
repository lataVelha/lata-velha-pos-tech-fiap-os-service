package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReativarVeiculoUseCaseTest {

    @Mock
    private ReativarVeiculoGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve reativar veículo inativo")
    void shouldReactivateVeiculo() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        veiculo.deactivate();

        when(gateway.getVeiculoInativoPorId(1L)).thenReturn(veiculo);
        when(gateway.salvarVeiculo(veiculo)).thenReturn(veiculo);

        ReativarVeiculoUseCase useCase = new ReativarVeiculoUseCase(gateway, logger);
        var result = useCase.execute(1L);

        assertThat(veiculo.isAtivo()).isTrue();
        assertThat(result).isNotNull();
        verify(gateway).getVeiculoInativoPorId(1L);
        verify(gateway).salvarVeiculo(veiculo);
    }
}

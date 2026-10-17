package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarVeiculoUseCaseTest {

    @Mock
    private AtualizarVeiculoGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve atualizar veículo com sucesso")
    void shouldUpdateVeiculo() {
        VeiculoRequest request = new VeiculoRequest(1L, "ABC1234", "Toyota", "Corolla", 2023, "Preto");
        Veiculo existing = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        Veiculo saved = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Toyota", "Corolla", 2023, "Preto");

        when(gateway.getVeiculoPorId(1L)).thenReturn(existing);
        when(gateway.salvarVeiculo(existing)).thenReturn(saved);

        AtualizarVeiculoUseCase useCase = new AtualizarVeiculoUseCase(gateway, logger);
        Veiculo result = useCase.execute(1L, request);

        assertNotNull(result);
        verify(gateway).getVeiculoPorId(1L);
        verify(gateway).getProprietarioAtivoPorId(1L);
        verify(gateway).salvarVeiculo(existing);
    }
}

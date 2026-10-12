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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarVeiculoUseCaseTest {

    @Mock
    private CriarVeiculoGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve criar veículo com sucesso")
    void shouldCreateVeiculo() {
        VeiculoRequest request = new VeiculoRequest(1L, "ABC1234", "Fiat", "Uno", 2020, "Prata");
        Veiculo saved = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");

        when(gateway.salvarVeiculo(any())).thenReturn(saved);

        CriarVeiculoUseCase useCase = new CriarVeiculoUseCase(gateway, logger);
        Veiculo result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPlaca().getFormatted()).isEqualTo("ABC-1234");
        verify(gateway).getProprietarioAtivoPorId(1L);
        verify(gateway).salvarVeiculo(any());
    }
}

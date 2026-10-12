package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarVeiculosPorProprietarioUseCaseTest {

    @Mock
    private ListarVeiculosPorProprietarioGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve listar veículos por proprietário")
    void shouldListByProprietario() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        when(gateway.findByProprietarioId(1L)).thenReturn(List.of(veiculo));

        ListarVeiculosPorProprietarioUseCase useCase = new ListarVeiculosPorProprietarioUseCase(gateway, logger);
        List<Veiculo> result = useCase.execute(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(gateway).findByProprietarioId(1L);
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não tem veículos")
    void shouldReturnEmptyList() {
        when(gateway.findByProprietarioId(99L)).thenReturn(List.of());

        ListarVeiculosPorProprietarioUseCase useCase = new ListarVeiculosPorProprietarioUseCase(gateway, logger);
        List<Veiculo> result = useCase.execute(99L);

        assertThat(result).isEmpty();
        verify(gateway).findByProprietarioId(99L);
    }
}

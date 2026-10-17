package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
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
class ListarVeiculosUseCaseTest {

    @Mock
    private ListarVeiculosGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve listar veículos paginado")
    void shouldListPaginated() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        PaginatedResult<Veiculo> paginatedResult = new PaginatedResult<>(List.of(veiculo), 0, 10, 1L, 1);

        when(gateway.findAll(0, 10)).thenReturn(paginatedResult);

        ListarVeiculosUseCase useCase = new ListarVeiculosUseCase(gateway, logger);
        PaginatedResult<Veiculo> result = useCase.execute(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getId()).isEqualTo(1L);
        verify(gateway).findAll(0, 10);
    }
}

package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
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
class BuscarServicosUseCaseTest {

    @Mock
    private BuscarServicosGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve listar serviços ativos de forma paginada")
    void deveListarServicosAtivosDeFormaPaginada() {
        var s1 = new Servico(1L, "Balanceamento", "Balanceamento das rodas", true);
        var s2 = new Servico(2L, "Troca de óleo", "Substituição do óleo", true);
        var page = new PaginatedResult<>(List.of(s1, s2), 0, 10, 2L, 1);

        when(gateway.findAll(0, 10)).thenReturn(page);

        var useCase = new BuscarServicosUseCase(gateway, logger);
        PaginatedResult<Servico> result = useCase.execute(0, 10);

        assertThat(result.content()).hasSize(2);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.content().get(0).getId()).isEqualTo(1L);
        verify(gateway).findAll(0, 10);
    }
}

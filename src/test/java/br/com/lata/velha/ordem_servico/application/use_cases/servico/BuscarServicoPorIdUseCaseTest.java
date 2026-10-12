package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
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
class BuscarServicoPorIdUseCaseTest {

    @Mock
    private BuscarServicoPorIdGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve buscar serviço ativo por ID")
    void deveBuscarServicoAtivoPorId() {
        var servico = new Servico(1L, "Alinhamento", "Alinhamento completo", true);
        when(gateway.getServicoPorId(1L)).thenReturn(servico);

        var useCase = new BuscarServicoPorIdUseCase(gateway, logger);
        var result = useCase.execute(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Alinhamento");
        verify(gateway).getServicoPorId(1L);
    }
}

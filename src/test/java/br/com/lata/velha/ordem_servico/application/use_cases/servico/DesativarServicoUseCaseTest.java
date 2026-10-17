package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesativarServicoUseCaseTest {

    @Mock
    private DesativarServicoGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve desativar serviço ativo com sucesso")
    void deveDesativarServicoAtivoComSucesso() {
        var servico = new Servico(1L, "Balanceamento", "Balanceamento das rodas", true);
        when(gateway.getServicoPorId(1L)).thenReturn(servico);

        DesativarServicoUseCase useCase = new DesativarServicoUseCase(gateway, logger);
        useCase.execute(1L);

        assertFalse(servico.isAtivo());
        verify(gateway).salvarServico(servico);
    }

    @Test
    @DisplayName("Deve falhar quando serviço já estiver desativado")
    void deveFalharQuandoServicoJaEstiverDesativado() {
        var servico = new Servico(1L, "Balanceamento", "Balanceamento das rodas", false);
        when(gateway.getServicoPorId(1L)).thenReturn(servico);

        DesativarServicoUseCase useCase = new DesativarServicoUseCase(gateway, logger);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L));
        verify(gateway, never()).salvarServico(servico);
    }
}

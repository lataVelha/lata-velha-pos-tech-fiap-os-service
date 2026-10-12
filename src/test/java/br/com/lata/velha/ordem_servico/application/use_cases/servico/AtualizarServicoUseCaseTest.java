package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarServicoUseCaseTest {

    @Mock
    private AtualizarServicoGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve atualizar serviço com sucesso")
    void deveAtualizarServicoComSucesso() {
        var request = new AtualizarServicoRequest("Alinhamento 3D", "Alinhamento eletrônico completo");
        var servico = new Servico(1L, "Alinhamento", "Alinhamento comum", true);

        when(gateway.getServicoPorId(1L)).thenReturn(servico);
        when(gateway.salvarServico(servico)).thenReturn(servico);

        var useCase = new AtualizarServicoUseCase(gateway, logger);
        var result = useCase.execute(1L, request);

        assertThat(servico.getNome()).isEqualTo("Alinhamento 3D");
        assertThat(servico.getDescricao()).isEqualTo("Alinhamento eletrônico completo");
        assertThat(result.getNome()).isEqualTo("Alinhamento 3D");
        verify(gateway).salvarServico(servico);
    }

    @Test
    @DisplayName("Deve falhar quando serviço não existir")
    void deveFalharQuandoServicoNaoExistir() {
        var request = new AtualizarServicoRequest("Nome", "Descrição");
        when(gateway.getServicoPorId(99L)).thenThrow(new IllegalArgumentException("Serviço não encontrado"));

        var useCase = new AtualizarServicoUseCase(gateway, logger);
        assertThatThrownBy(() -> useCase.execute(99L, request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(gateway, never()).salvarServico(any());
    }
}

package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
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
class CadastrarServicoUseCaseTest {

    @Mock
    private CadastrarServicoGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("Deve cadastrar serviço com sucesso")
    void deveCadastrarServicoComSucesso() {
        var request = new CadastrarServicoRequest("Alinhamento", "Alinhamento completo");
        var savedDomain = new Servico(10L, "Alinhamento", "Alinhamento completo", true);

        when(gateway.salvarServico(any())).thenReturn(savedDomain);

        var useCase = new CadastrarServicoUseCase(gateway, logger);
        var result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNome()).isEqualTo("Alinhamento");
        assertThat(result.getDescricao()).isEqualTo("Alinhamento completo");
        verify(gateway).salvarServico(any());
    }
}

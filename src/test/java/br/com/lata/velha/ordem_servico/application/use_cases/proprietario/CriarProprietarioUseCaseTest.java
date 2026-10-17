package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
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
class CriarProprietarioUseCaseTest {

    @Mock
    private CriarProprietarioGateway gateway;

    @Mock
    private NotificarCadastroProprietarioUseCase notificarUseCase;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve criar proprietário com sucesso")
    void shouldCreateProprietario() {
        ProprietarioRequest request = new ProprietarioRequest(
                "João", "joao@email.com", "52998224725", "11999990001", null);
        Proprietario saved = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(gateway.salvarProprietario(any())).thenReturn(saved);

        var useCase = new CriarProprietarioUseCase(gateway, notificarUseCase, logger);
        var result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("João");
        verify(gateway).salvarProprietario(any());
        verify(notificarUseCase).execute(saved);
    }
}

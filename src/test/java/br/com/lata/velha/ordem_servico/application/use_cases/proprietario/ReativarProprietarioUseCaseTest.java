package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReativarProprietarioUseCaseTest {

    @Mock
    private ReativarProprietarioGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve reativar proprietário inativo")
    void shouldReactivateProprietario() {
        Proprietario proprietario = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        proprietario.deactivate();

        when(gateway.getProprietarioInativoPorId(1L)).thenReturn(proprietario);
        when(gateway.salvarProprietario(proprietario)).thenReturn(proprietario);

        ReativarProprietarioUseCase useCase = new ReativarProprietarioUseCase(gateway, logger);
        var result = useCase.execute(1L);

        assertThat(proprietario.isAtivo()).isTrue();
        assertThat(result).isNotNull();
        verify(gateway).getProprietarioInativoPorId(1L);
        verify(gateway).salvarProprietario(proprietario);
    }
}

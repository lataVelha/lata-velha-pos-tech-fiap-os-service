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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesativarProprietarioUseCaseTest {

    @Mock
    private DesativarProprietarioGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve desativar proprietário (soft delete)")
    void shouldDeactivateProprietario() {
        Proprietario proprietario = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(gateway.getProprietarioPorId(1L)).thenReturn(proprietario);
        when(gateway.salvarProprietario(proprietario)).thenReturn(proprietario);

        DesativarProprietarioUseCase useCase = new DesativarProprietarioUseCase(gateway, logger);
        useCase.execute(1L);

        assertFalse(proprietario.isAtivo());
        verify(gateway).getProprietarioPorId(1L);
        verify(gateway).salvarProprietario(proprietario);
    }
}

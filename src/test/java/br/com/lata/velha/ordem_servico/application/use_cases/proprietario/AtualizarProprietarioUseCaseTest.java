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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarProprietarioUseCaseTest {

    @Mock
    private AtualizarProprietarioGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve atualizar proprietário com sucesso")
    void shouldUpdateProprietario() {
        ProprietarioRequest request = new ProprietarioRequest(
                "Maria", "maria@email.com", "52998224725", "11999990001", null);
        Proprietario existing = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        Proprietario saved = new Proprietario(1L, "Maria", "maria@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(gateway.getProprietarioPorId(1L)).thenReturn(existing);
        when(gateway.salvarProprietario(existing)).thenReturn(saved);

        AtualizarProprietarioUseCase useCase = new AtualizarProprietarioUseCase(gateway, logger);
        Proprietario result = useCase.execute(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Maria");
        verify(gateway).getProprietarioPorId(1L);
        verify(gateway).salvarProprietario(existing);
    }
}

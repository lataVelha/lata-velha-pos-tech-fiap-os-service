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
class BuscarProprietarioPorIdUseCaseTest {

    @Mock
    private BuscarProprietarioPorIdGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve buscar proprietário por id")
    void shouldFindById() {
        Proprietario domain = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        when(gateway.getProprietarioPorId(1L)).thenReturn(domain);

        BuscarProprietarioPorIdUseCase useCase = new BuscarProprietarioPorIdUseCase(gateway, logger);
        Proprietario result = useCase.execute(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("João");
        verify(gateway).getProprietarioPorId(1L);
    }
}

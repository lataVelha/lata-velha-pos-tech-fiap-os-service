package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
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
class ListarProprietariosUseCaseTest {

    @Mock
    private ListarProprietariosGateway gateway;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("deve listar proprietários paginado")
    void shouldListPaginated() {
        Proprietario domain = new Proprietario(1L, "João", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        PaginatedResult<Proprietario> paginatedResult = new PaginatedResult<>(List.of(domain), 0, 10, 1L, 1);

        when(gateway.findAll(0, 10)).thenReturn(paginatedResult);

        var useCase = new ListarProprietariosUseCase(gateway, logger);
        PaginatedResult<Proprietario> result = useCase.execute(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getId()).isEqualTo(1L);
        verify(gateway).findAll(0, 10);
    }
}

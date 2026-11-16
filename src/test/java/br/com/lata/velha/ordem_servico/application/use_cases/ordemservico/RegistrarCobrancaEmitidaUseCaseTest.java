package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.events.CobrancaEmitidaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrarCobrancaEmitidaUseCaseTest {

    private static final Long OS_ID = 42L;

    @Mock
    private Logger logger;

    private RegistrarCobrancaEmitidaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarCobrancaEmitidaUseCase(logger);
    }

    @Test
    @DisplayName("deve registrar o recebimento da cobrança sem alterar estado")
    void deveRegistrarRecebimento() {
        useCase.execute(new CobrancaEmitidaEvent(OS_ID));

        verify(logger).logInfo(any(String.class), eq(OS_ID));
    }
}

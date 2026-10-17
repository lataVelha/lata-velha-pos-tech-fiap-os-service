package br.com.lata.velha.ordem_servico.infrastructure.observability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
class HistoricoMetricsTest {

    @Autowired
    private HistoricoMetrics historicoMetrics;

    @Test
    @DisplayName("deve injetar NoOpHistoricoMetrics quando OTel está desativado")
    void deveInjetarNoOpQuandoOtelDesativado() {
        assertThat(historicoMetrics).isNotNull();
        assertThat(historicoMetrics).isInstanceOf(NoOpHistoricoMetrics.class);
    }

    @Test
    @DisplayName("não deve lançar exceção ao registrar métrica com OTel desativado")
    void naoDeveLancarExcecaoAoRegistrar() {
        assertThatCode(() -> historicoMetrics.registrar("EM_DIAGNOSTICO", 60))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deve ignorar valores inválidos sem lançar exceção")
    void deveIgnorarValoresInvalidos() {
        assertThatCode(() -> historicoMetrics.registrar(null, 60))
                .doesNotThrowAnyException();
        assertThatCode(() -> historicoMetrics.registrar("EM_DIAGNOSTICO", -1))
                .doesNotThrowAnyException();
    }
}

package br.com.lata.velha.ordem_servico.infrastructure.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implementação no-op de HistoricoMetrics para quando OTel está desativado.
 * Permite que o código chame registrar() sem verificar se OTel está ativo.
 */
@Component
@ConditionalOnProperty(name = "otel.exporter.otlp.enabled", havingValue = "false")
public class NoOpHistoricoMetrics extends HistoricoMetrics {

    private static final Logger logger = LoggerFactory.getLogger(NoOpHistoricoMetrics.class);

    public NoOpHistoricoMetrics() {
        super();
        logger.info("NoOpHistoricoMetrics ativo - métricas de histórico desabilitadas");
    }

    @Override
    public void registrar(String estado, long segundos) {
        // No-op: não emite métrica quando OTel está desativado
    }
}

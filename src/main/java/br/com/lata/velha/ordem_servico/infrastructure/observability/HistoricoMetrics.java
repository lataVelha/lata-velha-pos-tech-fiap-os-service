package br.com.lata.velha.ordem_servico.infrastructure.observability;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Componente de métricas para histórico de estados da OS.
 * Emite histograma de duração por estado via OpenTelemetry.
 */
@Component
@ConditionalOnProperty(name = "otel.exporter.otlp.enabled", havingValue = "true", matchIfMissing = true)
public class HistoricoMetrics {

    private static final Logger logger = LoggerFactory.getLogger(HistoricoMetrics.class);
    private static final String METRIC_NAME = "os.estado.duracao";
    private static final AttributeKey<String> ESTADO_TAG = AttributeKey.stringKey("estado");

    private final DoubleHistogram estadoDuracao;

    public HistoricoMetrics() {
        Meter meter = GlobalOpenTelemetry.getMeter("lata-velha");
        this.estadoDuracao = meter.histogramBuilder(METRIC_NAME)
                .setDescription("Duração de cada estado da OS em segundos")
                .setUnit("s")
                .build();
        logger.info("HistoricoMetrics inicializado - métrica '{}' registrada", METRIC_NAME);
    }

    /**
     * Registra a duração de um estado da OS.
     *
     * @param estado    nome do estado (ex: EM_DIAGNOSTICO, EM_EXECUCAO)
     * @param segundos  duração em segundos
     */
    public void registrar(String estado, long segundos) {
        if (estado == null || segundos < 0) {
            logger.warn("Métrica ignorada: estado={}, segundos={}", estado, segundos);
            return;
        }
        estadoDuracao.record(segundos, Attributes.of(ESTADO_TAG, estado));
        logger.debug("Métrica registrada: {}={}s", estado, segundos);
    }
}

package br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos;

import br.com.lata.velha.ordem_servico.domain.view.TempoMedioPorEstado;
import br.com.lata.velha.shared.application.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

public class BuscarHistoricoEstadoUseCase {

    private final BuscarHistoricoEstadoOsGateway buscarHistoricoEstadoOsGateway;
    private final Logger logger;

    public BuscarHistoricoEstadoUseCase(BuscarHistoricoEstadoOsGateway buscarHistoricoEstadoOsGateway, Logger logger) {
        this.buscarHistoricoEstadoOsGateway = buscarHistoricoEstadoOsGateway;
        this.logger = logger;
    }

    public List<TempoMedioPorEstado> execute(Input input) {
        if (input.inicio() == null && input.fim() == null)
            throw new IllegalArgumentException("Informe ao menos uma data: inicio ou fim.");

        LocalDateTime fim = input.fim() != null ? input.fim() : LocalDateTime.now();
        LocalDateTime inicio = input.inicio();
        if (inicio != null && inicio.isAfter(fim))
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");

        logger.logInfo("Buscando tempo médio por estado da OS - inicio={}, fim={}", inicio, fim);
        return buscarHistoricoEstadoOsGateway.buscarTempoMedioPorEstado(inicio, fim, LocalDateTime.now());
    }

    public record Input(LocalDateTime inicio, LocalDateTime fim) {
    }
}

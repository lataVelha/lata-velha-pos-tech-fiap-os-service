package br.com.lata.velha.ordem_servico.application.controllers.historicoestadoos;

import br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos.BuscarHistoricoEstadoUseCase;
import br.com.lata.velha.ordem_servico.domain.view.TempoMedioPorEstado;
import br.com.lata.velha.shared.application.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

public class HistoricoEstadoOsCleanController {

    private final Logger logger;
    private final BuscarHistoricoEstadoUseCase buscarHistoricoEstadoUseCase;

    public HistoricoEstadoOsCleanController(Logger logger, BuscarHistoricoEstadoUseCase buscarHistoricoEstadoUseCase) {
        this.logger = logger;
        this.buscarHistoricoEstadoUseCase = buscarHistoricoEstadoUseCase;
    }

    public List<TempoMedioPorEstado> buscarTempoMedioPorEstado(LocalDateTime inicio, LocalDateTime fim) {
        logger.logInfo("Buscando tempo médio por estado da OS - inicio={}, fim={}", inicio, fim);
        BuscarHistoricoEstadoUseCase.Input input = new BuscarHistoricoEstadoUseCase.Input(inicio, fim);
        return buscarHistoricoEstadoUseCase.execute(input);
    }
}

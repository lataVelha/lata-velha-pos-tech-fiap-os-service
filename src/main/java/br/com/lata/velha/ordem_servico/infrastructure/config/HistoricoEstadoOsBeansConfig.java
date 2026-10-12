package br.com.lata.velha.ordem_servico.infrastructure.config;

import br.com.lata.velha.ordem_servico.application.controllers.historicoestadoos.HistoricoEstadoOsCleanController;
import br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos.BuscarHistoricoEstadoOsGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos.BuscarHistoricoEstadoUseCase;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HistoricoEstadoOsBeansConfig {

    private final Logger logger;
    private final BuscarHistoricoEstadoOsGateway buscarHistoricoEstadoOsGateway;

    @Bean
    public BuscarHistoricoEstadoUseCase buscarHistoricoEstadoUseCase() {
        logger.logInfo("Configurando BuscarHistoricoEstadoUseCase");
        return new BuscarHistoricoEstadoUseCase(buscarHistoricoEstadoOsGateway, logger);
    }

    @Bean
    public HistoricoEstadoOsCleanController historicoEstadoOsCleanController(BuscarHistoricoEstadoUseCase buscarHistoricoEstadoUseCase) {
        logger.logInfo("Configurando HistoricoEstadoOsCleanController");
        return new HistoricoEstadoOsCleanController(logger, buscarHistoricoEstadoUseCase);
    }
}

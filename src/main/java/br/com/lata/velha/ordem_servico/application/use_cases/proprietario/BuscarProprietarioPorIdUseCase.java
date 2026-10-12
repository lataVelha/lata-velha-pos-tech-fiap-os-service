package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.application.logging.Logger;

public class BuscarProprietarioPorIdUseCase {

    private final BuscarProprietarioPorIdGateway gateway;
    private final Logger logger;

    public BuscarProprietarioPorIdUseCase(BuscarProprietarioPorIdGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Proprietario execute(Long id) {
        logger.logInfo("Buscando proprietário por id - proprietarioId={}", id);
        return gateway.getProprietarioPorId(id);
    }
}

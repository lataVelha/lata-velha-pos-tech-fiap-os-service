package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class ListarProprietariosUseCase {

    private final ListarProprietariosGateway gateway;
    private final Logger logger;

    public ListarProprietariosUseCase(ListarProprietariosGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public PaginatedResult<Proprietario> execute(int page, int size) {
        logger.logInfo("Listando proprietários - page={}, size={}", page, size);
        return gateway.findAll(page, size);
    }
}

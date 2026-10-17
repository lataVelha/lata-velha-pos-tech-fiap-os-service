package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.application.logging.Logger;

public class BuscarFuncionarioPorIdUseCase {

    private final BuscarFuncionarioPorIdGateway gateway;
    private final Logger logger;

    public BuscarFuncionarioPorIdUseCase(BuscarFuncionarioPorIdGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Funcionario execute(Long id) {
        logger.logInfo("Buscando funcionário por id - funcionarioId={}", id);
        return gateway.getFuncionarioById(id);
    }
}

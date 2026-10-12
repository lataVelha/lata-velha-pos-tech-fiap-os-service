package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.shared.application.logging.Logger;

public class DesativarFuncionarioUseCase {

    private final DesativarFuncionarioGateway gateway;
    private final Logger logger;

    public DesativarFuncionarioUseCase(DesativarFuncionarioGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public void execute(Long id) {
        logger.logInfo("Buscando funcionário para desativação - funcionarioId={}", id);
        var funcionario = gateway.getFuncionarioById(id);

        logger.logInfo("Desativando usuário de autenticação do funcionário - funcionarioId={}", id);
        gateway.desativarUsuario(funcionario.getUserId());
        logger.logInfo("Funcionário desativado com sucesso - funcionarioId={}", id);
    }
}

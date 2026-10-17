package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.application.logging.Logger;

public class AtualizarFuncionarioUseCase {

    private final AtualizarFuncionarioGateway gateway;
    private final Logger logger;

    public AtualizarFuncionarioUseCase(AtualizarFuncionarioGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Funcionario execute(Input input) {
        logger.logInfo("Atualizando funcionário - funcionarioId={}", input.id());
        var funcionario = gateway.getFuncionarioById(input.id());
        if (!gateway.isUsuarioAtivo(funcionario.getUserId())) {
            logger.logWarn("Atualização de funcionário rejeitada: usuário inativo - funcionarioId={}", input.id());
            throw InactiveUserException.fromEntityName("Funcionário");
        }

        logger.logDebug("Buscando novo cargo do funcionário - funcionarioId={}, cargoId={}", input.id(), input.cargoId());
        var cargo = gateway.getCargoPorId(input.cargoId());
        funcionario.update(input.nome(), cargo);
        var updated = gateway.salvarFuncionario(funcionario);
        logger.logInfo("Funcionário atualizado com sucesso - funcionarioId={}", input.id());
        return updated;
    }

    public record Input(Long id, String nome, Long cargoId) {}
}

package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserDto;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class CadastrarFuncionarioUseCase {

    private final CadastrarFuncionarioGateway gateway;
    private final AuthenticationService authService;
    private final Logger logger;

    public CadastrarFuncionarioUseCase(CadastrarFuncionarioGateway gateway, AuthenticationService authService, Logger logger) {
        this.gateway = gateway;
        this.authService = authService;
        this.logger = logger;
    }

    public Funcionario execute(Input input) {
        logger.logInfo("Buscando cargo para cadastro de funcionário - cargoId={}", input.cargoId());
        var cargo = gateway.getCargoPorId(input.cargoId());

        logger.logInfo("Criando usuário de autenticação para o funcionário - cargoId={}", input.cargoId());
        var userId = createUser(input);
        var funcionario = Funcionario.create(input.nome(), cargo, userId);

        logger.logInfo("Salvando funcionário - cargoId={}", input.cargoId());
        var saved = gateway.salvarFuncionario(funcionario);
        logger.logInfo("Funcionário cadastrado com sucesso - funcionarioId={}", saved.getId());
        return saved;
    }

    private UserId createUser(Input input) {
        var roles = authService.getRolesForCargo(input.cargoId());
        var createUserDto = new CreateAuthUserDto(input.username(), input.senha(), roles, input.cpf());
        var userResponse = authService.createUser(createUserDto);
        return UserId.create(userResponse.userId());
    }

    public record Input(String nome, String username, String senha, Long cargoId, String cpf) {}
}

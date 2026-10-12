package br.com.lata.velha.ordem_servico.application.controllers.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.AtualizarFuncionarioPresenter;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.BuscarFuncionarioPorIdPresenter;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.CadastrarFuncionarioPresenter;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.*;
import br.com.lata.velha.shared.application.logging.Logger;

public class FuncionarioCleanController {

    private final CadastrarFuncionarioGateway cadastrarGateway;
    private final AuthenticationService authService;
    private final CadastrarFuncionarioPresenter cadastrarPresenter;

    private final AtualizarFuncionarioGateway atualizarGateway;
    private final AtualizarFuncionarioPresenter atualizarPresenter;

    private final BuscarFuncionarioPorIdGateway buscarGateway;
    private final BuscarFuncionarioPorIdPresenter buscarPresenter;

    private final DesativarFuncionarioGateway desativarGateway;

    private final Logger logger;

    public FuncionarioCleanController(CadastrarFuncionarioGateway cadastrarGateway,
                                      AuthenticationService authService,
                                      CadastrarFuncionarioPresenter cadastrarPresenter,
                                      AtualizarFuncionarioGateway atualizarGateway,
                                      AtualizarFuncionarioPresenter atualizarPresenter,
                                      BuscarFuncionarioPorIdGateway buscarGateway,
                                      BuscarFuncionarioPorIdPresenter buscarPresenter,
                                      DesativarFuncionarioGateway desativarGateway,
                                      Logger logger) {
        this.cadastrarGateway = cadastrarGateway;
        this.authService = authService;
        this.cadastrarPresenter = cadastrarPresenter;
        this.atualizarGateway = atualizarGateway;
        this.atualizarPresenter = atualizarPresenter;
        this.buscarGateway = buscarGateway;
        this.buscarPresenter = buscarPresenter;
        this.desativarGateway = desativarGateway;
        this.logger = logger;
    }

    public FuncionarioResponse cadastrar(CadastrarFuncionarioUseCase.Input input) {
        logger.logInfo("Iniciando cadastro de funcionário - cargoId={}", input.cargoId());
        var funcionario = new CadastrarFuncionarioUseCase(cadastrarGateway, authService, logger).execute(input);
        logger.logInfo("Cadastro de funcionário concluído com sucesso - funcionarioId={}", funcionario.getId());
        return cadastrarPresenter.present(funcionario);
    }

    public FuncionarioResponse buscarPorId(Long id) {
        logger.logInfo("Iniciando busca de funcionário por id - funcionarioId={}", id);
        var funcionario = new BuscarFuncionarioPorIdUseCase(buscarGateway, logger).execute(id);
        logger.logInfo("Busca de funcionário por id concluída com sucesso - funcionarioId={}", id);
        return buscarPresenter.present(funcionario);
    }

    public FuncionarioResponse atualizar(AtualizarFuncionarioUseCase.Input input) {
        logger.logInfo("Iniciando atualização de funcionário - funcionarioId={}", input.id());
        var funcionario = new AtualizarFuncionarioUseCase(atualizarGateway, logger).execute(input);
        logger.logInfo("Atualização de funcionário concluída com sucesso - funcionarioId={}", input.id());
        return atualizarPresenter.present(funcionario);
    }

    public void desativar(Long id) {
        logger.logInfo("Iniciando desativação de funcionário - funcionarioId={}", id);
        new DesativarFuncionarioUseCase(desativarGateway, logger).execute(id);
        logger.logInfo("Desativação de funcionário concluída com sucesso - funcionarioId={}", id);
    }
}

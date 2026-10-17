package br.com.lata.velha.ordem_servico.infrastructure.config;

import br.com.lata.velha.ordem_servico.application.controllers.funcionario.FuncionarioCleanController;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.AtualizarFuncionarioPresenter;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.BuscarFuncionarioPorIdPresenter;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.CadastrarFuncionarioPresenter;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.*;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FuncionarioBeansConfig {

    private final Logger logger;
    private final CadastrarFuncionarioGateway cadastrarGateway;
    private final AuthenticationService authService;
    private final CadastrarFuncionarioPresenter cadastrarPresenter;
    private final AtualizarFuncionarioGateway atualizarGateway;
    private final AtualizarFuncionarioPresenter atualizarPresenter;
    private final BuscarFuncionarioPorIdGateway buscarGateway;
    private final BuscarFuncionarioPorIdPresenter buscarPresenter;
    private final DesativarFuncionarioGateway desativarGateway;

    @Bean
    public FuncionarioCleanController funcionarioCleanController() {
        logger.logInfo("Configurando FuncionarioCleanController");
        return new FuncionarioCleanController(
                cadastrarGateway,
                authService,
                cadastrarPresenter,
                atualizarGateway,
                atualizarPresenter,
                buscarGateway,
                buscarPresenter,
                desativarGateway,
                logger);
    }
}

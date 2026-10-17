package br.com.lata.velha.ordem_servico.infrastructure.config;

import br.com.lata.velha.ordem_servico.application.controllers.servico.ServicoCleanController;
import br.com.lata.velha.ordem_servico.application.presenters.servico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.servico.*;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ServicoBeansConfig {

    private final Logger logger;
    private final CadastrarServicoGateway cadastrarGateway;
    private final CadastrarServicoPresenter cadastrarPresenter;
    private final AtualizarServicoGateway atualizarGateway;
    private final AtualizarServicoPresenter atualizarPresenter;
    private final BuscarServicoPorIdGateway buscarPorIdGateway;
    private final BuscarServicoPorIdPresenter buscarPorIdPresenter;
    private final BuscarServicosGateway buscarTodosGateway;
    private final BuscarServicosPresenter buscarTodosPresenter;
    private final DesativarServicoGateway desativarGateway;

    @Bean
    public ServicoCleanController servicoCleanController() {
        logger.logInfo("Configurando ServicoCleanController");
        return new ServicoCleanController(
                cadastrarGateway,
                cadastrarPresenter,
                atualizarGateway,
                atualizarPresenter,
                buscarPorIdGateway,
                buscarPorIdPresenter,
                buscarTodosGateway,
                buscarTodosPresenter,
                desativarGateway,
                logger);
    }
}

package br.com.lata.velha.ordem_servico.infrastructure.config;

import br.com.lata.velha.ordem_servico.application.controllers.proprietario.ProprietarioCleanController;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.presenters.proprietario.*;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.*;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProprietarioBeansConfig {

    private final Logger logger;
    private final CriarProprietarioGateway criarGateway;
    private final CriarProprietarioPresenter criarPresenter;
    private final AtualizarProprietarioGateway atualizarGateway;
    private final AtualizarProprietarioPresenter atualizarPresenter;
    private final BuscarProprietarioPorIdGateway buscarPorIdGateway;
    private final BuscarProprietarioPorIdPresenter buscarPorIdPresenter;
    private final BuscarProprietarioPorDocumentoGateway buscarPorDocumentoGateway;
    private final BuscarProprietarioPorDocumentoPresenter buscarPorDocumentoPresenter;
    private final ListarProprietariosGateway listarGateway;
    private final ListarProprietariosPresenter listarPresenter;
    private final DesativarProprietarioGateway desativarGateway;
    private final ReativarProprietarioGateway reativarGateway;
    private final ReativarProprietarioPresenter reativarPresenter;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    @Bean
    public ProprietarioCleanController proprietarioCleanController() {
        logger.logInfo("Configurando ProprietarioCleanController");
        return new ProprietarioCleanController(
                criarGateway,
                criarPresenter,
                atualizarGateway,
                atualizarPresenter,
                buscarPorIdGateway,
                buscarPorIdPresenter,
                buscarPorDocumentoGateway,
                buscarPorDocumentoPresenter,
                listarGateway,
                listarPresenter,
                desativarGateway,
                reativarGateway,
                reativarPresenter,
                emailProvider,
                templateProvider,
                logger);
    }
}

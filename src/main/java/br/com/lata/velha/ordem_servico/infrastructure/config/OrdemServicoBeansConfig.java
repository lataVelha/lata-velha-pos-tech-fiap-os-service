package br.com.lata.velha.ordem_servico.infrastructure.config;

import br.com.lata.velha.ordem_servico.application.controllers.ordemservico.OrdemServicoCleanController;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.CriarProprietarioGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.CriarVeiculoGateway;
import br.com.lata.velha.shared.application.gateways.EventPublisher;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OrdemServicoBeansConfig {

    private final Logger logger;
    private final NotificarOrdemServicoGateway notificarGateway;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    private final CriarOrdemServicoGateway criarGateway;
    private final CriarOrdemServicoPresenter criarPresenter;
    private final CriarProprietarioGateway criarProprietarioGateway;
    private final CriarVeiculoGateway criarVeiculoGateway;
    private final AdicionarServicoGateway adicionarGateway;
    private final AprovarOrdemServicoGateway aprovarGateway;
    private final AprovarOrdemServicoPresenter aprovarPresenter;
    private final BuscarOrdemServicoGateway buscarGateway;
    private final BuscarOrdemServicoPresenter buscarPresenter;
    private final BuscarOrdensPorStatusOrdenadoGateway buscarOrdensPorStatusGateway;
    private final BuscarOrdensPorStatusPresenter buscarOrdensPorStatusPresenter;
    private final BuscarTempoMedioExecucaoGateway buscarTempoMedioGateway;
    private final BuscarTempoMedioExecucaoPresenter buscarTempoMedioPresenter;
    private final IniciarDiagnosticoGateway iniciarDiagnosticoGateway;
    private final FinalizarDiagnosticoGateway finalizarDiagnosticoGateway;
    private final ReprovarOrdemServicoGateway reprovarGateway;
    private final IniciarServicoGateway iniciarServicoGateway;
    private final FinalizarServicoGateway finalizarServicoGateway;
    private final RetirarVeiculoGateway retirarVeiculoGateway;
    private final ReceberAprovacaoOrcamentoClienteGateway receberAprovacaoGateway;
    private final ReceberAprovacaoOrcamentoClientePresenter receberAprovacaoPresenter;
    private final EventPublisher eventPublisher;

    @Bean
    public OrdemServicoCleanController ordemServicoCleanController() {
        logger.logInfo("Configurando OrdemServicoCleanController");
        return new OrdemServicoCleanController(
                notificarGateway,
                emailProvider,
                templateProvider,
                criarGateway,
                criarPresenter,
                criarProprietarioGateway,
                criarVeiculoGateway,
                adicionarGateway,
                aprovarGateway,
                aprovarPresenter,
                buscarGateway,
                buscarPresenter,
                buscarOrdensPorStatusGateway,
                buscarOrdensPorStatusPresenter,
                buscarTempoMedioGateway,
                buscarTempoMedioPresenter,
                iniciarDiagnosticoGateway,
                finalizarDiagnosticoGateway,
                reprovarGateway,
                iniciarServicoGateway,
                finalizarServicoGateway,
                retirarVeiculoGateway,
                receberAprovacaoGateway,
                receberAprovacaoPresenter,
                eventPublisher,
                logger);
    }
}

package br.com.lata.velha.ordem_servico.infrastructure.config;

import br.com.lata.velha.ordem_servico.application.event_handlers.DeducaoEstoqueFalhouEventHandler;
import br.com.lata.velha.ordem_servico.application.event_handlers.InventoryCheckedEventHandler;
import br.com.lata.velha.ordem_servico.application.event_handlers.OrcamentoReprovadoEventHandler;
import br.com.lata.velha.ordem_servico.application.event_handlers.PecaCompletamenteAlocadaEventHandler;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoGateway;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberInventoryCheckedGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberInventoryCheckedUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberOrcamentoReprovadoGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberOrcamentoReprovadoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberPecaCompletamenteAlocadaGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberPecaCompletamenteAlocadaUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.RegistrarFalhaDeducaoEstoqueUseCase;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OrdemServicoEventosBeansConfig {

    private final Logger logger;
    private final NotificarOrdemServicoGateway notificarGateway;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;
    private final ReceberInventoryCheckedGateway receberInventoryCheckedGateway;
    private final ReceberOrcamentoReprovadoGateway receberOrcamentoReprovadoGateway;
    private final ReceberPecaCompletamenteAlocadaGateway receberPecaCompletamenteAlocadaGateway;

    @Bean
    public ReceberInventoryCheckedUseCase receberInventoryCheckedUseCase() {
        return new ReceberInventoryCheckedUseCase(receberInventoryCheckedGateway, notificarOrdemServicoService(), logger);
    }

    @Bean
    public ReceberOrcamentoReprovadoUseCase receberOrcamentoReprovadoUseCase() {
        return new ReceberOrcamentoReprovadoUseCase(receberOrcamentoReprovadoGateway, notificarOrdemServicoService(), logger);
    }

    @Bean
    public ReceberPecaCompletamenteAlocadaUseCase receberPecaCompletamenteAlocadaUseCase() {
        return new ReceberPecaCompletamenteAlocadaUseCase(receberPecaCompletamenteAlocadaGateway, logger);
    }

    @Bean
    public RegistrarFalhaDeducaoEstoqueUseCase registrarFalhaDeducaoEstoqueUseCase() {
        return new RegistrarFalhaDeducaoEstoqueUseCase(logger);
    }

    @Bean
    public InventoryCheckedEventHandler inventoryCheckedEventHandler() {
        return new InventoryCheckedEventHandler(receberInventoryCheckedUseCase());
    }

    @Bean
    public OrcamentoReprovadoEventHandler orcamentoReprovadoEventHandler() {
        return new OrcamentoReprovadoEventHandler(receberOrcamentoReprovadoUseCase());
    }

    @Bean
    public PecaCompletamenteAlocadaEventHandler pecaCompletamenteAlocadaEventHandler() {
        return new PecaCompletamenteAlocadaEventHandler(receberPecaCompletamenteAlocadaUseCase());
    }

    @Bean
    public DeducaoEstoqueFalhouEventHandler deducaoEstoqueFalhouEventHandler() {
        return new DeducaoEstoqueFalhouEventHandler(registrarFalhaDeducaoEstoqueUseCase());
    }

    private NotificarOrdemServicoService notificarOrdemServicoService() {
        return new NotificarOrdemServicoService(notificarGateway, emailProvider, templateProvider, logger);
    }
}

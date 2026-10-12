package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class FinalizarServicoUseCase {

    private final FinalizarServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public FinalizarServicoUseCase(FinalizarServicoGateway gateway,
                                   NotificarOrdemServicoService notificarService,
                                   Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Buscando OS e mecânico para finalizar execução de serviço - osId={}, servicoId={}", input.osId(), input.servicoId());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.osId());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        ordemServico.finalizarExecucaoServico(input.servicoId(), mecanico.getId());

        // TODO: a baixa de peças do estoque deixou de ser feita aqui - a OS emite o
        // evento ServiceEnded e o InventoryService deduz o estoque de forma assíncrona
        // (general-flow.md passo 13-14).
        logger.logInfo("Salvando finalização de execução de serviço - osId={}, servicoId={}", input.osId(), input.servicoId());
        var saved = gateway.salvarOrdemServico(ordemServico);
        if (ordemServico.isFinalizada()) {
            logger.logInfo("Ordem de serviço finalizada - osId={}", saved.getId());
            notificarService.execute(saved);
        }
        logger.logInfo("Execução de serviço finalizada com sucesso - osId={}, servicoId={}", saved.getId(), input.servicoId());
    }

    public record Input(Long osId, Long servicoId, UserId userId) {}
}

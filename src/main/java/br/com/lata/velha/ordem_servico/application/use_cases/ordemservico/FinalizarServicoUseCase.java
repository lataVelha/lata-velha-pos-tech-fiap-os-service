package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.events.OSEndedEvent;
import br.com.lata.velha.ordem_servico.domain.events.ServiceEndedEvent;
import br.com.lata.velha.shared.application.gateways.EventPublisher;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class FinalizarServicoUseCase {

    private final FinalizarServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final EventPublisher eventPublisher;
    private final Logger logger;

    public FinalizarServicoUseCase(FinalizarServicoGateway gateway,
                                   NotificarOrdemServicoService notificarService,
                                   EventPublisher eventPublisher,
                                   Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.eventPublisher = eventPublisher;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Buscando OS e mecânico para finalizar execução de serviço - osId={}, servicoId={}", input.osId(), input.servicoId());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.osId());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        var execucao = ordemServico.getExecucaoById(input.servicoId());
        ordemServico.finalizarExecucaoServico(input.servicoId(), mecanico.getId());

        logger.logInfo("Salvando finalização de execução de serviço - osId={}, servicoId={}", input.osId(), input.servicoId());
        var saved = gateway.salvarOrdemServico(ordemServico);

        var itens = execucao.getPecasNecessarias().stream()
                .map(peca -> new ServiceEndedEvent.ItemPeca(peca.getPecaId(), peca.getQuantidade()))
                .toList();
        eventPublisher.publish(new ServiceEndedEvent(saved.getId(), input.servicoId(), itens));

        if (ordemServico.isFinalizada()) {
            logger.logInfo("Ordem de serviço finalizada - osId={}", saved.getId());
            eventPublisher.publish(new OSEndedEvent(saved.getId()));
            notificarService.execute(saved);
        }
        logger.logInfo("Execução de serviço finalizada com sucesso - osId={}, servicoId={}", saved.getId(), input.servicoId());
    }

    public record Input(Long osId, Long servicoId, UserId userId) {}
}

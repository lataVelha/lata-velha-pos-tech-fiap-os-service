package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaNecessaria;
import br.com.lata.velha.ordem_servico.domain.events.DiagnosticoFinalizadoEvent;
import br.com.lata.velha.ordem_servico.domain.events.OSEndedEvent;
import br.com.lata.velha.shared.application.gateways.EventPublisher;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class FinalizarDiagnosticoUseCase {

    private final FinalizarDiagnosticoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final EventPublisher eventPublisher;
    private final Logger logger;

    public FinalizarDiagnosticoUseCase(FinalizarDiagnosticoGateway gateway,
                                       NotificarOrdemServicoService notificarService,
                                       EventPublisher eventPublisher,
                                       Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.eventPublisher = eventPublisher;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Buscando OS para finalizar diagnóstico - osId={}", input.idOs());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.idOs());
        gateway.getFuncionarioPorUserId(input.userId()); // validates user is a funcionario
        ordemServico.finalizarDiagnostico(ordemServico.getMecanicoResponsavelId());

        logger.logInfo("Salvando diagnóstico finalizado - osId={}", input.idOs());
        var saved = gateway.salvarOrdemServico(ordemServico);

        if (!saved.getExecucaoServicos().isEmpty()) {
            eventPublisher.publish(buildDiagnosticoFinalizadoEvent(saved));
        } else {
            eventPublisher.publish(new OSEndedEvent(saved.getId()));
        }

        notificarService.execute(saved);
        logger.logInfo("Diagnóstico finalizado com sucesso - osId={}, status={}", saved.getId(), saved.getStatus());
    }

    private DiagnosticoFinalizadoEvent buildDiagnosticoFinalizadoEvent(OrdemServico ordemServico) {
        var itens = ordemServico.getExecucaoServicos().stream()
                .map(this::toItemDiagnostico)
                .toList();
        return new DiagnosticoFinalizadoEvent(ordemServico.getId(), itens);
    }

    private DiagnosticoFinalizadoEvent.ItemDiagnostico toItemDiagnostico(ExecucaoServico execucao) {
        var pecas = execucao.getPecasNecessarias().stream()
                .map(this::toItemPeca)
                .toList();
        return new DiagnosticoFinalizadoEvent.ItemDiagnostico(execucao.getId(), execucao.getServicoId(), execucao.getValorMaoDeObra(), pecas);
    }

    private DiagnosticoFinalizadoEvent.ItemPeca toItemPeca(PecaNecessaria peca) {
        return new DiagnosticoFinalizadoEvent.ItemPeca(peca.getPecaId(), peca.getQuantidade());
    }

    public record Input(Long idOs, UserId userId) {}
}

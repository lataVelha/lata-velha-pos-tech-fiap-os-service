package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class IniciarServicoUseCase {

    private final IniciarServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public IniciarServicoUseCase(IniciarServicoGateway gateway,
                                 NotificarOrdemServicoService notificarService,
                                 Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Buscando OS e mecânico para iniciar serviço - osId={}, servicoId={}", input.idOs(), input.servicoId());
        var ordemServico = gateway.getOrdemServicoComServicos(input.idOs());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        var primeiroServico = !ordemServico.isEmExecucao();
        ordemServico.iniciarExecucaoServico(input.servicoId(), mecanico.getId());

        logger.logInfo("Salvando início de execução de serviço - osId={}, servicoId={}", input.idOs(), input.servicoId());
        var saved = gateway.salvarOrdemServico(ordemServico);
        if (primeiroServico) {
            logger.logInfo("Primeiro serviço da OS iniciado, notificando proprietário - osId={}", saved.getId());
            notificarService.execute(saved);
        }
        logger.logInfo("Execução de serviço iniciada com sucesso - osId={}, servicoId={}, mecanicoId={}",
                saved.getId(), input.servicoId(), mecanico.getId());
    }

    public record Input(Long idOs, Long servicoId, UserId userId) {}
}

package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class ReprovarOrdemServicoUseCase {

    private final ReprovarOrdemServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public ReprovarOrdemServicoUseCase(ReprovarOrdemServicoGateway gateway,
                                       NotificarOrdemServicoService notificarService,
                                       Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Buscando OS para reprovação - osId={}", input.osId());
        var ordemServico = gateway.getOrdemServicoPorId(input.osId());
        var funcionario = gateway.getFuncionarioPorUserId(input.userId());

        logger.logInfo("Recusando execuções de serviço da OS - osId={}, quantidadeExecucoes={}",
                input.osId(), ordemServico.getExecucaoServicos().size());
        ordemServico.getExecucaoServicos().forEach(execucaoServico ->
                execucaoServico.recusar(funcionario.getId())
        );
        ordemServico.reprovar(funcionario.getId());

        logger.logInfo("Salvando reprovação da OS - osId={}", input.osId());
        gateway.salvarOrdemServico(ordemServico);

        notificarService.execute(ordemServico);
        logger.logInfo("Ordem de serviço reprovada com sucesso - osId={}", ordemServico.getId());
    }

    public record Input(Long osId, UserId userId) {}
}

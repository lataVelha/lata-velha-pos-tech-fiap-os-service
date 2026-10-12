package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class FinalizarDiagnosticoUseCase {

    private final FinalizarDiagnosticoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public FinalizarDiagnosticoUseCase(FinalizarDiagnosticoGateway gateway,
                                       NotificarOrdemServicoService notificarService,
                                       Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Buscando OS para finalizar diagnóstico - osId={}", input.idOs());
        var ordemServico = gateway.getOrdemServicoComServicos(input.idOs());
        gateway.getFuncionarioPorUserId(input.userId()); // validates user is a funcionario
        ordemServico.finalizarDiagnostico(ordemServico.getMecanicoResponsavelId());

        logger.logInfo("Salvando diagnóstico finalizado - osId={}", input.idOs());
        gateway.salvarOrdemServico(ordemServico);

        notificarService.execute(ordemServico);
        logger.logInfo("Diagnóstico finalizado com sucesso - osId={}, status={}", ordemServico.getId(), ordemServico.getStatus());
    }

    public record Input(Long idOs, UserId userId) {}
}

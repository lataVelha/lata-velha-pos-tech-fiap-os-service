package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class IniciarDiagnosticoUseCase {

    private final IniciarDiagnosticoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public IniciarDiagnosticoUseCase(IniciarDiagnosticoGateway gateway,
                                     NotificarOrdemServicoService notificarService,
                                     Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Buscando OS e mecânico para iniciar diagnóstico - osId={}", input.idOs());
        var ordemServico = gateway.getOrdemServicoPorId(input.idOs());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        ordemServico.iniciarDiagnostico(mecanico.getId());

        logger.logInfo("Salvando início de diagnóstico - osId={}, mecanicoId={}", input.idOs(), mecanico.getId());
        var saved = gateway.salvarOrdemServico(ordemServico);

        notificarService.execute(saved);
        logger.logInfo("Diagnóstico iniciado com sucesso - osId={}, mecanicoId={}", saved.getId(), mecanico.getId());
    }

    public record Input(Long idOs, UserId userId) {}
}

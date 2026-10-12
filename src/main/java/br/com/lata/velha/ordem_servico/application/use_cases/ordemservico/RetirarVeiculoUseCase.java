package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class RetirarVeiculoUseCase {

    private final RetirarVeiculoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public RetirarVeiculoUseCase(RetirarVeiculoGateway gateway,
                                 NotificarOrdemServicoService notificarService,
                                 Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(Long idOs, UserId userId) {
        logger.logInfo("Buscando OS para retirada do veículo - osId={}", idOs);
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(idOs);
        var funcionario = gateway.getFuncionarioPorUserId(userId);
        ordemServico.entregar(funcionario.getId());

        logger.logInfo("Salvando retirada do veículo - osId={}", idOs);
        gateway.salvarOrdemServico(ordemServico);

        notificarService.execute(ordemServico);
        logger.logInfo("Veículo retirado com sucesso - osId={}", ordemServico.getId());
    }
}

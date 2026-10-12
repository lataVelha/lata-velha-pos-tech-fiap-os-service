package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class CriarOrdemServicoUseCase {

    private final CriarOrdemServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public CriarOrdemServicoUseCase(CriarOrdemServicoGateway gateway,
                                    NotificarOrdemServicoService notificarService,
                                    Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public OrdemServicoProjection execute(Input input) {
        logger.logInfo("Validando proprietário e veículo - proprietarioId={}, veiculoId={}", input.proprietarioId(), input.veiculoId());
        var proprietario = gateway.getProprietarioAtivoPorId(input.proprietarioId());
        var veiculo = gateway.getVeiculoAtivoDoProprietario(input.veiculoId(), proprietario.getId());
        var funcionario = gateway.getFuncionarioPorUserId(input.userId());

        var ordemServico = OrdemServico.create(
                proprietario.getId(),
                veiculo.getId(),
                input.reclamacaoProprietario(),
                funcionario.getId()
        );

        logger.logInfo("Salvando nova ordem de serviço - proprietarioId={}, veiculoId={}", proprietario.getId(), veiculo.getId());
        var saved = gateway.salvarOrdemServico(ordemServico);

        notificarService.execute(saved);

        logger.logInfo("Ordem de serviço criada com sucesso - osId={}", saved.getId());
        return gateway.getOrdemServicoProjectionById(saved.getId());
    }

    public record Input(Long veiculoId, Long proprietarioId, UserId userId, String reclamacaoProprietario) {}
}

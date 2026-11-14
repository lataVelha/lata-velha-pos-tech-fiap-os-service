package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AprovarOrdemServicoUseCase {

    private final AprovarOrdemServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public AprovarOrdemServicoUseCase(AprovarOrdemServicoGateway gateway,
                                      NotificarOrdemServicoService notificarService,
                                      Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public OrdemServico execute(Input input) {
        logger.logInfo("Iniciando aprovação de ordem de serviço - osId={}, quantidadeServicos={}",
                input.idOs(), input.servicos().size());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.idOs());
        var funcionario = gateway.getFuncionarioPorUserId(input.userId());

        validateServicos(ordemServico, input.servicos());
        var statusPorId = input.getServiceStatusMap();

        ordemServico.getExecucaoServicos().forEach(execucaoServico -> {
            var novoStatus = statusPorId.get(execucaoServico.getId());
            if (novoStatus == null) novoStatus = StatusExecucaoServico.RECUSADO;

            switch (novoStatus) {
                case APROVADO -> {
                    logger.logInfo("Aprovando execução de serviço - osId={}, execucaoServicoId={}",
                            ordemServico.getId(), execucaoServico.getId());
                    execucaoServico.aprovar(funcionario.getId());
                }
                case RECUSADO -> {
                    logger.logInfo("Recusando execução de serviço - osId={}, execucaoServicoId={}",
                            ordemServico.getId(), execucaoServico.getId());
                    execucaoServico.recusar(funcionario.getId());
                }
                default -> {
                    logger.logWarn("Aprovação de OS rejeitada: status de execução não suportado - osId={}, execucaoServicoId={}, status={}",
                            ordemServico.getId(), execucaoServico.getId(), novoStatus);
                    throw new IllegalArgumentException("Status não suportado: " + novoStatus);
                }
            }
        });

        ordemServico.aprovar(funcionario.getId());
        notificarService.execute(ordemServico);

        var saved = gateway.salvarOrdemServico(ordemServico);
        logger.logInfo("Aprovação de ordem de serviço concluída com sucesso - osId={}, status={}", saved.getId(), saved.getStatus());
        return saved;
    }

    private void validateServicos(OrdemServico ordemServico, List<Input.ServicoAprovacao> servicos) {
        var registeredIds = ordemServico.getExecucaoServicos().stream()
                .map(ExecucaoServico::getId)
                .collect(Collectors.toSet());
        var idsInvalidos = servicos.stream()
                .filter(servico -> !registeredIds.contains(servico.execucaoServicoId()))
                .toList();
        if (!idsInvalidos.isEmpty()) {
            logger.logWarn("Aprovação de OS rejeitada: serviços não pertencem à OS - osId={}, execucaoServicoIds={}",
                    ordemServico.getId(), idsInvalidos);
            throw new IllegalArgumentException("Serviços não pertencem à OS " + ordemServico.getId() + ": " + idsInvalidos);
        }
    }

    public record Input(Long idOs, UserId userId, List<ServicoAprovacao> servicos) {
        public Map<Long, StatusExecucaoServico> getServiceStatusMap() {
            return servicos.stream()
                    .collect(Collectors.toMap(
                            ServicoAprovacao::execucaoServicoId,
                            ServicoAprovacao::status
                    ));
        }

        public record ServicoAprovacao(Long execucaoServicoId, StatusExecucaoServico status) {}
    }
}

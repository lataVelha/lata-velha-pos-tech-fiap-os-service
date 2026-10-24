package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.shared.application.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReceberAprovacaoOrcamentoClienteUseCase {

    private final ReceberAprovacaoOrcamentoClienteGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public ReceberAprovacaoOrcamentoClienteUseCase(ReceberAprovacaoOrcamentoClienteGateway gateway,
                                                    NotificarOrdemServicoService notificarService,
                                                    Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public OrdemServico execute(Input input) {
        logger.logInfo("Recebendo aprovação de orçamento do cliente - osId={}, quantidadeServicos={}",
                input.osId(), input.servicos().size());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.osId());
        validateServicos(ordemServico, input.servicos());

        var statusPorId = input.getServiceStatusMap();

        ordemServico.getExecucaoServicos().forEach(execucaoServico -> {
            var novoStatus = statusPorId.get(execucaoServico.getId());
            if (novoStatus == StatusExecucaoServico.APROVADO) {
                logger.logInfo("Cliente aprovou execução de serviço - osId={}, execucaoServicoId={}",
                        ordemServico.getId(), execucaoServico.getId());
                execucaoServico.aprovar(null);
            } else {
                logger.logInfo("Cliente recusou execução de serviço - osId={}, execucaoServicoId={}",
                        ordemServico.getId(), execucaoServico.getId());
                execucaoServico.recusar(null);
            }
        });

        boolean todosRecusados = ordemServico.getExecucaoServicos().stream()
                .allMatch(ExecucaoServico::isRecusado);
        if (todosRecusados) {
            logger.logInfo("Todos os serviços foram recusados, reprovando ordem de serviço - osId={}", ordemServico.getId());
            ordemServico.reprovar(null);
        } else {
            ordemServico.aprovar(null);
        }

        notificarService.execute(ordemServico);

        logger.logInfo("Salvando OS - osId={}", ordemServico.getId());
        var saved = gateway.salvarOrdemServico(ordemServico);
        logger.logInfo("Recebimento de aprovação de orçamento concluído com sucesso - osId={}, status={}", saved.getId(), saved.getStatus());
        return saved;
    }

    private void validateServicos(OrdemServico ordemServico, List<Input.ServicoAprovacao> servicos) {
        var registeredIds = ordemServico.getExecucaoServicos().stream()
                .map(ExecucaoServico::getId)
                .collect(Collectors.toSet());
        var idsInvalidos = servicos.stream()
                .filter(s -> !registeredIds.contains(s.execucaoServicoId()))
                .toList();
        if (!idsInvalidos.isEmpty()) {
            logger.logWarn("Recebimento de aprovação rejeitado: serviços não pertencem à OS - osId={}, execucaoServicoIds={}",
                    ordemServico.getId(), idsInvalidos);
            throw new IllegalArgumentException("Serviços não pertencem à OS " + ordemServico.getId() + ": " + idsInvalidos);
        }
    }

    public record Input(Long osId, List<ServicoAprovacao> servicos) {
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

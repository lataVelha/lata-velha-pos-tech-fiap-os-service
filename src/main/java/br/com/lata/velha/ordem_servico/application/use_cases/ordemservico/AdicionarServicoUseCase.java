package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaNecessaria;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.application.logging.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdicionarServicoUseCase {

    private final AdicionarServicoGateway gateway;
    private final Logger logger;

    public AdicionarServicoUseCase(AdicionarServicoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Procurando OS - osId={}, quantidadeServicosSolicitados={}", input.osId(), input.servicos().size());
        var ordemServico = gateway.getOrdemServicoPorId(input.osId());

        var execucoes = createExecucoes(input.servicos(), ordemServico.getId());

        logger.logInfo("Adicionando execuções à OS - osId={}, quantidadeExecucoes={}", ordemServico.getId(), execucoes.size());
        execucoes.forEach(ordemServico::adicionarServico);
        ordemServico = gateway.salvarOrdemServico(ordemServico);
        logger.logInfo("OS e execuções salvas com sucesso - osId={}", ordemServico.getId());

        var idsSolicitados = input.servicos().stream().map(Input.ServicoAdicionar::servicoId).collect(Collectors.toSet());
        execucoes = ordemServico.getExecucaoServicos().stream()
                .filter(execucaoServico -> idsSolicitados.stream().anyMatch(id -> execucaoServico.getServicoId().equals(id)))
                .toList();

        logger.logInfo("Adicionando peças solicitadas às execuções - osId={}, quantidadeExecucoes={}", ordemServico.getId(), execucoes.size());
        adicionarPecas(execucoes, input.servicos());
        gateway.salvarOrdemServico(ordemServico);
        logger.logInfo("OS e execuções salvas com sucesso - osId={}", ordemServico.getId());
    }

    private List<ExecucaoServico> createExecucoes(List<Input.ServicoAdicionar> servicosAdicionar, Long osId) {
        Set<Long> servicoIds = servicosAdicionar.stream().map(Input.ServicoAdicionar::servicoId).collect(Collectors.toSet());
        validateDuplicates(servicoIds, servicosAdicionar);
        var servicos = gateway.getServicosAtivosPorIds(servicoIds);
        validateIds(servicoIds, servicos);
        var servicoAdicionarMap = servicosAdicionar.stream().collect(Collectors.toMap(
                Input.ServicoAdicionar::servicoId,
                servico -> servico
        ));
        return servicoAdicionarMap.entrySet().stream()
                .map(entry -> {
                    var value = entry.getValue();
                    return ExecucaoServico.create(entry.getKey(), osId, value.valorMaoDeObra());
                })
                .toList();
    }

    private void adicionarPecas(List<ExecucaoServico> execucoes, List<Input.ServicoAdicionar> servicos) {
        var pecasMap = servicos.stream()
                .collect(Collectors.toMap(
                        Input.ServicoAdicionar::servicoId,
                        Input.ServicoAdicionar::pecas
                ));
        execucoes.forEach(execucao -> {
            var pecas = pecasMap.get(execucao.getServicoId());
            pecas.stream()
                    .map(peca -> PecaNecessaria.create(peca.pecaId(), execucao.getId(), peca.quantidade()))
                    .forEach(execucao::adicionarPecaNecessaria);
        });
    }

    private void validateDuplicates(Set<Long> servicoIds, List<Input.ServicoAdicionar> servicosAdicionar) {
        if (servicoIds.size() != servicosAdicionar.size()) {
            logger.logWarn("Adição de serviço rejeitada: serviços duplicados no input - quantidadeInformada={}, quantidadeUnica={}",
                    servicosAdicionar.size(), servicoIds.size());
            throw new IllegalArgumentException("Não é possível inserir serviços duplicados!");
        }
    }

    private void validateIds(Set<Long> servicoIds, List<Servico> servicos) {
        if (servicos.isEmpty()) {
            logger.logWarn("Adição de serviço rejeitada: nenhum serviço solicitado existe - servicoIds={}", servicoIds);
            throw new IllegalArgumentException("Nenhum serviço solicitado existe");
        }
        var invalidIds = servicoIds.stream()
                .filter(id -> servicos.stream().noneMatch(s -> s.getId().equals(id)))
                .collect(Collectors.toSet());
        if (!invalidIds.isEmpty()) {
            logger.logWarn("Adição de serviço rejeitada: serviços com Ids inexistentes - invalidIds={}", invalidIds);
            throw new IllegalArgumentException("Servicos com Ids: " + invalidIds + " não existem!");
        }
    }

    public record Input(Long osId, List<ServicoAdicionar> servicos) {
        public record ServicoAdicionar(Long servicoId, List<PecaNecessaria> pecas, BigDecimal valorMaoDeObra) {}
        public record PecaNecessaria(Long pecaId, Integer quantidade) {}
    }
}

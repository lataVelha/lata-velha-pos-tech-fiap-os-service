package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.OrdemServicoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.HistoricoEstadoOsEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.infrastructure.observability.HistoricoMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {

    private final OrdemServicoJpaRepository jpaRepository;
    private final ExecucaoServicoJpaRepository execucaoServicoJpaRepository;
    private final HistoricoEstadoOsJpaRepository historicoEstadoOsJpaRepository;
    private final HistoricoMetrics historicoMetrics;
    private final Logger logger;

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        var entity = OrdemServicoEntity.fromDomain(ordemServico);
        var saved = jpaRepository.save(entity);
        persistirHistorico(ordemServico, saved.getId());
        return saved.toDomain();
    }

    private void persistirHistorico(OrdemServico ordemServico, Long osId) {
        var historicoDomain = ordemServico.getHistoricoEstados();
        logger.logInfo("Persistindo histórico de estados - osId={}, totalRegistros={}", osId, historicoDomain.size());
        
        var historico = historicoDomain.stream()
                .map(entrada -> {
                    var entradaEntity = HistoricoEstadoOsEntity.fromDomain(entrada);
                    if (entradaEntity.getOsId() == null) {
                        entradaEntity.setOsId(osId);
                    }
                    if (entrada.getDataFim() != null) {
                        long segundos = Duration.between(entrada.getDataInicio(), entrada.getDataFim()).getSeconds();
                        logger.logInfo("Emitindo métrica de duração - osId={}, estado={}, segundos={}", osId, entrada.getEstadoOs(), segundos);
                        historicoMetrics.registrar(entrada.getEstadoOs(), segundos);
                    } else {
                        logger.logInfo("Estado aberto (sem métrica) - osId={}, estado={}", osId, entrada.getEstadoOs());
                    }
                    return entradaEntity;
                })
                .toList();
        if (!historico.isEmpty()) {
            logger.logInfo("Salvando registros de histórico - osId={}, quantidade={}", osId, historico.size());
            historicoEstadoOsJpaRepository.saveAll(historico);
        }
    }

    @Override
    public OrdemServico getById(Long id) {
        return jpaRepository.findById(id)
                .map(OrdemServicoEntity::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Ordem de serviço não encontrada - osId={}", id);
                    return OrdemServicoNotFoundException.fromId(id);
                });
    }

    @Override
    public OrdemServico getByIdWithExecucoesAndPecas(Long id) {
        var entity = jpaRepository.findByIdWithHistorico(id)
                .orElseThrow(() -> {
                    logger.logWarn("Ordem de serviço não encontrada - osId={}", id);
                    return OrdemServicoNotFoundException.fromId(id);
                });
        var execucoes = execucaoServicoJpaRepository.findWithPecasByOsId(id);
        return entity.toDomain(execucoes);
    }

    @Override
    public PaginatedResult<OrdemServicoProjection> findByAllOrdemSevico(Long id, String status, Long proprietarioId, Long mecanicoId, int page, int size) {
        Page<OrdemServicoProjection> result = jpaRepository.findByAllOrdemSevico(
                id,
                status,
                proprietarioId,
                mecanicoId,
                PageRequest.of(page, size)
        );

        List<OrdemServicoProjection> content = result.getContent();
        return new PaginatedResult<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public PaginatedResult<OrdemServicoProjection> findOrderedByStatusPriority(int page, int size) {
        Page<OrdemServicoProjection> result = jpaRepository.findOrderedByStatusPriority(
                PageRequest.of(page, size)
        );

        List<OrdemServicoProjection> content = result.getContent();
        return new PaginatedResult<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
}

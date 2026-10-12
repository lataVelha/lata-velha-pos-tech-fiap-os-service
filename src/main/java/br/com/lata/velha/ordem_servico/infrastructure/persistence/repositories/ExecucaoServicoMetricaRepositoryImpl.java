package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoMetricaRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExecucaoServicoMetricaRepositoryImpl implements ExecucaoServicoMetricaRepository {

    private final ExecucaoServicoJpaRepository execucaoServicoJpaRepository;
    private final Logger logger;

    @Override
    public List<TempoMedioExecucaoPorServico> buscarTempoMedioExecucaoServicosFinalizados(LocalDateTime dataInicio, LocalDateTime dataFim) {
        logger.logDebug("Consultando tempo médio de execução no banco - dataInicio={}, dataFim={}", dataInicio, dataFim);
        return execucaoServicoJpaRepository.buscarTempoMedioExecucaoServicosFinalizados(dataInicio, dataFim)
                .stream()
                .map(item -> new TempoMedioExecucaoPorServico(
                        item.getServicoId(),
                        item.getServicoNome(),
                        item.getTempoMedioMinutos()
                ))
                .toList();
    }
}

package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;

import java.time.LocalDateTime;
import java.util.List;

public interface ExecucaoServicoMetricaRepository {

    List<TempoMedioExecucaoPorServico> buscarTempoMedioExecucaoServicosFinalizados(LocalDateTime dataInicio, LocalDateTime dataFim);
}

package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;

import java.time.LocalDateTime;
import java.util.List;

public interface BuscarTempoMedioExecucaoGateway {
    List<TempoMedioExecucaoPorServico> buscarTempoMedioExecucao(LocalDateTime dataInicio, LocalDateTime dataFim);
}

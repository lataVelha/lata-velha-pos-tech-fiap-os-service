package br.com.lata.velha.ordem_servico.application.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;

import java.time.LocalDate;
import java.util.List;

public interface BuscarTempoMedioExecucaoPresenter {
    TempoMedioExecucaoResponse present(List<TempoMedioExecucaoPorServico> itens, LocalDate dataInicio, LocalDate dataFim);
}

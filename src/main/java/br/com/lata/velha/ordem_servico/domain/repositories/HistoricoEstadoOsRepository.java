package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.view.TempoMedioPorEstado;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoEstadoOsRepository {

    List<TempoMedioPorEstado> buscarTempoMedioPorEstado(LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora);
}

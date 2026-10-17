package br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos;

import br.com.lata.velha.ordem_servico.domain.view.TempoMedioPorEstado;

import java.time.LocalDateTime;
import java.util.List;

public interface BuscarHistoricoEstadoOsGateway {

    List<TempoMedioPorEstado> buscarTempoMedioPorEstado(LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora);
}

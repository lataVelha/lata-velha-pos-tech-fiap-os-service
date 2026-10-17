package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos.BuscarHistoricoEstadoOsGateway;
import br.com.lata.velha.ordem_servico.domain.repositories.HistoricoEstadoOsRepository;
import br.com.lata.velha.ordem_servico.domain.view.TempoMedioPorEstado;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoricoEstadoOsGatewayImpl implements BuscarHistoricoEstadoOsGateway {

    private final HistoricoEstadoOsRepository historicoEstadoOsRepository;
    private final Logger logger;

    @Override
    public List<TempoMedioPorEstado> buscarTempoMedioPorEstado(LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora) {
        logger.logInfo("Buscando tempo médio por estado da OS - inicio={}, fim={}", inicio, fim);
        return historicoEstadoOsRepository.buscarTempoMedioPorEstado(inicio, fim, agora);
    }
}

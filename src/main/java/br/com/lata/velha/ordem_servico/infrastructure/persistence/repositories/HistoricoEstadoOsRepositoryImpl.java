package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.repositories.HistoricoEstadoOsRepository;
import br.com.lata.velha.ordem_servico.domain.view.TempoMedioPorEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class HistoricoEstadoOsRepositoryImpl implements HistoricoEstadoOsRepository {

    private final HistoricoEstadoOsJpaRepository historicoEstadoOsJpaRepository;

    @Override
    public List<TempoMedioPorEstado> buscarTempoMedioPorEstado(LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora) {
        var registros = historicoEstadoOsJpaRepository.buscarNoIntervalo(inicio, fim);

        Map<String, List<Long>> duracoesPorEstado = new LinkedHashMap<>();
        for (var registro : registros) {
            var fimIntervalo = registro.getDataFim() != null ? registro.getDataFim() : agora;
            long segundos = Duration.between(registro.getDataInicio(), fimIntervalo).getSeconds();
            if (segundos < 0) segundos = 0;
            duracoesPorEstado.computeIfAbsent(registro.getEstadoOs(), k -> new ArrayList<>()).add(segundos);
        }

        return duracoesPorEstado.entrySet().stream()
                .map(entry -> new TempoMedioPorEstado(
                        entry.getKey(),
                        entry.getValue().stream().mapToLong(Long::longValue).average().orElse(0.0)))
                .sorted(Comparator.comparing(TempoMedioPorEstado::estadoOs))
                .toList();
    }
}

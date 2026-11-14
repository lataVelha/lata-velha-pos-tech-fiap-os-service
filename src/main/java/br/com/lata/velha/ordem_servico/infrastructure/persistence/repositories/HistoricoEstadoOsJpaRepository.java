package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.HistoricoEstadoOsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoEstadoOsJpaRepository extends JpaRepository<HistoricoEstadoOsEntity, Long> {

    // CAST explicito: com :inicio/:fim null o Postgres não consegue inferir o
    // tipo do parâmetro em "? is null" e rebenta com SQLState 42P18
    // ("could not determine data type of parameter $1") → 500.
    @Query("""
            SELECT h FROM HistoricoEstadoOsEntity h
            WHERE (CAST(:inicio AS timestamp) IS NULL OR h.dataInicio >= :inicio)
              AND (CAST(:fim AS timestamp) IS NULL OR h.dataInicio <= :fim)
            """)
    List<HistoricoEstadoOsEntity> buscarNoIntervalo(@Param("inicio") LocalDateTime inicio,
                                                    @Param("fim") LocalDateTime fim);
}

package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.HistoricoEstadoOsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoEstadoOsJpaRepository extends JpaRepository<HistoricoEstadoOsEntity, Long> {

    @Query("""
            SELECT h FROM HistoricoEstadoOsEntity h
            WHERE (:inicio IS NULL OR h.dataInicio >= :inicio)
              AND (:fim IS NULL OR h.dataInicio <= :fim)
            """)
    List<HistoricoEstadoOsEntity> buscarNoIntervalo(@Param("inicio") LocalDateTime inicio,
                                                    @Param("fim") LocalDateTime fim);
}

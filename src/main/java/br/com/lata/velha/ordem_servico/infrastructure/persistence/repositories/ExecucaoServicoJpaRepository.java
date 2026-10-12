package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ExecucaoServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.repositories.projection.TempoMedioExecucaoPorServicoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ExecucaoServicoJpaRepository extends JpaRepository<ExecucaoServicoEntity, Long> {

    @Query(value = """
            SELECT
              es.servico_id AS servicoId,
              s.nome AS servicoNome,
              AVG(EXTRACT(EPOCH FROM (es.terminado_em - es.iniciado_em)) / 60.0) AS tempoMedioMinutos
            FROM execucao_servico es
            JOIN servico s ON s.id = es.servico_id
            WHERE es.status_servico = 'FINALIZADO'
              AND es.iniciado_em IS NOT NULL
              AND es.terminado_em IS NOT NULL
              AND (CAST(:dataInicio AS timestamp) IS NULL OR es.terminado_em >= CAST(:dataInicio AS timestamp))
              AND (CAST(:dataFim AS timestamp) IS NULL OR es.terminado_em <= CAST(:dataFim AS timestamp))
            GROUP BY es.servico_id, s.nome
            ORDER BY es.servico_id
            """, nativeQuery = true)
    List<TempoMedioExecucaoPorServicoProjection> buscarTempoMedioExecucaoServicosFinalizados(@Param("dataInicio") LocalDateTime dataInicio,
                                                                                              @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT e FROM ExecucaoServicoEntity e JOIN FETCH e.pecasNecessarias p WHERE e.id in :ids")
    Set<ExecucaoServicoEntity> getAllByIdWithPeca(Set<Long> ids);

    @Query("SELECT DISTINCT e FROM ExecucaoServicoEntity e LEFT JOIN FETCH e.pecasNecessarias WHERE e.ordemServicoId = :osId")
    List<ExecucaoServicoEntity> findWithPecasByOsId(@Param("osId") Long osId);
}
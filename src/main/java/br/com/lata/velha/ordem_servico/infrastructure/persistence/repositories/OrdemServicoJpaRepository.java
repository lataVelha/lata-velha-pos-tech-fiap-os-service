package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoEntity, Long> {

    @Query(value = """
            SELECT
                   os.id AS id,
                   os.atendente_inicio_id AS "atendenteInicioId",
                   atendente.nome AS "atendenteNome",
                   os.veiculo_id AS "veiculoId",
                   CONCAT(v.marca, ' ', v.modelo, ' - ', v.ano)
                       AS "veiculoDescricao",
                   os.proprietario_id AS "proprietarioId",
                   p.nome AS "proprietarioNome",
                   os.mecanico_responsavel_id AS "mecanicoFinalId",
                   mecanico.nome AS "mecanicoNome",
                   os.status AS status,
                   os.reclamacao_proprietario AS "reclamacaoProprietario",
                   os.iniciado_em AS "iniciadoEm",
                   os.finalizado_em AS "finalizadoEm",
                   os.entregue_em AS "entregueEm",
                   os.atualizado_em AS "atualizadoEm",
                   COALESCE(
                       JSON_AGG(
                           DISTINCT JSONB_BUILD_OBJECT(
                               'id', so.id,
                               'servicoId', srv.id,
                               'servicoNome', srv.nome,
                               'status', so.status_servico,
                               'mecanico', JSONB_BUILD_OBJECT(
                                   'id', so.mecanico_responsavel_id,
                                   'nome', mec_servico.nome
                               ),
                               'valorMaoDeObra', so.valor_mao_de_obra,
                               'iniciadoEm', so.iniciado_em,
                               'terminadoEm', so.terminado_em,
                               'atualizadoEm', so.atualizado_em,
                               'pecas', (
                                   SELECT COALESCE(
                                       JSON_AGG(
                                           JSONB_BUILD_OBJECT(
                                               'alocacaoId', sp.id,
                                               'id', peca.id,
                                               'nome', peca.nome,
                                               'valor', peca.valor,
                                               'quantidadeSolicitada', sp.qtd_solicitada,
                                               'quantidadeReservada', sp.qtd_reservada,
                                               'quantidadeEncomendada', sp.qtd_encomendada,
                                               'status', sp.status,
                                               'atualizado', sp.atualizado_em
                                           )
                                           ORDER BY sp.atualizado_em ASC
                                       ),
                                       '[]'
                                   )
                                   FROM peca_alocada sp
                                   JOIN peca peca ON peca.id = sp.peca_id
                                   WHERE sp.execucao_servico_id = so.id
                               )
                           )
                       ) FILTER (WHERE so.id IS NOT NULL),
                       '[]'
                   ) AS servicos

               FROM ordem_servico os
               LEFT JOIN funcionario atendente
                   ON atendente.id = os.atendente_inicio_id
               LEFT JOIN funcionario mecanico
                   ON mecanico.id = os.mecanico_responsavel_id
               LEFT JOIN veiculo v
                   ON v.id = os.veiculo_id
               LEFT JOIN proprietario p
                   ON p.id = os.proprietario_id
               LEFT JOIN execucao_servico so
                    ON so.os_id = os.id
               LEFT JOIN servico srv
                   ON srv.id = so.servico_id
               LEFT JOIN funcionario mec_servico
                   ON mec_servico.id = so.mecanico_responsavel_id
               WHERE (:id IS NULL OR os.id = :id)
                 AND (:status IS NULL OR os.status = :status)
                 AND (:proprietarioId IS NULL OR os.proprietario_id = :proprietarioId)
                 AND (:mecanicoId IS NULL OR os.mecanico_responsavel_id = :mecanicoId)
               GROUP BY
                   os.id,
                   os.atendente_inicio_id,
                   atendente.nome,
                   os.veiculo_id,
                   v.marca,
                   v.modelo,
                   v.ano,
                   os.proprietario_id,
                   p.nome,
                   os.mecanico_responsavel_id,
                   mecanico.nome,
                   os.status,
                   os.reclamacao_proprietario,
                   os.iniciado_em,
                   os.finalizado_em,
                   os.entregue_em,
                   os.atualizado_em
                ORDER BY
                    CASE os.status
                        WHEN 'EM_EXECUCAO' THEN 1
                        WHEN 'AGUARDANDO_APROVACAO' THEN 2
                        WHEN 'EM_DIAGNOSTICO' THEN 3
                        WHEN 'RECEBIDA' THEN 4
                        ELSE 5
                    END,
                    os.iniciado_em ASC NULLS LAST
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT os.id)
                    FROM ordem_servico os
                    WHERE (:id IS NULL OR os.id = :id)
                      AND (:status IS NULL OR os.status = :status)
                      AND (:proprietarioId IS NULL OR os.proprietario_id = :proprietarioId)
                      AND (:mecanicoId IS NULL OR os.mecanico_responsavel_id = :mecanicoId)
                    """,
            nativeQuery = true)
    Page<OrdemServicoProjection> findByAllOrdemSevico(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("proprietarioId") Long proprietarioId,
            @Param("mecanicoId") Long mecanicoId,
            Pageable pageable
    );

    @Query(value = """
        SELECT
               os.id AS id,
               os.atendente_inicio_id AS "atendenteInicioId",
               atendente.nome AS "atendenteNome",
               os.veiculo_id AS "veiculoId",
               CONCAT(v.marca, ' ', v.modelo, ' - ', v.ano)
                   AS "veiculoDescricao",
               os.proprietario_id AS "proprietarioId",
               p.nome AS "proprietarioNome",
               os.mecanico_responsavel_id AS "mecanicoFinalId",
               mecanico.nome AS "mecanicoNome",
               os.status AS status,
               os.reclamacao_proprietario AS "reclamacaoProprietario",
               os.iniciado_em AS "iniciadoEm",
               os.finalizado_em AS "finalizadoEm",
               os.entregue_em AS "entregueEm",
               os.atualizado_em AS "atualizadoEm",
               COALESCE(
                   JSON_AGG(
                       DISTINCT JSONB_BUILD_OBJECT(
                           'id', so.id,
                           'servicoId', srv.id,
                           'servicoNome', srv.nome,
                           'status', so.status_servico,
                           'mecanico', JSONB_BUILD_OBJECT(
                               'id', so.mecanico_responsavel_id,
                               'nome', mec_servico.nome
                           ),
                           'valorMaoDeObra', so.valor_mao_de_obra,
                           'iniciadoEm', so.iniciado_em,
                           'terminadoEm', so.terminado_em,
                           'atualizadoEm', so.atualizado_em,
                           'pecas', (
                               SELECT COALESCE(
                                   JSON_AGG(
                                       JSONB_BUILD_OBJECT(
                                           'alocacaoId', sp.id,
                                           'id', peca.id,
                                           'nome', peca.nome,
                                           'valor', peca.valor,
                                           'quantidadeSolicitada', sp.qtd_solicitada,
                                           'quantidadeReservada', sp.qtd_reservada,
                                           'quantidadeEncomendada', sp.qtd_encomendada,
                                           'status', sp.status,
                                           'atualizado', sp.atualizado_em
                                       )
                                       ORDER BY sp.atualizado_em ASC
                                   ),
                                   '[]'
                               )
                               FROM peca_alocada sp
                               JOIN peca peca
                                   ON peca.id = sp.peca_id
                               WHERE sp.execucao_servico_id = so.id
                           )
                       )
                   ) FILTER (WHERE so.id IS NOT NULL),
                   '[]'
               ) AS servicos
        FROM ordem_servico os
        LEFT JOIN funcionario atendente
            ON atendente.id = os.atendente_inicio_id
        LEFT JOIN funcionario mecanico
            ON mecanico.id = os.mecanico_responsavel_id
        LEFT JOIN veiculo v
            ON v.id = os.veiculo_id
        LEFT JOIN proprietario p
            ON p.id = os.proprietario_id
        LEFT JOIN execucao_servico so
             ON so.os_id = os.id
        LEFT JOIN servico srv
            ON srv.id = so.servico_id
        LEFT JOIN funcionario mec_servico
            ON mec_servico.id = so.mecanico_responsavel_id
        WHERE os.status NOT IN ('FINALIZADA', 'ENTREGUE')
        GROUP BY
            os.id,
            os.atendente_inicio_id,
            atendente.nome,
            os.veiculo_id,
            v.marca,
            v.modelo,
            v.ano,
            os.proprietario_id,
            p.nome,
            os.mecanico_responsavel_id,
            mecanico.nome,
            os.status,
            os.reclamacao_proprietario,
            os.iniciado_em,
            os.finalizado_em,
            os.entregue_em,
            os.atualizado_em
        ORDER BY
            CASE os.status
                WHEN 'EM_EXECUCAO' THEN 1
                WHEN 'AGUARDANDO_APROVACAO' THEN 2
                WHEN 'EM_DIAGNOSTICO' THEN 3
                WHEN 'RECEBIDA' THEN 4
                ELSE 5
            END,
            os.iniciado_em ASC NULLS LAST
        """,
            countQuery = """
                SELECT COUNT(DISTINCT os.id)
                FROM ordem_servico os
                WHERE os.status NOT IN ('FINALIZADA', 'ENTREGUE')
                """,
            nativeQuery = true)
    Page<OrdemServicoProjection> findOrderedByStatusPriority(Pageable pageable);
}
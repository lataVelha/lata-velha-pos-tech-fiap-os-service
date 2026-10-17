package br.com.lata.velha.ordem_servico.domain.view;

import java.time.LocalDateTime;

public interface OrdemServicoProjection {

    Long getId();

    Long getAtendenteInicioId();
    String getAtendenteNome();

    Long getVeiculoId();
    String getVeiculoDescricao();

    Long getProprietarioId();
    String getProprietarioNome();

    Long getMecanicoFinalId();
    String getMecanicoNome();

    String getStatus();
    String getReclamacaoProprietario();

    LocalDateTime getIniciadoEm();
    LocalDateTime getFinalizadoEm();
    LocalDateTime getEntregueEm();
    LocalDateTime getAtualizadoEm();

    String getServicos();
}

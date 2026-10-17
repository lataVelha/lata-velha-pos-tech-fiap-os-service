package br.com.lata.velha.ordem_servico.infrastructure.repositories.projection;

public interface TempoMedioExecucaoPorServicoProjection {

    Long getServicoId();

    String getServicoNome();

    Double getTempoMedioMinutos();
}

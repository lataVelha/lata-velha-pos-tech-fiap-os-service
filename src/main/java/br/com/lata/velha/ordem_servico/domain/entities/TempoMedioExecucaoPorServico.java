package br.com.lata.velha.ordem_servico.domain.entities;

public record TempoMedioExecucaoPorServico(
        Long servicoId,
        String servicoNome,
        Double tempoMedioMinutos
) {
}

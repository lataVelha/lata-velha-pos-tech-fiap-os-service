package br.com.lata.velha.ordem_servico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "TempoMedioExecucaoServicoItemResponse", description = "Item da métrica de tempo médio de execução por serviço")
public record TempoMedioExecucaoServicoItemResponse(
        @Schema(description = "Id do serviço", example = "5")
        Long servicoId,

        @Schema(description = "Nome do serviço", example = "Troca freio")
        String servicoNome,

        @Schema(description = "Tempo médio de execução em minutos", example = "37.25")
        BigDecimal tempoMedioMinutos
) {
}

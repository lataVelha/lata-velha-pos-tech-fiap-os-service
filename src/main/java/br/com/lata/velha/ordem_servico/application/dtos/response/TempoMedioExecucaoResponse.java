package br.com.lata.velha.ordem_servico.application.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "TempoMedioExecucaoResponse", description = "Resposta da métrica de tempo médio de execução por serviço")
public record TempoMedioExecucaoResponse(
        @Schema(description = "Lista de serviços com o tempo médio de execução", requiredMode = Schema.RequiredMode.REQUIRED)
        List<TempoMedioExecucaoServicoItemResponse> servicos,

        @Schema(description = "Data inicial considerada na consulta", example = "01/01/2026")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataInicio,

        @Schema(description = "Data final considerada na consulta", example = "31/01/2026")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataFim,

        @Schema(description = "Timezone utilizada para cálculo da janela de datas", example = "America/Sao_Paulo")
        String timezone
) {
}

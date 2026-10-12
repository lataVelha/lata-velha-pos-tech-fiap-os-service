package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "ExecucaoServicoResponse", description = "Serviço vinculado a uma ordem de serviço")
public record ExecucaoServicoResponse(

        @Schema(description = "ID da execução de serviço", example = "189")
        Long id,

        @Schema(description = "ID do serviço cadastrado", example = "134")
        Long servicoId,

        @Schema(description = "Status atual do serviço", example = "APROVADO")
        StatusExecucaoServico status,

        @Schema(description = "Mecânico responsável pelo serviço")
        FuncionarioResumoResponse mecanico,

        @Schema(description = "Valor da mão de obra", example = "150.00")
        BigDecimal valorMaoDeObra,

        @Schema(description = "Data de início da execução", example = "19/04/2026 10:00:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime iniciadoEm,

        @Schema(description = "Data de conclusão da execução", example = "19/04/2026 15:30:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime terminadoEm,

        @Schema(description = "Data da última atualização", example = "19/04/2026 15:30:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime atualizadoEm,

        @Schema(description = "Peças necessárias para este serviço")
        List<PecaNecessariaResponse> pecas

) {
    public static ExecucaoServicoResponse from(ExecucaoServico domain) {
        return from(domain, Map.of());
    }

    public static ExecucaoServicoResponse from(
            ExecucaoServico domain,
            Map<Long, String> mecanicoNomes
    ) {

        List<PecaNecessariaResponse> pecas = domain.getPecasNecessarias() != null
                ? domain.getPecasNecessarias().stream()
                .map(PecaNecessariaResponse::from)
                .toList()
                : List.of();

        Long mecId = domain.getMecanicoResponsavelId();
        FuncionarioResumoResponse mecanico = new FuncionarioResumoResponse(
                mecId,
                mecId != null ? mecanicoNomes.get(mecId) : null
        );

        return new ExecucaoServicoResponse(
                domain.getId(),
                domain.getServicoId(),
                domain.getStatus(),
                mecanico,
                domain.getValorMaoDeObra(),
                domain.getIniciadoEm(),
                domain.getTerminadoEm(),
                domain.getAtualizadoEm(),
                pecas
        );
    }
}

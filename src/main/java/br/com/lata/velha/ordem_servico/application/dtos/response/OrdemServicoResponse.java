package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(name = "OrdemServicoResponse", description = "Resposta da Ordem de Serviço")
public record OrdemServicoResponse(

        @Schema(description = "Id da ordem de serviço", example = "100")
        Long id,

        @Schema(description = "Status da ordem", example = "EM_DIAGNOSTICO")
        String status,

        @Schema(description = "Reclamação do proprietário", example = "Barulho ao frear")
        String reclamacaoProprietario,

        @Schema(description = "Atendente responsável pela abertura")
        FuncionarioResumoResponse atendente,

        @Schema(description = "Mecânico responsável pela OS")
        FuncionarioResumoResponse mecanico,

        @Schema(description = "Proprietário do veículo")
        ProprietarioResumoResponse proprietario,

        @Schema(description = "Veículo da ordem de serviço")
        VeiculoResumoResponse veiculo,

        @Schema(description = "Data de início", example = "30/03/2026 11:00:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime iniciadoEm,

        @Schema(description = "Data de finalização", example = "30/03/2026 15:30:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime finalizadoEm,

        @Schema(description = "Data de entrega", example = "30/03/2026 16:00:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime entregueEm,

        @Schema(description = "Data da última atualização", example = "30/03/2026 15:30:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime atualizadoEm,

        @Schema(description = "Lista de serviços da OS")
        List<ExecucaoServicoResponse> servicos

) {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .configure(MapperFeature.USE_ANNOTATIONS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static OrdemServicoResponse from(
            OrdemServico domain,
            String atendenteNome,
            String mecanicoNome,
            String proprietarioNome,
            String veiculoDescricao
    ) {
        return from(domain, atendenteNome, mecanicoNome, proprietarioNome, veiculoDescricao, Map.of());
    }

    public static OrdemServicoResponse from(
            OrdemServico domain,
            String atendenteNome,
            String mecanicoNome,
            String proprietarioNome,
            String veiculoDescricao,
            Map<Long, String> mecanicoNomes
    ) {
        List<ExecucaoServicoResponse> servicos = domain.getExecucaoServicos() != null
                ? domain.getExecucaoServicos().stream()
                        .map(e -> ExecucaoServicoResponse.from(e, mecanicoNomes))
                        .toList()
                : List.of();

        return new OrdemServicoResponse(
                domain.getId(),
                domain.getStatus() != null ? domain.getStatus().name() : null,
                domain.getReclamacaoProprietario(),
                new FuncionarioResumoResponse(domain.getAtendenteInicioId(), atendenteNome),
                new FuncionarioResumoResponse(domain.getMecanicoResponsavelId(), mecanicoNome),
                new ProprietarioResumoResponse(domain.getProprietarioId(), proprietarioNome),
                new VeiculoResumoResponse(domain.getVeiculoId(), veiculoDescricao),
                domain.getIniciadoEm(),
                domain.getFinalizadoEm(),
                domain.getEntregueEm(),
                domain.getAtualizadoEm(),
                servicos
        );
    }

    public static OrdemServicoResponse from(OrdemServicoProjection p) {
        List<ExecucaoServicoResponse> servicos = List.of();
        try {
            if (p.getServicos() != null && !p.getServicos().isBlank()) {
                servicos = MAPPER.readValue(
                        p.getServicos(),
                        new TypeReference<List<ExecucaoServicoResponse>>() {}
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter serviços JSON", e);
        }

        return new OrdemServicoResponse(
                p.getId(),
                p.getStatus(),
                p.getReclamacaoProprietario(),
                new FuncionarioResumoResponse(p.getAtendenteInicioId(), p.getAtendenteNome()),
                new FuncionarioResumoResponse(p.getMecanicoFinalId(), p.getMecanicoNome()),
                new ProprietarioResumoResponse(p.getProprietarioId(), p.getProprietarioNome()),
                new VeiculoResumoResponse(p.getVeiculoId(), p.getVeiculoDescricao()),
                p.getIniciadoEm(),
                p.getFinalizadoEm(),
                p.getEntregueEm(),
                p.getAtualizadoEm(),
                servicos
        );
    }
}

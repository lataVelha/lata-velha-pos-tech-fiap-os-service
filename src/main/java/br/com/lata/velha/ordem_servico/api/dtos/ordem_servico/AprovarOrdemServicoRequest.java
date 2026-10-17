package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AprovarOrdemServicoUseCase;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "AprovarOrdemServicoRequest", description = "Dados para aprovação da Ordem de Serviço")
public record AprovarOrdemServicoRequest(
        @NotEmpty(message = "Lista de serviços não pode ser vazia")
        @Schema(description = "Serviços a aprovar ou reprovar")
        List<@Valid Servico> servicos
) {
        public AprovarOrdemServicoUseCase.Input toInput(UserId userId, Long idOs) {
                List<AprovarOrdemServicoUseCase.Input.ServicoAprovacao> servicosInput = servicos.stream()
                        .map(s -> new AprovarOrdemServicoUseCase.Input.ServicoAprovacao(s.execucaoServicoId(), StatusExecucaoServico.tryParseOrRecusado(s.status())))
                        .toList();
                return new AprovarOrdemServicoUseCase.Input(idOs, userId, servicosInput);
        }

        public record Servico(
                @NotNull(message = "ID do serviço OS é obrigatório")
                @Schema(description = "Id da execução de serviço", example = "10")
                Long execucaoServicoId,

                @NotNull(message = "Status do serviço é obrigatório")
                @Schema(description = "Novo status: APROVADO ou RECUSADO", example = "APROVADO")
                String status
        ) {}
}

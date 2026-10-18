package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AdicionarServicoUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * TODO: A OS só precisa saber qual peça e quanto dela é necessária para o serviço - preço é
 * assunto do InventoryService, consultado pelo BillingService ao montar o orçamento
 * (evento DiagnosticoFinalizado, ver billing-service-todo.md).
 */
public record PecaNecessariaRequest(
        @NotNull(message = "Peça Id é obrigatório!")
        @Schema(example = "1", description = "id da peça")
        Long pecaId,

        @NotNull(message = "Quantidade é obrigatório!")
        @Schema(example = "1", description = "quantidade de peças necessárias")
        Integer quantidade
) {
        public AdicionarServicoUseCase.Input.PecaNecessaria toUseCaseInput() {
                return new AdicionarServicoUseCase.Input.PecaNecessaria(pecaId, quantidade);
        }
}

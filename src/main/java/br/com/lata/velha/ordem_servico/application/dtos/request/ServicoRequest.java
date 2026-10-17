package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AdicionarServicoUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ServicoRequest(

        @NotNull(message = "Serviço Id é obrigatório!")
        @Schema(description = "ID do serviço a executar", example = "1")
        Long servicoId,

        @Schema(description = "Peças necessárias para o serviço")
        List<PecaNecessariaRequest> pecas,

        @NotNull(message = "Valor de Mão de Obra é obrigatório!")
        @Schema(description = "Valor cobrado pela mão de obra", example = "150.00")
        BigDecimal valorMaoDeObra

) {
    public AdicionarServicoUseCase.Input.ServicoAdicionar toUseCaseInput() {
        var pecas = this.pecas.stream().map(PecaNecessariaRequest::toUseCaseInput).toList();
        return new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId, pecas, valorMaoDeObra);
    }
}

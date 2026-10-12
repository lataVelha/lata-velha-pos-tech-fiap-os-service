package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AdicionarServicoUseCase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(name = "AddServicoRequest", description = "Dados para adição de serviços a uma ordem de serviço")
public record AddServicoRequest(
        @NotEmpty(message = "Lista servicoOSId é obrigatória")
        @Schema(description = "Lista de serviços a adicionar")
        List<@Valid ServicoRequest> servicoRequests

) {
    public AdicionarServicoUseCase.Input toUseCaseInput(Long idOs) {
        var servicos = servicoRequests.stream()
                .map(ServicoRequest::toUseCaseInput)
                .toList();
        return new AdicionarServicoUseCase.Input(idOs, servicos);
    }
}

package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.ServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoSemProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AdicionarServicoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.CriarOrdemServicoCompletaUseCase;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(name = "OrdemServicoCompletaRequest",
        description = "Dados para criação de uma Ordem de Serviço já com o cadastro do proprietário, do veículo e, opcionalmente, dos serviços/peças")
public record CriarOrdemServicoCompletaRequest(
        @NotNull(message = "Dados do proprietário são obrigatórios")
        @Valid
        @Schema(description = "Dados do proprietário a ser cadastrado")
        ProprietarioRequest proprietario,

        @NotNull(message = "Dados do veículo são obrigatórios")
        @Valid
        @Schema(description = "Dados do veículo a ser cadastrado")
        VeiculoSemProprietarioRequest veiculo,

        @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
        @Schema(
                description = "Observações da ordem de serviço",
                example = "Proprietário relatou barulho ao frear"
        )
        String reclamacaoProprietario,

        @Schema(description = "Serviços (referenciados por Id) a adicionar à ordem de serviço já na criação")
        List<@Valid ServicoRequest> servicos

) {
        public CriarOrdemServicoCompletaUseCase.Input toUseCaseInput(UserId userId) {
                var servicosInput = servicos != null
                        ? servicos.stream().map(ServicoRequest::toUseCaseInput).toList()
                        : List.<AdicionarServicoUseCase.Input.ServicoAdicionar>of();
                return new CriarOrdemServicoCompletaUseCase.Input(proprietario, veiculo, userId, reclamacaoProprietario, servicosInput);
        }
}

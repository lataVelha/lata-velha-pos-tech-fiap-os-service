package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberAprovacaoOrcamentoClienteUseCase;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReceberAprovacaoOrcamentoRequest(
        @NotEmpty(message = "Lista de serviços não pode ser vazia")
        List<@Valid ServicoAprovacao> servicos
) {
    public ReceberAprovacaoOrcamentoClienteUseCase.Input toInput(Long osId) {
        return new ReceberAprovacaoOrcamentoClienteUseCase.Input(
                osId,
                servicos.stream()
                        .map(s -> new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(s.execucaoServicoId(), s.status()))
                        .toList()
        );
    }

    public record ServicoAprovacao(
            @NotNull(message = "ID da execução de serviço é obrigatório")
            Long execucaoServicoId,
            @NotNull(message = "Status é obrigatório")
            StatusExecucaoServico status
    ) {}
}

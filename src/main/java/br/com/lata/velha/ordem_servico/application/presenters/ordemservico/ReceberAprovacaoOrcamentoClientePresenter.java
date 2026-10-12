package br.com.lata.velha.ordem_servico.application.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;

public interface ReceberAprovacaoOrcamentoClientePresenter {

    record ViewModel(Long idOs, String status) {}

    ViewModel present(OrdemServico ordemServico);
}

package br.com.lata.velha.ordem_servico.api.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.ReceberAprovacaoOrcamentoClientePresenter;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import org.springframework.stereotype.Component;

@Component
public class ReceberAprovacaoOrcamentoClientePresenterImpl implements ReceberAprovacaoOrcamentoClientePresenter {
    @Override
    public ReceberAprovacaoOrcamentoClientePresenter.ViewModel present(OrdemServico ordemServico) {
        return new ReceberAprovacaoOrcamentoClientePresenter.ViewModel(
                ordemServico.getId(),
                ordemServico.getStatus().name()
        );
    }
}

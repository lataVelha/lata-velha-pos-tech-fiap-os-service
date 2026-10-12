package br.com.lata.velha.ordem_servico.api.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.AprovarOrdemServicoPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AprovarOrdemServicoPresenterImpl implements AprovarOrdemServicoPresenter {

    @Override
    public AprovarOrdemServicoResponse present(OrdemServico ordemServico) {
        List<AprovarOrdemServicoResponse.Servico> servicos = ordemServico.getExecucaoServicos().stream()
                .map(s -> new AprovarOrdemServicoResponse.Servico(s.getId(), s.getStatus().name()))
                .toList();

        return new AprovarOrdemServicoResponse(
                ordemServico.getId(),
                ordemServico.getStatus().name(),
                servicos
        );
    }
}

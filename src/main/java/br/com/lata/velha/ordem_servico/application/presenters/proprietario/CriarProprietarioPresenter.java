package br.com.lata.velha.ordem_servico.application.presenters.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;

public interface CriarProprietarioPresenter {
    ProprietarioResponse present(Proprietario proprietario);
}

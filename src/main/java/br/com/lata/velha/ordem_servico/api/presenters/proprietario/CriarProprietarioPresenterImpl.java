package br.com.lata.velha.ordem_servico.api.presenters.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.application.presenters.proprietario.CriarProprietarioPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import org.springframework.stereotype.Component;

@Component
public class CriarProprietarioPresenterImpl implements CriarProprietarioPresenter {
    @Override
    public ProprietarioResponse present(Proprietario proprietario) {
        return ProprietarioResponse.from(proprietario);
    }
}

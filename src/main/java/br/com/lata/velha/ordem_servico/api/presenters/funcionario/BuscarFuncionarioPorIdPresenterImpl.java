package br.com.lata.velha.ordem_servico.api.presenters.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.BuscarFuncionarioPorIdPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import org.springframework.stereotype.Component;

@Component
public class BuscarFuncionarioPorIdPresenterImpl implements BuscarFuncionarioPorIdPresenter {
    @Override
    public FuncionarioResponse present(Funcionario funcionario) {
        return FuncionarioResponse.from(funcionario);
    }
}

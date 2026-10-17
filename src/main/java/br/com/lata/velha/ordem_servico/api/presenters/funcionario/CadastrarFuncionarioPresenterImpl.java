package br.com.lata.velha.ordem_servico.api.presenters.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.CadastrarFuncionarioPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import org.springframework.stereotype.Component;

@Component
public class CadastrarFuncionarioPresenterImpl implements CadastrarFuncionarioPresenter {
    @Override
    public FuncionarioResponse present(Funcionario funcionario) {
        return FuncionarioResponse.from(funcionario);
    }
}

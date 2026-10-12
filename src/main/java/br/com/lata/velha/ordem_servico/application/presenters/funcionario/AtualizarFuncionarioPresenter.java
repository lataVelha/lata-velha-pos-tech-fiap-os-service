package br.com.lata.velha.ordem_servico.application.presenters.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

public interface AtualizarFuncionarioPresenter {
    FuncionarioResponse present(Funcionario funcionario);
}

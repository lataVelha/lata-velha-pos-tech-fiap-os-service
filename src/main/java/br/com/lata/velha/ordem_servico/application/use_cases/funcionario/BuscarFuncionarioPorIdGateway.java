package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

public interface BuscarFuncionarioPorIdGateway {
    Funcionario getFuncionarioById(Long id);
}

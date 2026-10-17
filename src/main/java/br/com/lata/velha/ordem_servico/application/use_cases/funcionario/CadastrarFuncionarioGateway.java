package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

public interface CadastrarFuncionarioGateway {
    Cargo getCargoPorId(Long id);
    Funcionario salvarFuncionario(Funcionario f);
}

package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public interface DesativarFuncionarioGateway {
    Funcionario getFuncionarioById(Long id);
    void desativarUsuario(UserId userId);
    Funcionario salvarFuncionario(Funcionario f);
}

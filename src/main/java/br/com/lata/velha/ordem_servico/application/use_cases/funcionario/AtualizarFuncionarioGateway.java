package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public interface AtualizarFuncionarioGateway {
    Funcionario getFuncionarioById(Long id);
    boolean isUsuarioAtivo(UserId userId);
    Cargo getCargoPorId(Long cargoId);
    Funcionario salvarFuncionario(Funcionario f);
}

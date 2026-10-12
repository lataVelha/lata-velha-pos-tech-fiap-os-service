package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.*;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuncionarioGatewayImpl implements
        CadastrarFuncionarioGateway,
        AtualizarFuncionarioGateway,
        BuscarFuncionarioPorIdGateway,
        DesativarFuncionarioGateway {

    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;
    private final UserRepository userRepository;

    @Override
    public Cargo getCargoPorId(Long id) {
        return cargoRepository.getById(id);
    }

    @Override
    public Funcionario salvarFuncionario(Funcionario f) {
        return funcionarioRepository.save(f);
    }

    @Override
    public Funcionario getFuncionarioById(Long id) {
        return funcionarioRepository.getById(id);
    }

    @Override
    public boolean isUsuarioAtivo(UserId userId) {
        return userRepository.isAtivoById(userId);
    }

    @Override
    public void desativarUsuario(UserId userId) {
        var user = userRepository.getById(userId);
        user.desativar();
        userRepository.save(user);
    }
}

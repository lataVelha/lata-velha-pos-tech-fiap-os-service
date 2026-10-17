package br.com.lata.velha.authentication.infrastructure.gateways;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.repositories.RoleRepository;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.value_objects.Credential;
import br.com.lata.velha.authentication.domain.value_objects.Senha;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserDto;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserResponseDto;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.value_objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RoleRepository roleRepository;
    private final CargoRepository cargoRepository;
    private final Logger logger;

    @Override
    public Set<String> getRolesForCargo(Long cargoId) {
        logger.logInfo("Buscando roles do cargo - cargoId={}", cargoId);
        var cargo = cargoRepository.getByIdWithRoles(cargoId);
        return cargo.getRoles().stream().map(Role::getNome).collect(Collectors.toSet());
    }

    @Override
    public CreateAuthUserResponseDto createUser(CreateAuthUserDto input) {
        logger.logInfo("Criando usuário de autenticação");
        var email = Email.fromString(input.email());
        if (userRepository.existsByEmail(email)) {
            logger.logWarn("Cadastro de usuário rejeitado: e-mail já cadastrado");
            throw new ResourceAlreadyExistsException("Usuário já existe com o e-mail informado");
        }

        if (userRepository.existsByCpf(input.cpf())) {
            logger.logWarn("Cadastro de usuário rejeitado: CPF já cadastrado");
            throw new ResourceAlreadyExistsException("Usuário já existe com o CPF informado");
        }

        logger.logInfo("Validações de duplicidade concluídas, buscando roles - roles={}", input.roles());
        var roles = roleRepository.getByNomes(input.roles());
        var senha = Senha.fromString(input.senha());
        var credential = Credential.fromSenha(senha, passwordHasher);
        User user = User.create(email, credential, roles, input.cpf());

        logger.logInfo("Salvando novo usuário de autenticação");
        var saved = userRepository.save(user);
        logger.logInfo("Usuário de autenticação criado com sucesso - userId={}", saved.getId().getValue());
        return new CreateAuthUserResponseDto(saved.getId().getValue());
    }
}

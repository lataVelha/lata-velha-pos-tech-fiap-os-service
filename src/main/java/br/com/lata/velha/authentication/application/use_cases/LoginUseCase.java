package br.com.lata.velha.authentication.application.use_cases;

import br.com.lata.velha.authentication.application.gateways.TokenProvider;
import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final Logger logger;

    public Output execute(Input input) {
        logger.logInfo("Buscando usuário para login - username={}", input.username());
        var user = userRepository.getByUsernameWithRoles(input.username());
        user.login(input.senha);

        String scopes = user.getRoles()
                .stream()
                .map(Role::getNome)
                .collect(Collectors.joining(" "));

        logger.logInfo("Gerando token de acesso - userId={}, scopes={}", user.getId(), scopes);
        String token = tokenProvider.generate(user.getId(), scopes);
        logger.logInfo("Login realizado com sucesso - username={}, userId={}", input.username(), user.getId());

        return new Output(token, tokenProvider.getExpiresIn());
    }

    public record Input(String username, String senha) {}
    public record Output(String token, Long expiresIn) {}
}

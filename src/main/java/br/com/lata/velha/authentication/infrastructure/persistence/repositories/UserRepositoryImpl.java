package br.com.lata.velha.authentication.infrastructure.persistence.repositories;

import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions.UserNotFoundException;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.infrastructure.persistence.entities.UserEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;
    private final PasswordHasher passwordHasher;
    private final Logger logger;

    @Override
    public User getById(UserId id) {
        return jpaRepository.findById(id.getValue())
                .orElseThrow(() -> {
                    logger.logWarn("Usuário não encontrado - userId={}", id);
                    return UserNotFoundException.fromId(id);
                })
                .toDomain(passwordHasher);
    }

    @Override
    public User getByUsernameWithRoles(String username) {
        return jpaRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> {
                    logger.logWarn("Usuário não encontrado - username={}", username);
                    return UserNotFoundException.fromUsername(username);
                })
                .toDomain(passwordHasher);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValor());
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public boolean isAtivoById(UserId id) {
        return getById(id).isAtivo();
    }

    @Override
    public User save(User user) {
        var updated = jpaRepository.save(UserEntity.fromDomain(user));
        return updated.toDomain(passwordHasher);
    }
}

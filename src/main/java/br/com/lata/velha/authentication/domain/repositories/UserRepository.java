package br.com.lata.velha.authentication.domain.repositories;

import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public interface UserRepository {
    User getById(UserId id);
    User getByUsernameWithRoles(String username);
    boolean existsByEmail(Email email);
    boolean existsByCpf(String cpf);
    boolean isAtivoById(UserId id);
    User save(User user);

}

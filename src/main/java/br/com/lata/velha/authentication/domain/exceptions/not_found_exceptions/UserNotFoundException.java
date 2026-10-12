package br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class UserNotFoundException extends NotFoundException {
    private UserNotFoundException(String paramName, String paramValue) {
        super(User.class, paramName, paramValue);
    }

    public static UserNotFoundException fromId(UserId id) {
        return new UserNotFoundException("id", id.toString());
    }

    public static UserNotFoundException fromUsername(String username) {
        return new UserNotFoundException("username", username);
    }
}

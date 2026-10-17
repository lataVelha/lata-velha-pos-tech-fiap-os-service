package br.com.lata.velha.authentication.domain.value_objects;

import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public record UserData(UserId id, String username, Email email, boolean isActive) { }

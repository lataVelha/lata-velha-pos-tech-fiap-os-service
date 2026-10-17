package br.com.lata.velha.authentication.application.gateways;

import br.com.lata.velha.shared.domain.value_objects.UserId;

public interface TokenProvider {
    String generate(UserId userId, String scopes);
    long getExpiresIn();
}

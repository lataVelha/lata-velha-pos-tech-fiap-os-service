package br.com.lata.velha.authentication.domain.services;

import br.com.lata.velha.authentication.domain.value_objects.Credential;
import br.com.lata.velha.authentication.domain.value_objects.Senha;

public interface PasswordHasher {
    String hashSenha(Senha senha);
    boolean match(Credential credential, String rawPassword);
}

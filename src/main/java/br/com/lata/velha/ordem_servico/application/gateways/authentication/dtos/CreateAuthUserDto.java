package br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos;

import java.util.Set;

public record CreateAuthUserDto(
        String email,
        String senha,
        Set<String> roles,
        String cpf
) { }

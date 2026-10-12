package br.com.lata.velha.ordem_servico.application.gateways.authentication;

import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserDto;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserResponseDto;

import java.util.Set;

public interface AuthenticationService {
    Set<String> getRolesForCargo(Long cargoId);
    CreateAuthUserResponseDto createUser(CreateAuthUserDto input);
}

package br.com.lata.velha.authentication.api.dtos.responses;

import br.com.lata.velha.authentication.application.use_cases.LoginUseCase;

public record LoginResponse(String token, Long expiresIn) {
    public static LoginResponse fromLoginOutput(LoginUseCase.Output output) {
        return new LoginResponse(output.token(), output.expiresIn());
    }
}

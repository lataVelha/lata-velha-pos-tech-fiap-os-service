package br.com.lata.velha.authentication.api.dtos.requests;


import br.com.lata.velha.authentication.application.use_cases.LoginUseCase;

public record LoginRequest (String username, String password){
    public LoginUseCase.Input toLoginInput() {
        return new LoginUseCase.Input(username, password);
    }
}

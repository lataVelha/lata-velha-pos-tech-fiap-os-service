package br.com.lata.velha.authentication.api.controllers;

import br.com.lata.velha.authentication.api.dtos.requests.LoginRequest;
import br.com.lata.velha.authentication.api.dtos.responses.LoginResponse;
import br.com.lata.velha.authentication.application.use_cases.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Autenticação de funcionários via JWT. É o único endpoint público da API — todos os demais requerem o token obtido aqui no header Authorization: Bearer <token>.")
public class LoginController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    @Operation(
            summary = "Realizar login",
            description = "Autentica o funcionário e retorna um token JWT. Clique em **Authorize** no topo da página e cole o token para autenticar os demais endpoints."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso — use o campo `token` para autenticar os demais endpoints",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas — username ou senha incorretos"
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var input = loginRequest.toLoginInput();
        var output = loginUseCase.execute(input);
        var response = LoginResponse.fromLoginOutput(output);
        return ResponseEntity.ok(response);
    }
}
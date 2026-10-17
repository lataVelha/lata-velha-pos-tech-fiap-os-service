package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.application.controllers.funcionario.FuncionarioCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funcionarios")
@Tag(name = "Funcionários", description = "Cadastro e gerenciamento de funcionários. O cargo define as permissões: ATENDENTE, MECANICO ou ADMIN.")
public class FuncionarioController {

    private final FuncionarioCleanController cleanController;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar novo funcionário", description = "Cria funcionário e usuário de acesso (login/senha) vinculados a um cargo existente. O CPF é obrigatório e único — é ele que permite o login alternativo via a function de autenticação por CPF.")
    @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Cargo não encontrado")
    public ResponseEntity<FuncionarioResponse> cadastrar(@Valid @RequestBody CadastrarFuncionarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cleanController.cadastrar(request.toCadastrarInput()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cleanController.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar funcionário", description = "Atualiza nome e cargo. Senha e username não são alterados por este endpoint.")
    @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Funcionário ou cargo não encontrado")
    public ResponseEntity<FuncionarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarFuncionarioRequest request) {
        return ResponseEntity.ok(cleanController.atualizar(request.toUpdateUseCaseInput(id)));
    }

    @PatchMapping("/{id}/desativar")
    @Transactional
    @Operation(summary = "Desativar funcionário", description = "Soft delete — o funcionário é inativado e não poderá fazer login.")
    @ApiResponse(responseCode = "204", description = "Funcionário desativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        cleanController.desativar(id);
        return ResponseEntity.noContent().build();
    }
}

package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.application.controllers.proprietario.ProprietarioCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/proprietarios")
@Tag(name = "Proprietários", description = "Cadastro e gerenciamento de proprietários. Deve ser cadastrado antes de registrar veículos e abrir OS. O e-mail é usado para notificações.")
public class ProprietarioController {

    private final ProprietarioCleanController cleanController;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar proprietário", description = "Registra proprietário com dados pessoais e endereço. O e-mail será usado para notificações das OS.")
    @ApiResponse(responseCode = "201", description = "Proprietário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "CPF/CNPJ já cadastrado")
    public ResponseEntity<ProprietarioResponse> cadastrar(@Valid @RequestBody ProprietarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cleanController.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar proprietários ativos")
    @ApiResponse(responseCode = "200", description = "Lista paginada de proprietários ativos")
    public ResponseEntity<PaginatedResult<ProprietarioResponse>> listar(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cleanController.listar(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar proprietário por ID")
    @ApiResponse(responseCode = "200", description = "Proprietário encontrado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cleanController.buscarPorId(id));
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Buscar proprietário por CPF/CNPJ", description = "Útil para verificar se o proprietário já está cadastrado antes de abrir uma OS.")
    @ApiResponse(responseCode = "200", description = "Proprietário encontrado")
    @ApiResponse(responseCode = "404", description = "Nenhum proprietário encontrado com o documento informado")
    public ResponseEntity<ProprietarioResponse> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(cleanController.buscarPorDocumento(documento));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar proprietário")
    @ApiResponse(responseCode = "200", description = "Proprietário atualizado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ProprietarioRequest request) {
        return ResponseEntity.ok(cleanController.atualizar(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @Transactional
    @Operation(summary = "Desativar proprietário", description = "Soft delete — inativa o registro sem excluí-lo.")
    @ApiResponse(responseCode = "204", description = "Proprietário desativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        cleanController.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @Transactional
    @Operation(summary = "Reativar proprietário")
    @ApiResponse(responseCode = "200", description = "Proprietário reativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(cleanController.reativar(id));
    }
}

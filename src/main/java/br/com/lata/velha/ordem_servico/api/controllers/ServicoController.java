package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.application.controllers.servico.ServicoCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
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
@RequestMapping("/servicos")
@Tag(name = "Serviços", description = "Catálogo de serviços disponíveis na oficina. Os serviços cadastrados aqui são os que o mecânico pode adicionar a uma Ordem de Serviço durante o diagnóstico.")
public class ServicoController {

    private final ServicoCleanController cleanController;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar novo serviço", description = "Adiciona um novo tipo de serviço ao catálogo da oficina. Ex: 'Troca de óleo', 'Alinhamento', 'Balanceamento'. O serviço pode ser associado a peças quando adicionado a uma OS.")
    @ApiResponse(responseCode = "201", description = "Serviço criado")
    public ResponseEntity<ServicoResponse> cadastrar(@Valid @RequestBody CadastrarServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cleanController.cadastrar(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços ativos", description = "Retorna lista de serviços ativos no sistema")
    @ApiResponse(responseCode = "200", description = "Serviços listados")
    public ResponseEntity<PaginatedResult<ServicoResponse>> listar(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cleanController.buscarTodos(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID")
    @ApiResponse(responseCode = "200", description = "Serviço encontrado")
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cleanController.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar informações do serviço")
    @ApiResponse(responseCode = "200", description = "Serviço atualizado")
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    public ResponseEntity<ServicoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarServicoRequest request) {
        return ResponseEntity.ok(cleanController.atualizar(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @Transactional
    @Operation(summary = "Desativar serviço (Soft Delete)", description = "Inativa o serviço do catálogo sem excluir o registro. Serviços desativados não podem ser adicionados a novas OS, mas OS existentes que já utilizam o serviço não são afetadas.")
    @ApiResponse(responseCode = "204", description = "Serviço desativado")
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        cleanController.desativar(id);
        return ResponseEntity.noContent().build();
    }
}

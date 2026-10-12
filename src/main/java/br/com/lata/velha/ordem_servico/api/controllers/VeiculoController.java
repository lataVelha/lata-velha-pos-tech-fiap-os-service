package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.application.controllers.veiculo.VeiculoCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "Cadastro e gerenciamento de veículos. Um veículo deve estar vinculado a um proprietário e ser cadastrado antes de ser incluído em uma Ordem de Serviço.")
public class VeiculoController {

    private final VeiculoCleanController cleanController;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar veículo", description = "Registra um novo veículo vinculado a um proprietário existente. A placa deve ser única no sistema.")
    @ApiResponse(responseCode = "201", description = "Veículo cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos — campos obrigatórios ausentes")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado (proprietarioId inválido)")
    @ApiResponse(responseCode = "409", description = "Placa já cadastrada para outro veículo")
    public ResponseEntity<VeiculoResponse> cadastrar(@Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cleanController.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar veículos ativos paginado")
    @ApiResponse(responseCode = "200", description = "Veículos listados")
    public ResponseEntity<PaginatedResult<VeiculoResponse>> listar(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cleanController.listar(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veículo por ID")
    @ApiResponse(responseCode = "200", description = "Veículo encontrado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cleanController.buscarPorId(id));
    }

    @GetMapping("/proprietario/{proprietarioId}")
    @Operation(summary = "Listar veículos de um proprietário", description = "Retorna todos os veículos ativos vinculados a um proprietário. Útil na abertura de uma OS para selecionar o veículo do proprietário.")
    @ApiResponse(responseCode = "200", description = "Lista de veículos do proprietário")
    public ResponseEntity<List<VeiculoResponse>> listarPorProprietario(@PathVariable Long proprietarioId) {
        return ResponseEntity.ok(cleanController.listarPorProprietario(proprietarioId));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar veículo")
    @ApiResponse(responseCode = "200", description = "Veículo atualizado")
    @ApiResponse(responseCode = "404", description = "Veículo ou proprietário não encontrado")
    public ResponseEntity<VeiculoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.ok(cleanController.atualizar(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @Transactional
    @Operation(summary = "Desativar veículo (soft delete)")
    @ApiResponse(responseCode = "204", description = "Veículo desativado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        cleanController.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @Transactional
    @Operation(summary = "Reativar veículo")
    @ApiResponse(responseCode = "200", description = "Veículo reativado")
    @ApiResponse(responseCode = "404", description = "Veículo inativo não encontrado")
    public ResponseEntity<VeiculoResponse> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(cleanController.reativar(id));
    }
}

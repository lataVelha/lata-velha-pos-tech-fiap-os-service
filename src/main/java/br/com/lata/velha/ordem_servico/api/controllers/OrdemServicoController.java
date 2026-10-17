package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.CriarOrdemServicoCompletaRequest;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.CriarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.ReceberAprovacaoOrcamentoRequest;
import br.com.lata.velha.ordem_servico.application.controllers.ordemservico.OrdemServicoCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.ReceberAprovacaoOrcamentoClientePresenter;
import br.com.lata.velha.ordem_servico.application.dtos.request.AddServicoRequest;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/ordens-servico")
@RequiredArgsConstructor
@Tag(name = "Ordens de Serviço", description = "Ciclo de vida completo das OS: abertura, diagnóstico, aprovação, execução e entrega do veículo.")
public class OrdemServicoController {

    private final OrdemServicoCleanController cleanController;

    @PostMapping
    @Transactional
    @Operation(
            summary = "Abrir ordem de serviço",
            description = "**ATENDENTE** — Registra a entrada do veículo e a reclamação do proprietário. O atendente logado é vinculado automaticamente. Status resultante: `RECEBIDA`."
    )
    @ApiResponse(responseCode = "201", description = "OS criada — status RECEBIDA")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Veículo, proprietário ou atendente não encontrado")
    public ResponseEntity<OrdemServicoResponse> create(@Valid @RequestBody CriarOrdemServicoRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cleanController.criar(request.toCriarOsUseCaseInput(UserId.fromString(jwt.getSubject()))));
    }

    @PostMapping("/completa")
    @Transactional
    @Operation(
            summary = "Abrir ordem de serviço com cadastro completo",
            description = "**ATENDENTE** — Cadastra proprietário e veículo (dados completos) e abre a OS em uma única requisição, opcionalmente já vinculando serviços/peças (referenciados por Id). O atendente logado é vinculado automaticamente. Status resultante: `RECEBIDA`."
    )
    @ApiResponse(responseCode = "201", description = "Proprietário, veículo e OS criados — status RECEBIDA")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Atendente, serviço ou peça não encontrado")
    public ResponseEntity<OrdemServicoResponse> createCompleta(@Valid @RequestBody CriarOrdemServicoCompletaRequest request,
                                                                @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cleanController.criarCompleta(request.toUseCaseInput(UserId.fromString(jwt.getSubject()))));
    }

    @GetMapping
    @Operation(
            summary = "Listar ordens de serviço",
            description = "Lista paginada de OS com filtros opcionais por `id`, `status`, `proprietarioId` e `mecanicoId`. Todos os filtros são combináveis."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    public ResponseEntity<PaginatedResult<OrdemServicoResponse>> getOrdems(
            @Parameter(description = "Filtrar por ID da OS") @RequestParam(required = false) Long id,
            @Parameter(description = "Filtrar por status da OS") @RequestParam(required = false) StatusOrdemServico status,
            @Parameter(description = "Filtrar por ID do proprietário") @RequestParam(required = false) Long proprietarioId,
            @Parameter(description = "Filtrar por ID do mecânico") @RequestParam(required = false) Long mecanicoId,
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cleanController.buscar(id, status, proprietarioId, mecanicoId, page, size));
    }

    @GetMapping("/metricas/tempo-medio-execucao")
    @Operation(
            summary = "Tempo médio de execução por serviço [ADMIN]",
            description = "**ADMIN** — Tempo médio (horas) por tipo de serviço nas OS finalizadas. Período opcional via `dataInicio` e `dataFim` no formato `dd/MM/yyyy`."
    )
    @ApiResponse(responseCode = "200", description = "Métricas calculadas com sucesso")
    @ApiResponse(responseCode = "400", description = "Formato de data inválido — use dd/MM/yyyy")
    public ResponseEntity<TempoMedioExecucaoResponse> getAverageExecutionTime(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDate dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDate dataFim) {
        return ResponseEntity.ok(cleanController.buscarTempoMedioExecucao(dataInicio, dataFim));
    }

    @PatchMapping("/{idOs}/iniciar-diagnostico")
    @Transactional
    @Operation(
            summary = "Iniciar diagnóstico",
            description = "**MECANICO** — Mecânico logado assume a OS e inicia a inspeção. Transição: `RECEBIDA` → `EM_DIAGNOSTICO`."
    )
    @ApiResponse(responseCode = "200", description = "Diagnóstico iniciado — status EM_DIAGNOSTICO")
    @ApiResponse(responseCode = "404", description = "OS ou mecânico não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status RECEBIDA")
    public ResponseEntity<Void> startDiagnostic(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @AuthenticationPrincipal Jwt jwt) {
        cleanController.iniciarDiagnostico(new IniciarDiagnosticoUseCase.Input(idOs, UserId.fromString(jwt.getSubject())));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idOs}/adicionar-servico")
    @Transactional
    @Operation(
            summary = "Adicionar serviços à OS",
            description = "**MECANICO** — Adiciona serviços (com mão de obra e peças) durante o diagnóstico. O mesmo serviço não pode ser adicionado duas vezes. Status inicial do serviço: `PENDENTE`."
    )
    @ApiResponse(responseCode = "200", description = "Serviços adicionados com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "OS, serviço ou peça não encontrada")
    @ApiResponse(responseCode = "422", description = "Serviço já adicionado ou status da OS não permite adição")
    public ResponseEntity<Void> addService(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Valid @RequestBody AddServicoRequest request
    ) {
        cleanController.adicionarServico(request.toUseCaseInput(idOs));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idOs}/finalizar-diagnostico")
    @Transactional
    @Operation(
            summary = "Finalizar diagnóstico",
            description = "**MECANICO** — Encerra o diagnóstico e envia e-mail ao proprietário com os serviços identificados. Transição: `EM_DIAGNOSTICO` → `AGUARDANDO_APROVACAO`."
    )
    @ApiResponse(responseCode = "200", description = "Diagnóstico finalizado — status AGUARDANDO_APROVACAO. E-mail enviado ao proprietário.")
    @ApiResponse(responseCode = "404", description = "OS ou mecânico não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status EM_DIAGNOSTICO")
    public ResponseEntity<Void> finalDiagnostic(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @AuthenticationPrincipal Jwt jwt) {
        cleanController.finalizarDiagnostico(new FinalizarDiagnosticoUseCase.Input(idOs, UserId.fromString(jwt.getSubject())));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idOs}/aprovar")
    @Transactional
    @Operation(
            summary = "Aprovar ou reprovar serviços da OS",
            description = """
                    **ATENDENTE** — Avalia cada serviço com `APROVADO` ou `RECUSADO`. Serviços não informados são automaticamente recusados. Pelo menos um deve ser aprovado.

                    A reserva de estoque (e o aviso de encomenda, se insuficiente) é feita pelo InventoryService. E-mail de confirmação enviado ao proprietário. Transição: `AGUARDANDO_APROVACAO` → `APROVADA`."""
    )
    @ApiResponse(responseCode = "200", description = "OS aprovada")
    @ApiResponse(responseCode = "400", description = "Status inválido ou ID de serviço não pertence à OS")
    @ApiResponse(responseCode = "404", description = "OS ou funcionário não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está em AGUARDANDO_APROVACAO ou nenhum serviço foi aprovado")
    public ResponseEntity<AprovarOrdemServicoResponse> approve(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Valid @RequestBody AprovarOrdemServicoRequest request, @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(cleanController.aprovar(request.toInput(UserId.fromString(jwt.getSubject()), idOs)));
    }

    @PatchMapping("/{idOs}/reprovar")
    @Transactional
    @Operation(
            summary = "Reprovar a OS inteira",
            description = "**ATENDENTE** — Recusa todos os serviços e encerra a OS. Use quando o proprietário recusar todos os serviços identificados. Para aprovação parcial, use `PATCH /aprovar`. Transição: `AGUARDANDO_APROVACAO` → `REPROVADA` (terminal)."
    )
    @ApiResponse(responseCode = "200", description = "OS reprovada — status REPROVADA")
    @ApiResponse(responseCode = "404", description = "OS ou funcionário não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status AGUARDANDO_APROVACAO")
    public ResponseEntity<Void> reprove(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @AuthenticationPrincipal Jwt jwt) {
        cleanController.reprovar(new ReprovarOrdemServicoUseCase.Input(idOs, UserId.fromString(jwt.getSubject())));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idOs}/iniciar-servico/{servicoId}")
    @Transactional
    @Operation(
            summary = "Iniciar execução de um serviço",
            description = "**MECANICO** — Inicia a execução de um serviço aprovado. Apenas um serviço pode estar em execução por vez. Status do serviço: `APROVADO` → `EM_EXECUCAO`."
    )
    @ApiResponse(responseCode = "200", description = "Execução iniciada — status do serviço: EM_EXECUCAO")
    @ApiResponse(responseCode = "404", description = "OS, serviço ou mecânico não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está em EM_EXECUCAO, serviço não está APROVADO ou já há outro em execução")
    public ResponseEntity<Void> startService(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Parameter(description = "ID do serviço a iniciar", example = "5") @PathVariable Long servicoId,
            @AuthenticationPrincipal Jwt jwt) {
        cleanController.iniciarServico(new IniciarServicoUseCase.Input(idOs, servicoId, UserId.fromString(jwt.getSubject())));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idOs}/finalizar-servico/{servicoId}")
    @Transactional
    @Operation(
            summary = "Finalizar execução de um serviço",
            description = "**MECANICO** — Conclui um serviço (`EM_EXECUCAO` → `FINALIZADO`). Ao finalizar o último serviço, a OS passa para `FINALIZADA` e um e-mail é enviado ao proprietário."
    )
    @ApiResponse(responseCode = "200", description = "Serviço finalizado. Se for o último, OS → FINALIZADA e e-mail enviado ao proprietário.")
    @ApiResponse(responseCode = "404", description = "OS, serviço ou mecânico não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está em EM_EXECUCAO ou serviço não está em EM_EXECUCAO")
    public ResponseEntity<Void> finishService(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Parameter(description = "ID do serviço a finalizar", example = "5") @PathVariable Long servicoId,
            @AuthenticationPrincipal Jwt jwt) {
        cleanController.finalizarServico(new FinalizarServicoUseCase.Input(idOs, servicoId, UserId.fromString(jwt.getSubject())));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{idOs}/retirar-veiculo")
    @Transactional
    @Operation(
            summary = "Registrar retirada do veículo",
            description = "**ATENDENTE** — Registra a entrega do veículo ao proprietário. Transição: `FINALIZADA` → `ENTREGUE` (terminal)."
    )
    @ApiResponse(responseCode = "200", description = "Veículo entregue — status ENTREGUE.")
    @ApiResponse(responseCode = "404", description = "OS ou funcionário não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status FINALIZADA")
    public ResponseEntity<Void> removeVehicle(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @AuthenticationPrincipal Jwt jwt) {
        cleanController.retirarVeiculo(idOs, UserId.fromString(jwt.getSubject()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status-service")
    @Operation(
            summary = "Listar ordens por prioridade de status",
            description = "Lista paginada de OS ordenadas por prioridade de status (Em Execução > Aguardando Aprovação > Diagnóstico > Recebida) e data mais antigua primeiro."
    )
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    public ResponseEntity<PaginatedResult<OrdemServicoResponse>> getStatusService(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(cleanController.buscarPorStatus(page, size));
    }

    @PostMapping("/{id}/aprovacao-orcamento")
    @Transactional
    @Operation(
            summary = "Receber aprovação de orçamento do cliente",
            description = "**Webhook aberto** — Endpoint chamado pelo cliente (sem autenticação) para informar quais serviços do orçamento foram aprovados ou recusados. Ao menos um serviço deve ser aprovado para a OS seguir para execução."
    )
    @ApiResponse(responseCode = "200", description = "Aprovação processada — retorna ID da OS e novo status")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou serviços não pertencem à OS")
    @ApiResponse(responseCode = "404", description = "OS não encontrada")
    @ApiResponse(responseCode = "422", description = "OS não está em AGUARDANDO_APROVACAO")
    public ResponseEntity<ReceberAprovacaoOrcamentoClientePresenter.ViewModel> receberAprovacaoOrcamento(
            @PathVariable Long id,
            @Valid @RequestBody ReceberAprovacaoOrcamentoRequest request) {
        return ResponseEntity.ok(cleanController.receberAprovacaoOrcamentoCliente(request.toInput(id)));
    }
}

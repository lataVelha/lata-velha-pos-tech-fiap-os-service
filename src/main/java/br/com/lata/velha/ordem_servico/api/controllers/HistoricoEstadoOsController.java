package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.application.controllers.historicoestadoos.HistoricoEstadoOsCleanController;
import br.com.lata.velha.ordem_servico.domain.view.TempoMedioPorEstado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historico")
@Tag(name = "Histórico", description = "Métricas sobre o histórico de estados das ordens de serviço.")
public class HistoricoEstadoOsController {

    private final HistoricoEstadoOsCleanController cleanController;

    @GetMapping("/tempo-medio-por-estado")
    @Operation(summary = "Tempo médio das ordens de serviço por estado, no intervalo informado")
    @ApiResponse(responseCode = "200", description = "Tempo médio calculado")
    @ApiResponse(responseCode = "400", description = "Nenhuma data informada ou intervalo inválido")
    public ResponseEntity<List<TempoMedioPorEstado>> buscarTempoMedioPorEstado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(cleanController.buscarTempoMedioPorEstado(inicio, fim));
    }
}

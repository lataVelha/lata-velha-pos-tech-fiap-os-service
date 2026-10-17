package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.HistoricoEstadoOs;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "ORDEM_SERVICO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PROPRIETARIO_ID", nullable = false)
    private Long proprietarioId;

    @Column(name = "VEICULO_ID", nullable = false)
    private Long veiculoId;

    @Column(name = "RECLAMACAO_PROPRIETARIO")
    private String reclamacaoProprietario;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30)
    private StatusOrdemServico status;

    @Column(name = "CRIADO_EM")
    private LocalDateTime criadoEm;

    @Column(name = "INICIADO_EM")
    private LocalDateTime iniciadoEm;

    @Column(name = "FINALIZADO_EM")
    private LocalDateTime finalizadoEm;

    @Column(name = "ENTREGUE_EM")
    private LocalDateTime entregueEm;

    @Column(name = "ATUALIZADO_EM")
    private LocalDateTime atualizadoEm;

    @Column(name = "ATENDENTE_INICIO_ID", nullable = false)
    private Long atendenteInicioId;

    @Column(name = "MECANICO_RESPONSAVEL_ID")
    private Long mecanicoResponsavelId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "OS_ID")
    private List<ExecucaoServicoEntity> servicos;

    @OneToMany
    @JoinColumn(name = "os_id", insertable = false, updatable = false)
    private List<HistoricoEstadoOsEntity> historicoEstados = new ArrayList<>();

    public static OrdemServicoEntity fromDomain(OrdemServico domain) {
        var servicos = domain.getExecucaoServicos().stream()
                .map(ExecucaoServicoEntity::fromDomain)
                .collect(Collectors.toCollection(ArrayList::new));
        var historicoEstados = new ArrayList<HistoricoEstadoOsEntity>();
        return new OrdemServicoEntity(
                domain.getId(),
                domain.getProprietarioId(),
                domain.getVeiculoId(),
                domain.getReclamacaoProprietario(),
                domain.getStatus(),
                domain.getCriadoEm(),
                domain.getIniciadoEm(),
                domain.getFinalizadoEm(),
                domain.getEntregueEm(),
                domain.getAtualizadoEm(),
                domain.getAtendenteInicioId(),
                domain.getMecanicoResponsavelId(),
                servicos,
                historicoEstados
        );
    }

    public OrdemServico toDomain() {
        var execucaoServicosDomain = this.servicos.stream()
                .map(ExecucaoServicoEntity::toDomain)
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        return map(execucaoServicosDomain);
    }

    public OrdemServico toDomain(List<ExecucaoServicoEntity> execucoes) {
        var execucaoServicosDomain = execucoes.stream()
                .map(ExecucaoServicoEntity::toDomain)
                .toList();
        return map(execucaoServicosDomain);
    }

    private OrdemServico map(List<ExecucaoServico> servicos){
        var historicoDomain = new ArrayList<HistoricoEstadoOs>();
        if (this.historicoEstados != null) {
            this.historicoEstados.stream()
                    .map(HistoricoEstadoOsEntity::toDomain)
                    .forEach(historicoDomain::add);
        }
        return new OrdemServico(
                id,
                proprietarioId,
                veiculoId,
                reclamacaoProprietario,
                status,
                criadoEm,
                iniciadoEm,
                finalizadoEm,
                entregueEm,
                atualizadoEm,
                atendenteInicioId,
                mecanicoResponsavelId,
                servicos,
                historicoDomain
        );
    }
}

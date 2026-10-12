package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "EXECUCAO_SERVICO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecucaoServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "OS_ID", nullable = false)
    private Long ordemServicoId;

    @Column(name = "SERVICO_ID", nullable = false)
    private Long servicoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_SERVICO", length = 30)
    private StatusExecucaoServico statusExecucaoServico;

    @Column(name = "VALOR_MAO_DE_OBRA", precision = 10, scale = 2)
    private BigDecimal valorMaoDeObra;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "EXECUCAO_SERVICO_ID")
    private Set<PecaNecessariaEntity> pecasNecessarias = new HashSet<>();

    @Column(name = "ATENDENTE_APROVACAO_ID")
    private Long atendenteAprovacaoId;

    @Column(name = "MECANICO_RESPONSAVEL_ID")
    private Long mecanicoResponsavelId;

    @Column(name = "INICIADO_EM")
    private LocalDateTime iniciadoEm;

    @Column(name = "TERMINADO_EM")
    private LocalDateTime terminadoEm;

    @Column(name = "ATUALIZADO_EM")
    private LocalDateTime atualizadoEm;

    public static ExecucaoServicoEntity fromDomain(ExecucaoServico domain) {
        var pecas = domain.getPecasNecessarias().stream()
                .map(PecaNecessariaEntity::fromDomain)
                .collect(Collectors.toSet());
        return new ExecucaoServicoEntity(
                domain.getId(),
                domain.getOrdemServicoId(),
                domain.getServicoId(),
                domain.getStatus(),
                domain.getValorMaoDeObra(),
                pecas,
                domain.getAtendenteAprovacaoId(),
                domain.getMecanicoResponsavelId(),
                domain.getIniciadoEm(),
                domain.getTerminadoEm(),
                domain.getAtualizadoEm()
        );
    }

    public ExecucaoServico toDomain() {
        var pecasDomain = this.pecasNecessarias.stream()
                .map(PecaNecessariaEntity::toDomain)
                .collect(Collectors.toSet());
        return new ExecucaoServico(
                id,
                servicoId,
                ordemServicoId,
                statusExecucaoServico,
                valorMaoDeObra,
                pecasDomain,
                atendenteAprovacaoId,
                mecanicoResponsavelId,
                iniciadoEm,
                terminadoEm,
                atualizadoEm
        );
    }

    public void setOrdemServico(OrdemServicoEntity ordemServico) {
        this.ordemServicoId = ordemServico != null ? ordemServico.getId() : null;
    }

    public void setServico(ServicoEntity servico) {
        this.servicoId = servico != null ? servico.getId() : null;
    }
}

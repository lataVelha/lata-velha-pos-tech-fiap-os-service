package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityConversionTest {

    // ------------------------------------------------------------------ ServicoEntity

    @Test
    void servicoEntity_toDomain_deveMapearTodosCampos() {
        var entity = new ServicoEntity();
        entity.setId(10L);
        entity.setNome("Troca de óleo");
        entity.setDescricao("Troca do óleo do motor");
        entity.setAtivo(true);

        var domain = entity.toDomain();

        assertThat(domain.getId()).isEqualTo(10L);
        assertThat(domain.getNome()).isEqualTo("Troca de óleo");
        assertThat(domain.getDescricao()).isEqualTo("Troca do óleo do motor");
        assertThat(domain.isAtivo()).isTrue();
    }

    @Test
    void servicoEntity_toDomain_deveMapearAtivoFalse() {
        var entity = new ServicoEntity();
        entity.setId(11L);
        entity.setNome("Alinhamento");
        entity.setDescricao("Alinhamento de rodas");
        entity.setAtivo(false);

        assertThat(entity.toDomain().isAtivo()).isFalse();
    }

    // ------------------------------------------------------------------ PecaNecessariaEntity

    @Test
    void pecaNecessariaEntity_fromDomain_deveMapearTodosCampos() {
        var now = LocalDateTime.now();
        var domain = new PecaNecessaria(10L, 5L, 1L, 4, now);
        var entity = PecaNecessariaEntity.fromDomain(domain);

        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getPecaId()).isEqualTo(5L);
        assertThat(entity.getExecucaoServicoId()).isEqualTo(1L);
        assertThat(entity.getQuantidade()).isEqualTo(4);
        assertThat(entity.getAtualizado()).isEqualTo(now);
    }

    @Test
    void pecaNecessariaEntity_fromDomain_deveRetornarNullParaDomainNull() {
        assertThat(PecaNecessariaEntity.fromDomain(null)).isNull();
    }

    @Test
    void pecaNecessariaEntity_toDomain_deveMapearTodosCampos() {
        var now = LocalDateTime.now();
        var entity = new PecaNecessariaEntity(20L, 6L, 2L, 3, now);
        var domain = entity.toDomain();

        assertThat(domain.getId()).isEqualTo(20L);
        assertThat(domain.getPecaId()).isEqualTo(6L);
        assertThat(domain.getExecucaoServicoId()).isEqualTo(2L);
        assertThat(domain.getQuantidade()).isEqualTo(3);
    }

    // ------------------------------------------------------------------ ExecucaoServicoEntity

    @Test
    void execucaoServicoEntity_fromDomain_deveMapearTodosCampos() {
        var now = LocalDateTime.now();
        var execucao = new ExecucaoServico(
                1L, 10L, 100L,
                StatusExecucaoServico.APROVADO,
                new BigDecimal("200.00"),
                Set.of(),
                5L, 8L,
                now.minusHours(2), now, now
        );
        var entity = ExecucaoServicoEntity.fromDomain(execucao);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getServicoId()).isEqualTo(10L);
        assertThat(entity.getOrdemServicoId()).isEqualTo(100L);
        assertThat(entity.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.APROVADO);
        assertThat(entity.getValorMaoDeObra()).isEqualByComparingTo("200.00");
        assertThat(entity.getAtendenteAprovacaoId()).isEqualTo(5L);
        assertThat(entity.getMecanicoResponsavelId()).isEqualTo(8L);
        assertThat(entity.getPecasNecessarias()).isEmpty();
    }

    @Test
    void execucaoServicoEntity_toDomain_deveMapearTodosCampos() {
        var entity = new ExecucaoServicoEntity();
        entity.setId(2L);
        entity.setOrdemServicoId(200L);
        entity.setServicoId(20L);
        entity.setStatusExecucaoServico(StatusExecucaoServico.FINALIZADO);
        entity.setValorMaoDeObra(new BigDecimal("350.00"));
        entity.setMecanicoResponsavelId(9L);

        var domain = entity.toDomain();

        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getOrdemServicoId()).isEqualTo(200L);
        assertThat(domain.getServicoId()).isEqualTo(20L);
        assertThat(domain.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
        assertThat(domain.getValorMaoDeObra()).isEqualByComparingTo("350.00");
        assertThat(domain.getMecanicoResponsavelId()).isEqualTo(9L);
        assertThat(domain.getPecasNecessarias()).isEmpty();
    }

    @Test
    void execucaoServicoEntity_roundTrip_comPecasDevePreservarDados() {
        var pecaNecessaria = new PecaNecessaria(
                30L, 7L, 1L, 2, LocalDateTime.now()
        );
        var execucao = new ExecucaoServico(
                3L, 12L, 102L,
                StatusExecucaoServico.EM_EXECUCAO,
                new BigDecimal("150.00"),
                Set.of(pecaNecessaria),
                null, 9L,
                null, null, null
        );
        var roundTrip = ExecucaoServicoEntity.fromDomain(execucao).toDomain();

        assertThat(roundTrip.getPecasNecessarias()).hasSize(1);
        assertThat(roundTrip.getPecasNecessarias().iterator().next().getId()).isEqualTo(30L);
    }
}

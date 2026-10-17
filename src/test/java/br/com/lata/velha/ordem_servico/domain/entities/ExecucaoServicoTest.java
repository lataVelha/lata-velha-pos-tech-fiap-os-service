package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ExecucaoServico")
class ExecucaoServicoTest {

    private static final Long SERVICO_ID = 99L;
    private static final Long OS_ID = 10L;
    private static final Long EXEC_ID = 1L;
    private static final Long ATENDENTE_ID = 2L;
    private static final Long MECANICO_ID = 3L;

    private static ExecucaoServico build(StatusExecucaoServico status, Set<PecaNecessaria> pecas) {
        return new ExecucaoServico(EXEC_ID, SERVICO_ID, OS_ID, status, new BigDecimal("150.00"),
                pecas, null, null, null, null, LocalDateTime.now());
    }

    private static ExecucaoServico pendente() {
        return build(StatusExecucaoServico.PENDENTE, new HashSet<>());
    }

    private static ExecucaoServico aprovado() {
        return build(StatusExecucaoServico.APROVADO, new HashSet<>());
    }

    private static ExecucaoServico emExecucao() {
        return build(StatusExecucaoServico.EM_EXECUCAO, new HashSet<>());
    }

    private static PecaNecessaria peca(Long pecaId, int quantidade) {
        return new PecaNecessaria(null, pecaId, EXEC_ID, quantidade, LocalDateTime.now());
    }

    @Nested
    @DisplayName("construtor")
    class Construtor {

        @Test
        @DisplayName("deve lançar exceção quando servicoId é nulo")
        void deveLancarExcecaoQuandoServicoIdNulo() {
            assertThatThrownBy(() ->
                    new ExecucaoServico(null, null, OS_ID, StatusExecucaoServico.PENDENTE,
                            BigDecimal.ZERO, new HashSet<>(), null, null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Serviço obrigatório");
        }

        @Test
        @DisplayName("deve lançar exceção quando ordemServicoId é nulo")
        void deveLancarExcecaoQuandoOrdemServicoIdNulo() {
            assertThatThrownBy(() ->
                    new ExecucaoServico(null, SERVICO_ID, null, StatusExecucaoServico.PENDENTE,
                            BigDecimal.ZERO, new HashSet<>(), null, null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Ordem de serviço é obrigatória");
        }

        @Test
        @DisplayName("deve lançar exceção quando valorMaoDeObra é nulo")
        void deveLancarExcecaoQuandoValorNulo() {
            assertThatThrownBy(() ->
                    new ExecucaoServico(null, SERVICO_ID, OS_ID, StatusExecucaoServico.PENDENTE,
                            null, new HashSet<>(), null, null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Valor inválido");
        }

        @Test
        @DisplayName("deve lançar exceção quando valorMaoDeObra é negativo")
        void deveLancarExcecaoQuandoValorNegativo() {
            var value = new BigDecimal("-1");

            assertThatThrownBy(() ->
                    new ExecucaoServico(null, SERVICO_ID, OS_ID, StatusExecucaoServico.PENDENTE,
                            value, new HashSet<>(), null, null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Valor inválido");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("deve criar com status PENDENTE e sem peças")
        void deveCriarComStatusPendente() {
            ExecucaoServico exec = ExecucaoServico.create(SERVICO_ID, OS_ID, new BigDecimal("100.00"));

            assertThat(exec.getId()).isNull();
            assertThat(exec.getServicoId()).isEqualTo(SERVICO_ID);
            assertThat(exec.getOrdemServicoId()).isEqualTo(OS_ID);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.PENDENTE);
            assertThat(exec.getValorMaoDeObra()).isEqualByComparingTo("100.00");
            assertThat(exec.getPecasNecessarias()).isEmpty();
            assertThat(exec.getAtualizadoEm()).isNotNull();
        }
    }

    @Nested
    @DisplayName("aprovar")
    class Aprovar {

        @Test
        @DisplayName("deve transicionar para APROVADO quando pendente")
        void deveAprovarQuandoPendente() {
            ExecucaoServico exec = pendente();

            exec.aprovar(ATENDENTE_ID);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
            assertThat(exec.getAtendenteAprovacaoId()).isEqualTo(ATENDENTE_ID);
        }

        @Test
        @DisplayName("deve lançar exceção quando status não é PENDENTE")
        void deveLancarExcecaoQuandoNaoPendente() {
            ExecucaoServico exec = aprovado();

            assertThatThrownBy(() -> exec.aprovar(ATENDENTE_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Só serviços pendentes podem ser aprovados");
        }
    }

    @Nested
    @DisplayName("recusar")
    class Recusar {

        @Test
        @DisplayName("deve transicionar para RECUSADO e atribuir atendente")
        void deveRecusarEAtribuirAtendente() {
            ExecucaoServico exec = pendente();

            exec.recusar(ATENDENTE_ID);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
            assertThat(exec.getAtendenteAprovacaoId()).isEqualTo(ATENDENTE_ID);
            assertThat(exec.getTerminadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção quando status não é PENDENTE")
        void deveLancarExcecaoQuandoNaoPendente() {
            ExecucaoServico exec = aprovado();

            assertThatThrownBy(() -> exec.recusar(ATENDENTE_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Só serviços pendentes podem ser aprovados");
        }
    }

    @Nested
    @DisplayName("iniciar")
    class Iniciar {

        @Test
        @DisplayName("deve transicionar para EM_EXECUCAO e atribuir mecânico")
        void deveIniciarEAtribuirMecanico() {
            ExecucaoServico exec = aprovado();

            exec.iniciar(MECANICO_ID);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.EM_EXECUCAO);
            assertThat(exec.getMecanicoResponsavelId()).isEqualTo(MECANICO_ID);
            assertThat(exec.getIniciadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção quando status não é APROVADO")
        void deveLancarExcecaoQuandoNaoAprovado() {
            ExecucaoServico exec = pendente();

            assertThatThrownBy(() -> exec.iniciar(MECANICO_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Só serviços aprovados podem ser iniciados");
        }
    }

    @Nested
    @DisplayName("finalizar")
    class Finalizar {

        @Test
        @DisplayName("deve transicionar para FINALIZADO")
        void deveFinalizar() {
            ExecucaoServico exec = emExecucao();

            exec.finalizar();

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
            assertThat(exec.getTerminadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção quando status não é EM_EXECUCAO")
        void deveLancarExcecaoQuandoNaoEmExecucao() {
            ExecucaoServico exec = aprovado();

            assertThatThrownBy(exec::finalizar)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Não é possível finalizar um serviço que não está em execução");
        }
    }

    @Nested
    @DisplayName("adicionarPecaNecessaria")
    class AdicionarPecaNecessaria {

        @Test
        @DisplayName("deve adicionar peça sem alterar status")
        void deveAdicionarPecaSemMudarStatus() {
            ExecucaoServico exec = pendente();
            var peca = peca(5L, 2);

            exec.adicionarPecaNecessaria(peca);

            assertThat(exec.getPecasNecessarias()).hasSize(1);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.PENDENTE);
        }

        @Test
        @DisplayName("deve lançar exceção quando serviço já está finalizado")
        void deveLancarExcecaoQuandoFinalizado() {
            ExecucaoServico exec = build(StatusExecucaoServico.FINALIZADO, new HashSet<>());
            var peca = peca(5L, 1);

            assertThatThrownBy(() -> exec.adicionarPecaNecessaria(peca))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Serviço já finalizado");
        }

        @Test
        @DisplayName("deve lançar exceção quando peça é nula")
        void deveLancarExcecaoQuandoPecaNula() {
            ExecucaoServico exec = pendente();

            assertThatThrownBy(() -> exec.adicionarPecaNecessaria(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Peça inválida");
        }

        @Test
        @DisplayName("deve lançar exceção quando peça já foi adicionada")
        void deveLancarExcecaoQuandoPecaDuplicada() {
            ExecucaoServico exec = pendente();
            exec.adicionarPecaNecessaria(peca(5L, 2));
            var pecaDuplicada = peca(5L, 1);

            assertThatThrownBy(() -> exec.adicionarPecaNecessaria(pecaDuplicada))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Peça já adicionada ao serviço");
        }
    }

    @Nested
    @DisplayName("booleans")
    class Booleans {

        @Test
        @DisplayName("isPendente retorna true somente quando PENDENTE")
        void isPendente() {
            assertThat(pendente().isPendente()).isTrue();
            assertThat(aprovado().isPendente()).isFalse();
        }

        @Test
        @DisplayName("isAprovado retorna true somente quando APROVADO")
        void isAprovado() {
            assertThat(aprovado().isAprovado()).isTrue();
            assertThat(pendente().isAprovado()).isFalse();
        }

        @Test
        @DisplayName("isRecusado retorna true somente quando RECUSADO")
        void isRecusado() {
            assertThat(build(StatusExecucaoServico.RECUSADO, new HashSet<>()).isRecusado()).isTrue();
            assertThat(pendente().isRecusado()).isFalse();
        }

        @Test
        @DisplayName("isFinalizado retorna true somente quando FINALIZADO")
        void isFinalizado() {
            assertThat(build(StatusExecucaoServico.FINALIZADO, new HashSet<>()).isFinalizado()).isTrue();
            assertThat(pendente().isFinalizado()).isFalse();
        }

        @Test
        @DisplayName("isConcluido retorna true quando FINALIZADO")
        void isConcluidoQuandoFinalizado() {
            assertThat(build(StatusExecucaoServico.FINALIZADO, new HashSet<>()).isConcluido()).isTrue();
        }

        @Test
        @DisplayName("isConcluido retorna true quando RECUSADO")
        void isConcluidoQuandoRecusado() {
            assertThat(build(StatusExecucaoServico.RECUSADO, new HashSet<>()).isConcluido()).isTrue();
        }

        @Test
        @DisplayName("isConcluido retorna false quando PENDENTE")
        void isConcluidoFalsoQuandoPendente() {
            assertThat(pendente().isConcluido()).isFalse();
        }
    }

    @Nested
    @DisplayName("getters")
    class Getters {

        @Test
        @DisplayName("deve retornar todos os valores corretamente")
        void deveRetornarTodosOsValores() {
            var iniciadoEm = LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0);
            var terminadoEm = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 0);
            var atualizadoEm = LocalDateTime.of(2024, Month.JANUARY, 1, 12, 0);

            ExecucaoServico exec = new ExecucaoServico(EXEC_ID, SERVICO_ID, OS_ID,
                    StatusExecucaoServico.EM_EXECUCAO, new BigDecimal("200.00"),
                    new HashSet<>(), ATENDENTE_ID, MECANICO_ID,
                    iniciadoEm, terminadoEm, atualizadoEm);

            assertThat(exec.getId()).isEqualTo(EXEC_ID);
            assertThat(exec.getServicoId()).isEqualTo(SERVICO_ID);
            assertThat(exec.getOrdemServicoId()).isEqualTo(OS_ID);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.EM_EXECUCAO);
            assertThat(exec.getValorMaoDeObra()).isEqualByComparingTo("200.00");
            assertThat(exec.getPecasNecessarias()).isEmpty();
            assertThat(exec.getAtendenteAprovacaoId()).isEqualTo(ATENDENTE_ID);
            assertThat(exec.getMecanicoResponsavelId()).isEqualTo(MECANICO_ID);
            assertThat(exec.getIniciadoEm()).isEqualTo(iniciadoEm);
            assertThat(exec.getTerminadoEm()).isEqualTo(terminadoEm);
            assertThat(exec.getAtualizadoEm()).isEqualTo(atualizadoEm);
        }
    }

    @Nested
    @DisplayName("setStatus")
    class SetStatus {

        @Test
        @DisplayName("deve alterar o status")
        void deveAlterarStatus() {
            ExecucaoServico exec = pendente();

            exec.setStatus(StatusExecucaoServico.APROVADO);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        }
    }

}

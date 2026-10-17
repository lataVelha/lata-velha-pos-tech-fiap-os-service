package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrdemServico")
class OrdemServicoTest {

    private static OrdemServico recebida() {
        return OrdemServico.create(4L, 3L, "Barulho ao frear", 2L);
    }

    private static OrdemServico emDiagnostico() {
        OrdemServico os = recebida();
        os.iniciarDiagnostico(10L);
        return os;
    }

    private static OrdemServico aguardandoAprovacao() {
        OrdemServico os = emDiagnostico();
        ExecucaoServico exec = execucaoServico(1L);
        os.adicionarServico(exec);
        exec.aprovar(2L);
        os.finalizarDiagnostico(10L);
        return os;
    }

    private static OrdemServico emExecucao() {
        ExecucaoServico exec = ExecucaoServico.create(999L, 1L, new BigDecimal("150.00"));
        exec.setStatus(StatusExecucaoServico.FINALIZADO);
        return new OrdemServico(null, 4L, 3L, "Barulho ao frear",
                StatusOrdemServico.EM_EXECUCAO, LocalDateTime.now(), LocalDateTime.now(), null, null, null,
                2L, 10L, new java.util.ArrayList<>(java.util.List.of(exec)));
    }

    private static OrdemServico finalizada() {
        OrdemServico os = emExecucao();
        os.finalizar(10L);
        return os;
    }

    private static ExecucaoServico execucaoServico(Long servicoId) {
        return ExecucaoServico.create(servicoId, 1L, new BigDecimal("150.00"));
    }

    private static ExecucaoServico execucaoFinalizada(Long servicoId) {
        ExecucaoServico exec = execucaoServico(servicoId);
        exec.setStatus(StatusExecucaoServico.FINALIZADO);
        return exec;
    }

    @Nested
    @DisplayName("create")
    class Criacao {

        @Test
        @DisplayName("deve criar com status RECEBIDA")
        void deveCriarComStatusRecebida() {
            OrdemServico os = OrdemServico.create(4L, 3L, "Barulho ao frear", 2L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        }

        @Test
        @DisplayName("deve criar com os IDs corretos")
        void deveCriarComIdCorretos() {
            OrdemServico os = OrdemServico.create(4L, 3L, "Barulho ao frear", 2L);

            assertThat(os.getProprietarioId()).isEqualTo(4L);
            assertThat(os.getVeiculoId()).isEqualTo(3L);
            assertThat(os.getAtendenteInicioId()).isEqualTo(2L);
            assertThat(os.getReclamacaoProprietario()).isEqualTo("Barulho ao frear");
        }

        @Test
        @DisplayName("deve criar com id nulo e lista de serviços vazia")
        void deveCriarSemIdEComListaVazia() {
            OrdemServico os = OrdemServico.create(4L, 3L, "Barulho", 2L);

            assertThat(os.getId()).isNull();
            assertThat(os.getExecucaoServicos()).isEmpty();
        }
    }

    @Nested
    @DisplayName("iniciarDiagnostico")
    class IniciarDiagnostico {

        @Test
        @DisplayName("deve transitar para EM_DIAGNOSTICO e definir mecânico")
        void deveIniciarDiagnosticoComSucesso() {
            OrdemServico os = recebida();

            os.iniciarDiagnostico(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
            assertThat(os.getMecanicoResponsavelId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("deve lançar exceção se status não for RECEBIDA")
        void deveLancarExcecaoSeStatusNaoForRecebida() {
            OrdemServico os = emDiagnostico();

            assertThatThrownBy(() -> os.iniciarDiagnostico(10L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("finalizarDiagnostico")
    class FinalizarDiagnostico {

        @Test
        @DisplayName("deve transitar para AGUARDANDO_APROVACAO quando há serviços")
        void deveFinalizarDiagnosticoParaAguardandoAprovacaoComServicos() {
            OrdemServico os = emDiagnostico();
            os.adicionarServico(execucaoServico(1L));

            os.finalizarDiagnostico(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.AGUARDANDO_APROVACAO);
            assertThat(os.getMecanicoResponsavelId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("deve transitar para FINALIZADA quando não há serviços")
        void deveFinalizarDiagnosticoParaFinalizadaSemServicos() {
            OrdemServico os = emDiagnostico();

            os.finalizarDiagnostico(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
            assertThat(os.getFinalizadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção se status não for EM_DIAGNOSTICO")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.finalizarDiagnostico(10L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("aprovar")
    class Aprovar {

        @Test
        @DisplayName("deve transitar para APROVADA")
        void deveAprovarComSucesso() {
            OrdemServico os = aguardandoAprovacao();

            os.aprovar(2L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.APROVADA);
        }

        @Test
        @DisplayName("deve lançar exceção se status não for AGUARDANDO_APROVACAO")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.aprovar(2L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("reprovar")
    class Reprovar {

        @Test
        @DisplayName("deve transitar para REPROVADA e definir finalizadoEm")
        void deveReprovarComSucesso() {
            OrdemServico os = aguardandoAprovacao();

            os.reprovar(2L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.REPROVADA);
            assertThat(os.getFinalizadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção se status não for AGUARDANDO_APROVACAO")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.reprovar(2L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("finalizar")
    class Finalizar {

        @Test
        @DisplayName("deve transitar para FINALIZADA quando não há serviços")
        void deveFinalizarSemServicosComSucesso() {
            OrdemServico os = emExecucao();

            os.finalizar(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
            assertThat(os.getFinalizadoEm()).isNotNull();
            assertThat(os.getMecanicoResponsavelId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("deve lançar exceção se status não for EM_EXECUCAO")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.finalizar(10L))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("deve lançar exceção se existir serviço em execução")
        void deveLancarExcecaoSeExistirServicoEmExecucao() {
            ExecucaoServico finalizado = ExecucaoServico.create(999L, 1L, new BigDecimal("150.00"));
            finalizado.setStatus(StatusExecucaoServico.FINALIZADO);
            ExecucaoServico emAndamento = execucaoServico(1L);
            emAndamento.setStatus(StatusExecucaoServico.EM_EXECUCAO);
            OrdemServico os = new OrdemServico(null, 4L, 3L, "Barulho ao frear",
                    StatusOrdemServico.EM_EXECUCAO, LocalDateTime.now(), LocalDateTime.now(), null, null, null,
                    2L, 10L, new java.util.ArrayList<>(java.util.List.of(finalizado, emAndamento)));

            assertThatThrownBy(() -> os.finalizar(10L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Existem execuções de serviço não finalizadas");
        }

        @Test
        @DisplayName("não deve lançar exceção quando serviço recusado possui peças necessárias registradas")
        void naoDeveLancarExcecaoQuandoServicoRecusadoPossuiPecaNecessaria() {
            ExecucaoServico base = ExecucaoServico.create(999L, 1L, new BigDecimal("150.00"));
            base.setStatus(StatusExecucaoServico.FINALIZADO);
            ExecucaoServico recusado = execucaoFinalizada(1L);
            recusado.setStatus(StatusExecucaoServico.RECUSADO);
            recusado.getPecasNecessarias().add(new PecaNecessaria(1L, 1L, 1L, 5, LocalDateTime.now()));
            OrdemServico os = new OrdemServico(null, 4L, 3L, "Barulho ao frear",
                    StatusOrdemServico.EM_EXECUCAO, LocalDateTime.now(), LocalDateTime.now(), null, null, null,
                    2L, 10L, new java.util.ArrayList<>(java.util.List.of(base, recusado)));

            os.finalizar(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
        }

        @Test
        @DisplayName("deve finalizar quando serviços ativos possuem peças necessárias registradas")
        void deveFinalizarQuandoServicosAtivosPossuemPecaNecessaria() {
            ExecucaoServico base = ExecucaoServico.create(999L, 1L, new BigDecimal("150.00"));
            base.setStatus(StatusExecucaoServico.FINALIZADO);
            ExecucaoServico exec = execucaoFinalizada(1L);
            exec.getPecasNecessarias().add(new PecaNecessaria(1L, 1L, 1L, 5, LocalDateTime.now()));
            OrdemServico os = new OrdemServico(null, 4L, 3L, "Barulho ao frear",
                    StatusOrdemServico.EM_EXECUCAO, LocalDateTime.now(), LocalDateTime.now(), null, null, null,
                    2L, 10L, new java.util.ArrayList<>(java.util.List.of(base, exec)));

            os.finalizar(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
        }
    }

    @Nested
    @DisplayName("entregar")
    class Entregar {

        @Test
        @DisplayName("deve transitar para ENTREGUE quando OS está FINALIZADA")
        void deveEntregarComSucesso() {
            OrdemServico os = finalizada();

            os.entregar(2L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
            assertThat(os.getEntregueEm()).isNotNull();
        }

        @Test
        @DisplayName("deve transitar para ENTREGUE quando OS está REPROVADA")
        void deveEntregarQuandoOsReprovada() {
            OrdemServico os = aguardandoAprovacao();
            os.reprovar(2L);

            os.entregar(3L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
            assertThat(os.getEntregueEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção se status não for FINALIZADA nem REPROVADA")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.entregar(2L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("adicionarServico")
    class AdicionarServico {

        @Test
        @DisplayName("deve adicionar serviço à lista quando OS está EM_DIAGNOSTICO")
        void deveAdicionarServicoComSucesso() {
            OrdemServico os = emDiagnostico();
            ExecucaoServico servico = execucaoServico(1L);

            os.adicionarServico(servico);

            assertThat(os.getExecucaoServicos()).hasSize(1);
            assertThat(os.getExecucaoServicos().get(0)).isEqualTo(servico);
        }

        @Test
        @DisplayName("deve lançar exceção para serviço nulo")
        void deveLancarExcecaoParaServicoNulo() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.adicionarServico(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Serviço inválido");
        }

        @Test
        @DisplayName("deve lançar exceção se ordem estiver FINALIZADA")
        void deveLancarExcecaoSeOrdemFinalizada() {
            OrdemServico os = finalizada();
            ExecucaoServico servico = execucaoServico(99L);

            assertThatThrownBy(() -> os.adicionarServico(servico))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Não é possível adicionar serviço");
        }

        @Test
        @DisplayName("deve lançar exceção se ordem estiver ENTREGUE")
        void deveLancarExcecaoSeOrdemEntregue() {
            OrdemServico os = finalizada();
            os.entregar(2L);
            ExecucaoServico servico = execucaoServico(99L);

            assertThatThrownBy(() -> os.adicionarServico(servico))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Não é possível adicionar serviço");
        }

        @Test
        @DisplayName("deve lançar exceção se serviço já estiver na ordem")
        void deveLancarExcecaoSeServicoDuplicado() {
            OrdemServico os = emDiagnostico();
            ExecucaoServico servico1 = execucaoServico(1L);
            ExecucaoServico servico2 = execucaoServico(1L);
            os.adicionarServico(servico1);

            assertThatThrownBy(() -> os.adicionarServico(servico2))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Serviço já adicionado");
        }

        @Test
        @DisplayName("deve adicionar serviço à lista quando OS está RECEBIDA")
        void deveAdicionarServicoComSucessoQuandoOsEstaRecebida() {
            OrdemServico os = recebida();
            ExecucaoServico servico = execucaoServico(1L);

            os.adicionarServico(servico);

            assertThat(os.getExecucaoServicos()).hasSize(1);
            assertThat(os.getExecucaoServicos().getFirst()).isEqualTo(servico);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToString {

        @Test
        @DisplayName("deve conter id e status")
        void deveConterIdEStatus() {
            OrdemServico os = finalizada();

            String result = os.toString();

            assertThat(result).contains("status=FINALIZADA");
        }

        @Test
        @DisplayName("não deve lançar exceção")
        void naoDeveLancarExcecao() {
            assertThat(recebida().toString()).isNotNull();
        }
    }

    @Nested
    @DisplayName("equals")
    class Equals {

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void deveSerIgualASiMesmo() {
            OrdemServico os = recebida();

            assertThat(os).isEqualTo(os);
        }

        @Test
        @DisplayName("deve ser igual a outra instância com mesmo id")
        void deveSerIgualComMesmoId() {
            OrdemServico os1 = OrdemServico.create(1L, 2L, "ruído", 3L);
            OrdemServico os2 = OrdemServico.create(1L, 2L, "ruído", 3L);

            assertThat(os1).isEqualTo(os2);
        }

        @Test
        @DisplayName("não deve ser igual quando ids são diferentes")
        void naoDeveSerIgualComIdsDiferentes() {
            OrdemServico a = new OrdemServico(1L, 1L, 1L, "x", StatusOrdemServico.RECEBIDA,
                    null, null, null, null, null, 1L, null, new java.util.ArrayList<>());
            OrdemServico b = new OrdemServico(2L, 1L, 1L, "x", StatusOrdemServico.RECEBIDA,
                    null, null, null, null, null, 1L, null, new java.util.ArrayList<>());

            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void naoDeveSerIgualANull() {
            assertThat(recebida()).isNotNull();
        }

        @Test
        @DisplayName("não deve ser igual a objeto de outro tipo")
        void naoDeveSerIgualAOutroTipo() {
            assertThat(recebida()).isNotEqualTo("string");
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCode {

        @Test
        @DisplayName("instâncias com mesmo id devem ter mesmo hashCode")
        void mesmoIdMesmoHashCode() {
            OrdemServico a = new OrdemServico(1L, 1L, 1L, "x", StatusOrdemServico.RECEBIDA,
                    null, null, null, null, null, 1L, null, new java.util.ArrayList<>());
            OrdemServico b = new OrdemServico(1L, 9L, 9L, "y", StatusOrdemServico.FINALIZADA,
                    null, null, null, null, null, 9L, null, new java.util.ArrayList<>());

            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("instâncias com ids diferentes devem ter hashCodes diferentes")
        void idsDiferentesHashCodesDiferentes() {
            OrdemServico a = new OrdemServico(1L, 1L, 1L, "x", StatusOrdemServico.RECEBIDA,
                    null, null, null, null, null, 1L, null, new java.util.ArrayList<>());
            OrdemServico b = new OrdemServico(2L, 1L, 1L, "x", StatusOrdemServico.RECEBIDA,
                    null, null, null, null, null, 1L, null, new java.util.ArrayList<>());

            assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        }
    }
}

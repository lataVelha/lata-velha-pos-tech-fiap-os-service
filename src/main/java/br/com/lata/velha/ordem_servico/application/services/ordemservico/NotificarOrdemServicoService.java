package br.com.lata.velha.ordem_servico.application.services.ordemservico;

import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificarOrdemServicoService {

    private final NotificarOrdemServicoGateway gateway;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;
    private final Logger logger;

    private static final String RECEBIDA = "Recebida";
    private static final String EM_DIAGNOSTICO = "Em Diagnóstico";
    private static final String FINALIZADA = "Finalizada";
    private static final String ENTREGUE = "Entregue";
    private static final String AGUARDANDO_APROVACAO = "Aguardando Aprovação";
    private static final String MSG_VEICULO_RECEBIDO = "Veículo recebido pela oficina";
    private static final String MSG_MECANICO_AVALIOU = "Mecânico avaliando o veículo";
    private static final String MSG_VEICULO_RETIRADO = "Veículo retirado pelo proprietário";
    private static final String STATUS = "status";
    private static final String RECUSADO = "RECUSADO";
    private static final String CONCLUIDO = "CONCLUIDO";

    public NotificarOrdemServicoService(NotificarOrdemServicoGateway gateway,
                                        EmailProvider emailProvider,
                                        EmailTemplateProvider templateProvider,
                                        Logger logger) {
        this.gateway = gateway;
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
        this.logger = logger;
    }

    public void execute(OrdemServico os) {
        logger.logInfo("Notificando proprietário sobre OS - osId={}, status={}", os.getId(), os.getStatus());
        try {
            Proprietario proprietario = gateway.getProprietarioPorId(os.getProprietarioId());
            Veiculo veiculo = gateway.getVeiculoPorId(os.getVeiculoId());

            Map<String, Object> variables = buildVariables(os, proprietario, veiculo);
            String assunto = getAssunto(os.getStatus());

            String html = templateProvider.render("os-notificacao", variables);
            emailProvider.send(proprietario.getEmail(), assunto, html);
            logger.logInfo("E-mail de notificação de OS enviado - osId={}", os.getId());
        } catch (Exception e) {
            logger.logError("Falha ao enviar e-mail de notificação de status de OS - osId=" + os.getId() + ", status=" + os.getStatus(), e);
        }
    }

    private Map<String, Object> buildVariables(OrdemServico os, Proprietario proprietario, Veiculo veiculo) {
        boolean fluxoReprovado = isFluxoReprovado(os);
        boolean semServicos = isSemServicos(os);

        Map<String, Object> variables = new HashMap<>();
        variables.put("nome", proprietario.getNome());
        variables.put("osNumero", os.getId());
        variables.put("veiculo", veiculo.getMarca() + " " + veiculo.getModelo());
        variables.put("reprovada", fluxoReprovado);
        variables.put("timeline", buildTimeline(os.getStatus(), fluxoReprovado, semServicos));

        StatusConfig config = semServicos ? getConfigSemServicos(os.getStatus()) : getConfig(os.getStatus());
        variables.put("subtitulo", config.subtitulo());
        variables.put("mensagem", config.mensagem());

        addReclamacao(variables, os);
        addServicos(variables, os);
        addAprovadosRecusados(variables, os);
        addTodosRecusados(variables, os);

        return variables;
    }

    private boolean isFluxoReprovado(OrdemServico os) {
        if (os.getStatus() == StatusOrdemServico.REPROVADA) return true;
        return os.getStatus() == StatusOrdemServico.ENTREGUE
                && !os.getExecucaoServicos().isEmpty()
                && os.getExecucaoServicos().stream().allMatch(ExecucaoServico::isRecusado);
    }

    private boolean isSemServicos(OrdemServico os) {
        return (os.getStatus() == StatusOrdemServico.FINALIZADA || os.getStatus() == StatusOrdemServico.ENTREGUE)
                && os.getExecucaoServicos().isEmpty();
    }

    private String getAssunto(StatusOrdemServico status) {
        return getConfig(status).assuntoEmail() + " - Lata Velha";
    }

    private StatusConfig getConfig(StatusOrdemServico status) {
        return switch (status) {
            case RECEBIDA -> new StatusConfig(
                    "Ordem de Serviço",
                    "Sua Ordem de Serviço foi aberta com sucesso. Acompanhe abaixo o progresso.",
                    "Ordem de Serviço Aberta"
            );
            case EM_DIAGNOSTICO -> new StatusConfig(
                    "Diagnóstico Iniciado",
                    "Nosso mecânico iniciou o diagnóstico do seu veículo.",
                    "Diagnóstico Iniciado"
            );
            case AGUARDANDO_APROVACAO -> new StatusConfig(
                    "Diagnóstico Finalizado",
                    "O diagnóstico do seu veículo foi finalizado. Confira abaixo os serviços identificados.",
                    "Diagnóstico Finalizado"
            );
            case APROVADA -> new StatusConfig(
                    "Serviços Aprovados",
                    "Os serviços foram aprovados. Em breve nossa equipe iniciará a execução.",
                    "Serviços Aprovados"
            );
            case EM_EXECUCAO -> new StatusConfig(
                    "Serviços em Execução",
                    "Nossa equipe já está trabalhando no seu veículo.",
                    "Serviços em Execução"
            );
            case FINALIZADA -> new StatusConfig(
                    "Serviços Finalizados",
                    "Todos os serviços do seu veículo foram concluídos. Seu veículo está pronto para retirada.",
                    "Serviços Finalizados"
            );
            case REPROVADA -> new StatusConfig(
                    "Serviços Recusados",
                    "Todos os serviços foram reprovados!",
                    "Serviços Recusados"
            );
            case ENTREGUE -> new StatusConfig(
                    "Veículo Entregue",
                    "Seu veículo foi entregue. Agradecemos a confiança!",
                    "Veículo Entregue"
            );
        };
    }

    private void addReclamacao(Map<String, Object> variables, OrdemServico os) {
        if (os.getStatus() == StatusOrdemServico.RECEBIDA
                || os.getStatus() == StatusOrdemServico.EM_DIAGNOSTICO
                || os.getStatus() == StatusOrdemServico.AGUARDANDO_APROVACAO) {
            variables.put("reclamacao", os.getReclamacaoProprietario());
        }
    }

    private void addServicos(Map<String, Object> variables, OrdemServico os) {
        if (os.getStatus() != StatusOrdemServico.AGUARDANDO_APROVACAO
                || os.getExecucaoServicos().isEmpty()) {
            return;
        }

        var servicosIds = os.getExecucaoServicos().stream()
                .map(ExecucaoServico::getServicoId)
                .collect(Collectors.toSet());
        var mapServicos = gateway.getServicosAtivosPorIds(servicosIds).stream()
                .collect(Collectors.toMap(Servico::getId, servico -> servico));

        List<Map<String, Object>> servicos = os.getExecucaoServicos().stream()
                .map(s -> {
                    var servico = mapServicos.get(s.getServicoId());
                    Map<String, Object> map = new HashMap<>();
                    if (servico != null) {
                        map.put("nome", servico.getNome());
                        map.put("descricao", servico.getDescricao());
                    }
                    return map;
                })
                .toList();

        variables.put("servicos", servicos);
    }

    private static final Set<StatusExecucaoServico> STATUS_APROVADOS = Set.of(
            StatusExecucaoServico.APROVADO,
            StatusExecucaoServico.EM_EXECUCAO,
            StatusExecucaoServico.AGUARDANDO_PECA,
            StatusExecucaoServico.FINALIZADO
    );

    private void addAprovadosRecusados(Map<String, Object> variables, OrdemServico os) {
        if ((os.getStatus() != StatusOrdemServico.EM_EXECUCAO && os.getStatus() != StatusOrdemServico.APROVADA)
                || os.getExecucaoServicos().isEmpty()) {
            return;
        }

        List<Map<String, Object>> aprovados = os.getExecucaoServicos().stream()
                .filter(s -> STATUS_APROVADOS.contains(s.getStatus()))
                .map(this::toServicoMap)
                .toList();

        List<Map<String, Object>> recusados = os.getExecucaoServicos().stream()
                .filter(s -> s.getStatus() == StatusExecucaoServico.RECUSADO)
                .map(this::toServicoMap)
                .toList();

        variables.put("servicosAprovados", aprovados);
        variables.put("servicosRecusados", recusados);
    }

    private void addTodosRecusados(Map<String, Object> variables, OrdemServico os) {
        if (!isFluxoReprovado(os) || os.getExecucaoServicos().isEmpty()) {
            return;
        }

        List<Map<String, Object>> recusados = os.getExecucaoServicos().stream()
                .map(this::toServicoMap)
                .toList();

        variables.put("servicosRecusados", recusados);
    }

    private Map<String, Object> toServicoMap(ExecucaoServico s) {
        var servico = gateway.getServicoAtivoPorId(s.getServicoId());
        Map<String, Object> map = new HashMap<>();
        map.put("nome", servico.getNome());
        return map;
    }

    private StatusConfig getConfigSemServicos(StatusOrdemServico status) {
        return switch (status) {
            case FINALIZADA -> new StatusConfig(
                    "Nenhum Serviço Necessário",
                    "O diagnóstico foi concluído e nenhum serviço foi identificado. Seu veículo está pronto para retirada.",
                    "Veículo Disponível para Retirada"
            );
            default -> getConfig(status);
        };
    }

    private List<Map<String, Object>> buildTimeline(StatusOrdemServico statusAtual, boolean fluxoReprovado, boolean semServicos) {
        List<TimelineStep> steps;

        if (semServicos) {
            steps = List.of(
                    new TimelineStep(1, RECEBIDA, MSG_VEICULO_RECEBIDO, StatusOrdemServico.RECEBIDA),
                    new TimelineStep(2, EM_DIAGNOSTICO, MSG_MECANICO_AVALIOU, StatusOrdemServico.EM_DIAGNOSTICO),
                    new TimelineStep(3, FINALIZADA, "Nenhum serviço identificado", StatusOrdemServico.FINALIZADA),
                    new TimelineStep(4, ENTREGUE, MSG_VEICULO_RETIRADO, StatusOrdemServico.ENTREGUE)
            );
        } else if (fluxoReprovado) {
            steps = List.of(
                    new TimelineStep(1, RECEBIDA, MSG_VEICULO_RECEBIDO, StatusOrdemServico.RECEBIDA),
                    new TimelineStep(2, EM_DIAGNOSTICO, MSG_MECANICO_AVALIOU, StatusOrdemServico.EM_DIAGNOSTICO),
                    new TimelineStep(3, AGUARDANDO_APROVACAO, "Serviços apresentados ao proprietário", StatusOrdemServico.AGUARDANDO_APROVACAO),
                    new TimelineStep(4, "Reprovada", "Todos os serviços foram recusados", StatusOrdemServico.REPROVADA),
                    new TimelineStep(5, ENTREGUE, MSG_VEICULO_RETIRADO, StatusOrdemServico.ENTREGUE)
            );
        } else {
            steps = List.of(
                    new TimelineStep(1, RECEBIDA, MSG_VEICULO_RECEBIDO, StatusOrdemServico.RECEBIDA),
                    new TimelineStep(2, EM_DIAGNOSTICO, MSG_MECANICO_AVALIOU, StatusOrdemServico.EM_DIAGNOSTICO),
                    new TimelineStep(3, AGUARDANDO_APROVACAO, "Aprovação dos serviços identificados", StatusOrdemServico.AGUARDANDO_APROVACAO),
                    new TimelineStep(4, "Aprovada", "Serviços aprovados, aguardando início da execução", StatusOrdemServico.APROVADA),
                    new TimelineStep(5, "Em Execução", "Mecânico executando os serviços", StatusOrdemServico.EM_EXECUCAO),
                    new TimelineStep(6, FINALIZADA, "Serviços concluídos", StatusOrdemServico.FINALIZADA),
                    new TimelineStep(7, ENTREGUE, MSG_VEICULO_RETIRADO, StatusOrdemServico.ENTREGUE)
            );
        }

        int currentIndex = steps.stream()
                .filter(s -> s.status() == statusAtual)
                .map(TimelineStep::numero)
                .findFirst()
                .orElse(1);

        boolean isStepConcluido = statusAtual == StatusOrdemServico.RECEBIDA
                || statusAtual == StatusOrdemServico.FINALIZADA
                || statusAtual == StatusOrdemServico.ENTREGUE
                || statusAtual == StatusOrdemServico.REPROVADA
                || statusAtual == StatusOrdemServico.APROVADA;

        List<Map<String, Object>> timeline = new ArrayList<>();
        for (TimelineStep step : steps) {
            Map<String, Object> map = new HashMap<>();
            map.put("numero", step.numero());
            map.put("titulo", step.titulo());
            map.put("descricao", step.descricao());

            if (step.numero() < currentIndex) {
                map.put(STATUS, step.status() == StatusOrdemServico.REPROVADA ? RECUSADO : CONCLUIDO);
            } else if (step.numero() == currentIndex) {
                if (isStepConcluido) {
                    map.put(STATUS, step.status() == StatusOrdemServico.REPROVADA ? RECUSADO : CONCLUIDO);
                } else {
                    map.put(STATUS, "ATUAL");
                }
            } else {
                map.put(STATUS, "PENDENTE");
            }

            timeline.add(map);
        }

        return timeline;
    }

    private record StatusConfig(String subtitulo, String mensagem, String assuntoEmail) {}

    private record TimelineStep(int numero, String titulo, String descricao, StatusOrdemServico status) {}
}

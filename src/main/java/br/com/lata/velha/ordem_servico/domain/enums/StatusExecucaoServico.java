package br.com.lata.velha.ordem_servico.domain.enums;

public enum StatusExecucaoServico {
    PENDENTE,
    APROVADO,
    RECUSADO,
    EM_EXECUCAO,
    AGUARDANDO_PECA,
    FINALIZADO;

        public static StatusExecucaoServico tryParseOrRecusado(String value) {
            try {
                return StatusExecucaoServico.valueOf(value);
            } catch (IllegalArgumentException ignored) {
                return StatusExecucaoServico.RECUSADO;
            }
        }
    }
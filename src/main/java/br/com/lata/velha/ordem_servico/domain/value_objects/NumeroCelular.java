package br.com.lata.velha.ordem_servico.domain.value_objects;

import java.util.Objects;

public class NumeroCelular {

    private final String valor;

    private NumeroCelular(String valor) {
        this.valor = valor;
    }

    public static NumeroCelular of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Número de celular não pode ser vazio");
        }

        String cleanedValue = valor.replaceAll("[^\\d]", "");

        if (cleanedValue.length() < 10 || cleanedValue.length() > 11) {
            throw new IllegalArgumentException(
                    "Número de celular inválido. Informe DDD + número (10 ou 11 dígitos)");
        }

        return new NumeroCelular(cleanedValue);
    }

    // --- getter ---

    public String getValor() { return valor; }

    // --- business method ---

    public String getFormatted() {
        if (valor.length() == 11) {
            return valor.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        return valor.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(valor, ((NumeroCelular) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return getFormatted(); }
}
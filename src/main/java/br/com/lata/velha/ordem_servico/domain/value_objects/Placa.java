package br.com.lata.velha.ordem_servico.domain.value_objects;

import java.util.Objects;

public class Placa {

    private final String valor;

    private Placa(String valor) {
        this.valor = valor.toUpperCase();
    }

    public static Placa of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Placa não pode ser vazia");
        }

        String cleanedValue = valor.toUpperCase().replaceAll("[^A-Z0-9]", "");

        if (!cleanedValue.matches("[A-Z]{3}\\d[A-Z0-9]\\d{2}")) {
            throw new IllegalArgumentException("Placa inválida. Formatos aceitos: ABC1234 ou ABC1D23");
        }

        return new Placa(cleanedValue);
    }

    // --- getter ---

    public String getValor() { return valor; }

    // --- business method ---

    public String getFormatted() {
        return valor.substring(0, 3) + "-" + valor.substring(3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(valor, ((Placa) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return getFormatted(); }
}
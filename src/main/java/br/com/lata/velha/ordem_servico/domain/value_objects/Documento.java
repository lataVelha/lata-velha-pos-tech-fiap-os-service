package br.com.lata.velha.ordem_servico.domain.value_objects;

import java.util.Objects;

public class Documento {

    private final String valor;
    private final Tipo tipo;

    public enum Tipo { CPF, CNPJ }

    private Documento(String valor, Tipo tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public static Documento of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Documento não pode ser vazio");
        }

        String cleaned = valor.replaceAll("[^\\dA-Za-z]", "").toUpperCase();

        if (cleaned.matches("\\d{11}")) {
            if (!isValidCpf(cleaned)) {
                throw new IllegalArgumentException("CPF inválido");
            }
            return new Documento(cleaned, Tipo.CPF);
        }

        if (cleaned.length() == 14 && cleaned.matches("[A-Z0-9]{14}")) {
            if (cleaned.matches("\\d{14}") && !isValidCnpj(cleaned)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }
            return new Documento(cleaned, Tipo.CNPJ);
        }

        throw new IllegalArgumentException(
                "Documento inválido. CPF: 11 dígitos. CNPJ: 14 caracteres (numérico ou alfanumérico)");
    }

    // -------------------- CPF VALIDATION --------------------

    private static boolean isValidCpf(String cpf) {
        if (cpf.chars().distinct().count() == 1) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;
        if (firstDigit != (cpf.charAt(9) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;
        return secondDigit == (cpf.charAt(10) - '0');
    }

    // -------------------- CNPJ VALIDATION --------------------

    private static boolean isValidCnpj(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) return false;

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (cnpj.charAt(i) - '0') * weights1[i];
        }
        int firstDigit = sum % 11;
        firstDigit = firstDigit < 2 ? 0 : 11 - firstDigit;
        if (firstDigit != (cnpj.charAt(12) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += (cnpj.charAt(i) - '0') * weights2[i];
        }
        int secondDigit = sum % 11;
        secondDigit = secondDigit < 2 ? 0 : 11 - secondDigit;
        return secondDigit == (cnpj.charAt(13) - '0');
    }

    // -------------------- GETTERS --------------------

    public String getValor() { return valor; }

    public Tipo getTipo() { return tipo; }

    public String getFormatted() {
        if (tipo == Tipo.CPF) {
            return valor.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return valor.substring(0, 2) + "." +
               valor.substring(2, 5) + "." +
               valor.substring(5, 8) + "/" +
               valor.substring(8, 12) + "-" +
               valor.substring(12, 14);
    }

    public String getMasked() {
        String formatted = getFormatted();
        if (tipo == Tipo.CPF) {
            return "***." + formatted.substring(4, 11) + "-**";
        }
        return "**." + formatted.substring(3, 15) + "-**";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(valor, ((Documento) o).valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return getFormatted(); }
}
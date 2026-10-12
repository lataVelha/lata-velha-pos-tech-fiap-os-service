package br.com.lata.velha.authentication.domain.value_objects;

import java.util.Objects;

/**
 * Value Object que encapsula a senha em texto plano do usuário.
 * Responsável por validar as regras de complexidade da senha.
 */
public final class Senha {
    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 712;
    public static final int MIN_NUMBERS = 1;
    public static final int MIN_SPECIAL_CHARACTERS = 1;
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

    private final String valor;

    private Senha(String valor) {
        validateSenha(valor);
        this.valor = valor;
    }

    public static Senha fromString(String senha) {
        return new Senha(senha);
    }

    public String getValor() {
        return valor;
    }

    private static void validateSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }

        if (senha.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Senha deve ter no mínimo %d caracteres", MIN_LENGTH));
        }

        if (senha.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Senha deve ter no máximo %d caracteres", MAX_LENGTH));
        }

        long digitCount = senha.chars().filter(Character::isDigit).count();
        if (digitCount < MIN_NUMBERS) {
            throw new IllegalArgumentException(
                    String.format("Senha deve conter no mínimo %d número(s)", MIN_NUMBERS));
        }

        long specialCount = senha.chars()
                .filter(c -> SPECIAL_CHARACTERS.indexOf(c) >= 0)
                .count();
        if (specialCount < MIN_SPECIAL_CHARACTERS) {
            throw new IllegalArgumentException(
                    String.format("Senha deve conter no mínimo %d caractere(s) especial(is)", MIN_SPECIAL_CHARACTERS));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Senha that)) return false;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public String toString() {
        return "Senha{***}";
    }
}
package br.com.lata.velha.authentication.domain.entities;

import br.com.lata.velha.authentication.domain.exceptions.InvalidLoginException;
import br.com.lata.velha.authentication.domain.value_objects.Credential;
import br.com.lata.velha.authentication.domain.value_objects.UserData;
import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.time.LocalDateTime;
import java.util.Set;

public final class User {
    private final UserId id;
    private final String username;
    private final Email email;
    private final Credential credential;
    private final Set<Role> roles;
    private boolean ativo;
    private final LocalDateTime criacaoDate;
    private LocalDateTime ultimoLoginDate;
    private final String cpf;

    public User(UserData userData, Credential credential, Set<Role> roles, LocalDateTime criacaoDate, LocalDateTime ultimoLoginDate) {
        this(userData, credential, roles, criacaoDate, ultimoLoginDate, null);
    }

    public User(UserData userData, Credential credential, Set<Role> roles, LocalDateTime criacaoDate, LocalDateTime ultimoLoginDate, String cpf) {
        this.id = userData.id();
        this.username = userData.username();
        this.email = userData.email();
        this.ativo = userData.isActive();
        this.credential = credential;
        this.roles = roles;
        this.criacaoDate = criacaoDate;
        this.ultimoLoginDate = ultimoLoginDate;
        this.cpf = cpf;
    }

    public static User create(Email email, Credential credential, Set<Role> roles, String cpf) {
        validateCpf(cpf);
        var id = UserId.random();
        var username = email.toString();
        var userData = new UserData(id, username, email, true);
        return new User(userData, credential, roles, LocalDateTime.now(), null, cpf);
    }

    private static void validateCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}"))
            throw new IllegalArgumentException("CPF do usuário é obrigatório e deve conter 11 dígitos numéricos");
    }

    public boolean login(String senha) {
        if(!isAtivo()) throw new InvalidLoginException("Usuário inativo");
        if (!credential.match(senha)) throw new InvalidLoginException();
        this.ultimoLoginDate = LocalDateTime.now();
        return true;
    }

    public void addRole(Role role) {
        if(role == null)
            throw new IllegalArgumentException("Tentando inserir uma role null ao usuário: " + id.toString());
        this.roles.add(role);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    //region GETTERS
    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public Credential getCredential() {
        return credential;
    }

    public LocalDateTime getCriacaoDate() {
        return criacaoDate;
    }

    public LocalDateTime getUltimoLoginDate() {
        return ultimoLoginDate;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getCpf() {
        return cpf;
    }
    //endregion
}


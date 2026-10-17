package br.com.lata.velha.authentication.infrastructure.persistence.entities;

import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.value_objects.Credential;
import br.com.lata.velha.authentication.domain.value_objects.UserData;
import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Column(name = "USER_NAME", nullable = false)
    private String username;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "CREDENTIAL", nullable = false)
    private String credential;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<RoleEntity> roles;

    @Column(name = "ATIVO", nullable = false)
    private boolean ativo;

    @Column(name = "CRIACAO_DATE", nullable = false)
    private LocalDateTime criacaoDate;

    @Column(name = "ULTIMO_LOGIN_DATE")
    private LocalDateTime ultimoLoginDate;

    @Column(name = "CPF", nullable = false, unique = true)
    private String cpf;

    public static UserEntity fromDomain(User user) {
        var roles = user.getRoles().stream()
                .map(RoleEntity::fromDomain)
                .collect(Collectors.toSet());
        return new UserEntity(
                user.getId().getValue(),
                user.getUsername(),
                user.getEmail().getValor(),
                user.getCredential().getHash(),
                roles,
                user.isAtivo(),
                user.getCriacaoDate(),
                user.getUltimoLoginDate(),
                user.getCpf()
        );
    }

    public User toDomain(PasswordHasher passwordHasher) {
        var roleEntities = this.roles.stream()
                .map(RoleEntity::toDomain)
                .collect(Collectors.toSet());
        var userData = new UserData(UserId.create(this.id), this.username, Email.fromString(this.email), this.isAtivo());
        return new User(
            userData,
            Credential.fromHash(this.credential, passwordHasher),
            roleEntities,
            this.criacaoDate,
            this.ultimoLoginDate,
            this.cpf
        );
    }
}

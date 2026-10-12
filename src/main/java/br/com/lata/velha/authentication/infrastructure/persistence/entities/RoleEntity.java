package br.com.lata.velha.authentication.infrastructure.persistence.entities;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.shared.domain.value_objects.RoleId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "ROLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Column(name = "NOME", nullable = false, unique = true)
    private String nome;

    public static RoleEntity fromDomain(Role role) {
        return new RoleEntity(role.getRoleId().getValue(), role.getNome());
    }

    public Role toDomain() {
        return new Role(RoleId.create(this.id), this.nome);
    }
}
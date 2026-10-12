package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.FuncionarioEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.shared.domain.value_objects.RoleId;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FuncionarioPersistenceMapper {

    private final PasswordHasher passwordHasher;

    public Funcionario toDomain(FuncionarioEntity entity) {
        if (entity == null) return null;

        return new Funcionario(
                entity.getId(),
                entity.getNome(),
                toDomain(entity.getCargo()),
                UserId.create(entity.getUserId())
        );
    }

    public Cargo toDomain(CargoEntity entity) {
        if (entity == null) return null;

        Set<Role> roles = entity.getRoles() != null
                ? entity.getRoles().stream().map(this::toDomain).collect(Collectors.toSet())
                : Collections.emptySet();

        return new Cargo(entity.getId(), entity.getNome(), roles);
    }

    public Role toDomain(RoleEntity entity) {
        if (entity == null) return null;
        return new Role(RoleId.create(entity.getId()), entity.getNome());
    }

    public FuncionarioEntity toEntity(Funcionario model) {
        if (model == null) return null;

        var entity = new FuncionarioEntity();
        entity.setId(model.getId());
        entity.setNome(model.getNome());
        entity.setUserId(model.getUserId().getValue());
        entity.setCargo(toEntity(model.getCargo()));
        return entity;
    }

    public CargoEntity toEntity(Cargo model) {
        if (model == null) return null;
        var entity = new CargoEntity();
        entity.setId(model.getId());
        entity.setNome(model.getNome());
        return entity;
    }
}

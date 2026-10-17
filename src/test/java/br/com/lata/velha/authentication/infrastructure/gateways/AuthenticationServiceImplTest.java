package br.com.lata.velha.authentication.infrastructure.gateways;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.repositories.RoleRepository;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.value_objects.Credential;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserDto;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.RoleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private AuthenticationServiceImpl service;

    @Test
    @DisplayName("getRolesForCargo deve retornar nomes das roles do cargo")
    void shouldReturnRoleNamesForCargo() {
        var role1 = new Role(RoleId.create(UUID.randomUUID()), "MECANICO");
        var role2 = new Role(RoleId.create(UUID.randomUUID()), "TECNICO");
        var cargo = new Cargo(1L, "MECANICO", Set.of(role1, role2));

        when(cargoRepository.getByIdWithRoles(1L)).thenReturn(cargo);

        var result = service.getRolesForCargo(1L);

        assertThat(result).containsExactlyInAnyOrder("MECANICO", "TECNICO");
        verify(cargoRepository).getByIdWithRoles(1L);
    }

    @Test
    @DisplayName("getRolesForCargo deve retornar conjunto vazio quando cargo não tem roles")
    void shouldReturnEmptySetWhenCargoHasNoRoles() {
        var cargo = new Cargo(1L, "CARGO_SEM_ROLE", null);
        when(cargoRepository.getByIdWithRoles(1L)).thenReturn(cargo);

        var result = service.getRolesForCargo(1L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("createUser deve criar usuário e retornar seu userId")
    void shouldCreateUserAndReturnUserId() {
        var role = new Role(RoleId.create(UUID.randomUUID()), "MECANICO");
        var dto = new CreateAuthUserDto("novo@example.com", "Senha1@!", Set.of("MECANICO"), "44455566619");
        var credential = Credential.fromHash("hashed_password", passwordHasher);
        var savedUser = User.create(Email.fromString("novo@example.com"), credential, Set.of(role), "44455566619");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(roleRepository.getByNomes(Set.of("MECANICO"))).thenReturn(Set.of(role));
        when(passwordHasher.hashSenha(any())).thenReturn("hashed_password");
        when(userRepository.save(any())).thenReturn(savedUser);

        var response = service.createUser(dto);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(savedUser.getId().getValue());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser deve lançar ResourceAlreadyExistsException quando email já existe")
    void shouldThrowWhenEmailAlreadyExists() {
        var dto = new CreateAuthUserDto("existente@example.com", "Senha1@!", Set.of("MECANICO"), "55566677720");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> service.createUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser deve associar roles ao usuário criado")
    void shouldAssignRolesToCreatedUser() {
        var role = new Role(RoleId.create(UUID.randomUUID()), "MECANICO");
        var dto = new CreateAuthUserDto("novo@example.com", "Senha1@!", Set.of("MECANICO"), "66677788830");
        var credential = Credential.fromHash("hashed_password", passwordHasher);
        var savedUser = User.create(Email.fromString("novo@example.com"), credential, Set.of(role), "66677788830");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(roleRepository.getByNomes(Set.of("MECANICO"))).thenReturn(Set.of(role));
        when(passwordHasher.hashSenha(any())).thenReturn("hashed_password");
        when(userRepository.save(any())).thenReturn(savedUser);

        service.createUser(dto);

        verify(roleRepository).getByNomes(Set.of("MECANICO"));
        verify(userRepository).save(argThat(user -> user.getRoles().contains(role)));
    }
}

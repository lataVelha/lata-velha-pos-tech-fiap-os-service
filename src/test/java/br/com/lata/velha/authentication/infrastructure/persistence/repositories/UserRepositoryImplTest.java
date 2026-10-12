package br.com.lata.velha.authentication.infrastructure.persistence.repositories;

import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions.UserNotFoundException;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.value_objects.Credential;
import br.com.lata.velha.authentication.domain.value_objects.UserData;
import br.com.lata.velha.authentication.infrastructure.persistence.entities.UserEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private Logger logger;

    @InjectMocks
    private UserRepositoryImpl repository;

    private UUID rawUuid;
    private UserId userId;
    private UserEntity userEntity;
    private User domainUser;

    @BeforeEach
    void setUp() {
        rawUuid = UUID.randomUUID();
        userId = UserId.create(rawUuid);
        userEntity = new UserEntity(rawUuid, "joao", "joao@example.com", "hash", Set.of(), true,
                LocalDateTime.now(), null, null);
        Credential credential = Credential.fromHash("hash", passwordHasher);
        var userData = new UserData(userId, "joao", Email.fromString("joao@example.com"), true);
        domainUser = new User(userData, credential, Set.of(), LocalDateTime.now(), null);
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("deve retornar User quando encontrado")
        void shouldReturnUserWhenFound() {
            when(jpaRepository.findById(rawUuid)).thenReturn(Optional.of(userEntity));

            User result = repository.getById(userId);

            assertNotNull(result);
            assertEquals("joao", result.getUsername());
        }

        @Test
        @DisplayName("deve lançar UserNotFoundException quando não encontrado")
        void shouldThrowWhenNotFound() {
            when(jpaRepository.findById(rawUuid)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> repository.getById(userId));
        }
    }

    @Nested
    @DisplayName("getByUsernameWithRoles")
    class GetByUsername {

        @Test
        @DisplayName("deve retornar User quando username existe")
        void shouldReturnUserByUsername() {
            when(jpaRepository.findByUsernameWithRoles("joao")).thenReturn(Optional.of(userEntity));

            User result = repository.getByUsernameWithRoles("joao");

            assertNotNull(result);
            assertEquals("joao", result.getUsername());
        }

        @Test
        @DisplayName("deve lançar UserNotFoundException quando username não existe")
        void shouldThrowWhenUsernameNotFound() {
            when(jpaRepository.findByUsernameWithRoles("inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> repository.getByUsernameWithRoles("inexistente"));
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("deve retornar true quando email existe")
        void shouldReturnTrueWhenEmailExists() {
            when(jpaRepository.existsByEmail("joao@example.com")).thenReturn(true);

            assertTrue(repository.existsByEmail(Email.fromString("joao@example.com")));
        }

        @Test
        @DisplayName("deve retornar false quando email não existe")
        void shouldReturnFalseWhenEmailNotExists() {
            when(jpaRepository.existsByEmail("novo@example.com")).thenReturn(false);

            assertFalse(repository.existsByEmail(Email.fromString("novo@example.com")));
        }
    }

    @Nested
    @DisplayName("isActiveById")
    class IsActiveById {

        @Test
        @DisplayName("deve retornar true para usuário ativo")
        void shouldReturnTrueForActiveUser() {
            when(jpaRepository.findById(rawUuid)).thenReturn(Optional.of(userEntity));

            assertTrue(repository.isAtivoById(userId));
        }

        @Test
        @DisplayName("deve retornar false para usuário inativo")
        void shouldReturnFalseForInactiveUser() {
            UserEntity inactiveEntity = new UserEntity(rawUuid, "joao", "joao@example.com", "hash",
                    Set.of(), false, LocalDateTime.now(), null, null);
            when(jpaRepository.findById(rawUuid)).thenReturn(Optional.of(inactiveEntity));

            assertFalse(repository.isAtivoById(userId));
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("deve salvar e retornar User mapeado")
        void shouldSaveAndReturnMappedUser() {
            when(jpaRepository.save(any(UserEntity.class))).thenReturn(userEntity);

            User result = repository.save(domainUser);

            assertNotNull(result);
            assertEquals("joao", result.getUsername());
            verify(jpaRepository).save(any(UserEntity.class));
        }
    }
}

package br.com.lata.velha.authentication.infrastructure.persistence.repositories;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions.RoleNotFoundException;
import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.jpa.RoleJpaRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryImplTest {

    @Mock
    private RoleJpaRepository jpaRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private RoleRepositoryImpl repository;

    @Test
    @DisplayName("getByNome deve retornar Role quando encontrada")
    void shouldReturnRoleWhenFound() {
        RoleEntity entity = new RoleEntity(UUID.randomUUID(), "ADMIN");
        when(jpaRepository.findByNome("ADMIN")).thenReturn(Optional.of(entity));

        Role result = repository.getByNome("ADMIN");

        assertNotNull(result);
        assertEquals("ADMIN", result.getNome());
        verify(jpaRepository).findByNome("ADMIN");
    }

    @Test
    @DisplayName("getByNome deve lançar RoleNotFoundException quando não encontrada")
    void shouldThrowWhenRoleNotFound() {
        when(jpaRepository.findByNome("INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> repository.getByNome("INEXISTENTE"));
    }

    @Test
    @DisplayName("getByNome deve mapear corretamente o nome da role")
    void shouldMapRoleNameCorrectly() {
        RoleEntity entity = new RoleEntity(UUID.randomUUID(), "MECANICO");
        when(jpaRepository.findByNome("MECANICO")).thenReturn(Optional.of(entity));

        Role result = repository.getByNome("MECANICO");

        assertEquals("MECANICO", result.getNome());
        assertNotNull(result.getRoleId());
    }

    @Test
    @DisplayName("getByNomes deve retornar conjunto de roles correspondentes aos nomes informados")
    void shouldReturnRolesForGivenNames() {
        var entity1 = new RoleEntity(UUID.randomUUID(), "MECANICO");
        var entity2 = new RoleEntity(UUID.randomUUID(), "ADMIN");
        when(jpaRepository.findAllByNomeIn(Set.of("MECANICO", "ADMIN"))).thenReturn(Set.of(entity1, entity2));

        Set<Role> result = repository.getByNomes(Set.of("MECANICO", "ADMIN"));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> "MECANICO".equals(r.getNome())));
        assertTrue(result.stream().anyMatch(r -> "ADMIN".equals(r.getNome())));
        verify(jpaRepository).findAllByNomeIn(Set.of("MECANICO", "ADMIN"));
    }

    @Test
    @DisplayName("getByNomes deve retornar conjunto vazio quando nenhuma role for encontrada")
    void shouldReturnEmptySetWhenNoRolesFound() {
        when(jpaRepository.findAllByNomeIn(Set.of("INEXISTENTE"))).thenReturn(Set.of());

        Set<Role> result = repository.getByNomes(Set.of("INEXISTENTE"));

        assertTrue(result.isEmpty());
    }
}

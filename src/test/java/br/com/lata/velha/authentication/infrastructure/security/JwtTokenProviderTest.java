package br.com.lata.velha.authentication.infrastructure.security;

import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Logger logger;

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(jwtEncoder, "lata-velha", 3600L, logger);
    }

    private Jwt stubEncoder(String tokenValue) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn(tokenValue);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        return jwt;
    }

    @Test
    @DisplayName("deve retornar o valor do token gerado pelo encoder")
    void shouldReturnTokenValueFromEncoder() {
        stubEncoder("mocked-token");

        String token = tokenProvider.generate(UserId.random(), "ROLE_ADMIN");

        assertEquals("mocked-token", token);
    }

    @Test
    @DisplayName("deve definir o subject do token com o id do usuário")
    void shouldSetSubjectToUserId() {
        stubEncoder("token");
        UserId userId = UserId.random();

        tokenProvider.generate(userId, "ROLE_USER");

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        assertEquals(userId.toString(), captor.getValue().getClaims().getSubject());
    }

    @Test
    @DisplayName("deve definir o issuer configurado no token")
    void shouldSetConfiguredIssuer() {
        stubEncoder("token");

        tokenProvider.generate(UserId.random(), "ROLE_ADMIN");

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        assertEquals("lata-velha", captor.getValue().getClaims().getClaim("iss"));
    }

    @Test
    @DisplayName("deve incluir os scopes no claim 'scope'")
    void shouldIncludeScopesInScopeClaim() {
        stubEncoder("token");

        tokenProvider.generate(UserId.random(), "ADMIN USER");

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        assertEquals("ADMIN USER", captor.getValue().getClaims().getClaim("scope"));
    }

    @Test
    @DisplayName("deve definir expiração como now + expiresIn segundos")
    void shouldSetExpiryToNowPlusExpiresIn() {
        stubEncoder("token");
        Instant before = Instant.now();

        tokenProvider.generate(UserId.random(), "ROLE_ADMIN");

        Instant after = Instant.now();
        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        JwtClaimsSet claims = captor.getValue().getClaims();
        assertNotNull(claims.getExpiresAt());
        assertTrue(claims.getExpiresAt().isAfter(before.plusSeconds(3599)));
        assertTrue(claims.getExpiresAt().isBefore(after.plusSeconds(3601)));
    }

    @Test
    @DisplayName("deve retornar o tempo de expiração configurado")
    void shouldReturnConfiguredExpiresIn() {
        assertEquals(3600L, tokenProvider.getExpiresIn());
    }
}

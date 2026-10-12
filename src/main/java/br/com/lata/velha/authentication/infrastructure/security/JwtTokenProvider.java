package br.com.lata.velha.authentication.infrastructure.security;

import br.com.lata.velha.authentication.application.gateways.TokenProvider;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expiresIn;
    private final Logger logger;

    public JwtTokenProvider(
            JwtEncoder jwtEncoder,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expires-in}") long expiresIn,
            Logger logger) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expiresIn = expiresIn;
        this.logger = logger;
    }

    @Override
    public String generate(UserId userId, String scopes) {
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        logger.logInfo("Token JWT gerado - userId={}, expiresIn={}s", userId, expiresIn);
        return token;
    }

    @Override
    public long getExpiresIn() {
        return expiresIn;
    }
}

package system.payments.poc.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import system.payments.poc.service.JwtService;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

@Slf4j
@Service
public class DefaultJwtService implements JwtService {
    private final Long jwtTokenValidity;
    private final Algorithm hmac512;
    private final JWTVerifier verifier;
    private final String jwtIssuer;

    public DefaultJwtService(@Value("${jwt.secret}") final String secret, @Value("${jwt.issuer}") final String jwtIssuer,
                             @Value("${jwt.token_validity}") final Long jwtTokenValidity) {
        this.jwtTokenValidity = jwtTokenValidity;
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
        this.jwtIssuer = jwtIssuer;
    }

    public String generateToken(final UserDetails userDetails) {
        final Instant now = Instant.now();
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", new ArrayList<>(userDetails.getAuthorities()).get(0).getAuthority().toUpperCase().substring(5))
                .withIssuer(this.jwtIssuer)
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(Duration.ofMinutes(jwtTokenValidity).toMillis()))
                .sign(this.hmac512);
    }

    public String validateTokenAndGetUsername(String token) {
        log.info("Checking for LOCAL token login...");
        return verifier.verify(token).getSubject();
    }
}

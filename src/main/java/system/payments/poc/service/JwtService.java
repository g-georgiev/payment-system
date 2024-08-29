package system.payments.poc.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateToken(final UserDetails userDetails);

    String validateTokenAndGetUsername(final String token);
}

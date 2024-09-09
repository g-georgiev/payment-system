package system.payments.poc.service.impl;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import system.payments.poc.service.JwtService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DefaultJwtServiceTest {
    private JwtService jwtTokenService;

    @BeforeEach
    public void setUp() {
        jwtTokenService = new DefaultJwtService(
                "MIIBPQIBAAJBANkpF/lVw/COC3QGJmWfw5Kay97N7+O2LaKdo+eWuEFnCxGEiB67Q7/q7ntJeDg+vxMXK30w8w18e2Xjd9AqFY8CAwEAAQJBAII9RBSUnGwENNhD6/lc5QVi80xQBER0eVAi7S8y1JTb5Sqc/NPFT9KqeI8RpEv+uRx5K24hFzkkAFxRH2EggtkCIQD/hxD/7VrrRmWqAHicbmm9yvMktT53kCKNPspVbnUNewIhANmP3ocFpw4ghdN/tLgjQE1eCUIMhVvGicx9mK1WpFn9AiEAt0SMMMJv3ybSa6eC2c9nD7SrxrwnXWGHJn7OZ4+dbFkCIQCQeB+9q3w+Pkx/oHriF854Uax2Jphb1B/nIbPDzxf+BQIhAI5O3sI8LYKvBJhBTnzzWfjYQMaRmHMyz+0ZBbgfwXPq",
                "SmartMenuApp", 120L);
    }

    @Test
    public void generateAndValidateTest() {
        String username = "username";
        UserDetails userDetails = new User(username, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String token = jwtTokenService.generateToken(userDetails);
        String usernameFromToken = jwtTokenService.validateTokenAndGetUsername(token);

        assertEquals(username, usernameFromToken);

        assertThrows(JWTDecodeException.class, () -> jwtTokenService.validateTokenAndGetUsername("12345678"));
    }
}

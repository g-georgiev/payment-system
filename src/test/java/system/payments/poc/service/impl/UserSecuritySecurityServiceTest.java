package system.payments.poc.service.impl;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import system.payments.poc.enums.Role;
import system.payments.poc.mapper.UserMapper;
import system.payments.poc.model.UserCredentials;
import system.payments.poc.repository.UserCredentialsRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSecuritySecurityServiceTest {

    private DefaultUserCredentialsService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserCredentialsRepository userRepository;
    @Mock
    private DefaultJwtService jwtTokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;

    public UserSecuritySecurityServiceTest() {
    }

    @BeforeEach
    public void setUp() {
        this.userService = new DefaultUserCredentialsService(userRepository, authenticationManager,
                userDetailsService, jwtTokenService, userMapper);
    }

    @ParameterizedTest
    @CsvSource({"username, password", "mail@example.com, password"})
    public void createUser_Success(String username, String password) {
        when(userRepository.existsByUsername(username)).thenReturn(false);
        assertDoesNotThrow(() -> userService.createUser(username, password));
        verify(userRepository, times(1)).save(any(UserCredentials.class));
    }

    @Test
    public void createUser_Failed() {
        when(userRepository.existsByUsername(any())).thenReturn(true);
        assertThrows(EntityExistsException.class, () -> userService.createUser("username", "password"));
        verify(userRepository, times(0)).save(any(UserCredentials.class));
    }

    @Test
    public void authenticate_Success() {
        String username = "user";
        String password = "password";
        String token = "<token>";

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, password,
                Collections.singletonList(new SimpleGrantedAuthority(Role.MERCHANT.toString())));

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password))).thenReturn(new TestingAuthenticationToken("test", "test"));
        when(jwtTokenService.generateToken(userDetails)).thenReturn(token);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        assertEquals(token, userService.authenticateUser(username, password));
    }

}

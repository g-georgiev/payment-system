package system.payments.poc.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import system.payments.poc.model.Merchant;
import system.payments.poc.repository.UserCredentialsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSecurityDetailsServiceTest {

    private UserDetailsService userDetailsService;
    @Mock
    private UserCredentialsRepository userRepository;

    @BeforeEach
    public void setUp() {
        this.userDetailsService = new DefaultUserDetailsService(userRepository);
    }

    @Test
    public void loadUserByUsername_ExistingUser() {
        String usernameToSearch = "merchant";
        Merchant merchant = new Merchant();
        merchant.setUsername(usernameToSearch);

        when(userRepository.findByUsername(usernameToSearch)).thenReturn(Optional.of(merchant));

        assertEquals(usernameToSearch, userDetailsService.loadUserByUsername(usernameToSearch).getUsername());
        verify(userRepository, times(1)).findByUsername(usernameToSearch);
    }

}
package system.payments.poc.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import system.payments.poc.dto.AdminDto;
import system.payments.poc.mapper.AdminMapper;
import system.payments.poc.model.Admin;
import system.payments.poc.repository.AdminRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSecuritySecurityServiceTest {

    @Mock
    private AdminMapper adminMapper;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private DefaultJwtService jwtTokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private DefaultUserCredentialsService userService;

    public UserSecuritySecurityServiceTest() {
    }

    @Test
    public void authenticate_Success() {
        String username = "user";
        String password = "password";
        String token = "<token>";

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, password,
                Collections.singletonList(new SimpleGrantedAuthority("merchant")));

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password))).thenReturn(new TestingAuthenticationToken("test", "test"));
        when(jwtTokenService.generateToken(userDetails)).thenReturn(token);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        assertEquals(token, userService.authenticateUser(username, password));
    }

    @Test
    public void create_admin_when_username_does_not_exist() {
        String username = "newadmin";
        String password = "password";

        when(adminRepository.existsByUsername(username)).thenReturn(false);
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createAdmin(username, password);

        verify(adminRepository, times(1)).save(any(Admin.class));
        verify(adminMapper, times(1)).mapToDTO(any(Admin.class));
    }

    @Test
    public void do_not_create_admin_if_username_already_exists() {
        String username = "existingadmin";
        String password = "password";

        when(adminRepository.existsByUsername(username)).thenReturn(true);

        userService.createAdmin(username, password);

        verify(adminRepository, never()).save(any());
        verify(adminMapper, never()).mapToDTO(any());
    }


    // Returns UserDTO when admin with given username exists
    @Test
    public void returns_userdto_when_admin_exists() {
        // Arrange
        String username = "adminUser";
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setActive(true);
        AdminDto expectedUserDTO = new AdminDto();
        expectedUserDTO.setUsername(username);
        expectedUserDTO.setActive(true);

        when(adminRepository.findByUsername(username)).thenReturn(admin);
        when(adminMapper.mapToDTO(admin)).thenReturn(expectedUserDTO);

        // Act
        AdminDto result = userService.getAdminByUserName(username);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUserDTO.getUsername(), result.getUsername());
    }

    @Test
    public void returns_null_when_admin_does_not_exist() {
        // Arrange
        String username = "nonExistingAdmin";
        when(adminRepository.findByUsername(username)).thenReturn(null);

        // Act
        AdminDto result = userService.getAdminByUserName(username);

        // Assert
        assertNull(result);
    }
}

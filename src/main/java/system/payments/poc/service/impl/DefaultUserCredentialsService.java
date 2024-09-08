package system.payments.poc.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import system.payments.poc.dto.AdminDto;
import system.payments.poc.mapper.AdminMapper;
import system.payments.poc.model.Admin;
import system.payments.poc.model.security.UserSecurity;
import system.payments.poc.repository.AdminRepository;
import system.payments.poc.service.JwtService;
import system.payments.poc.service.UserCredentialsService;

import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultUserCredentialsService implements UserCredentialsService {

    private AdminRepository adminRepository;

    private AuthenticationManager authenticationManager;

    private UserDetailsService userDetailsService;
    private JwtService jwtTokenService;

    private AdminMapper userMapper;

    @Override
    public UserSecurity getCurrentUserCredentials() {
        return (UserSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    @Transactional
    public String authenticateUser(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return jwtTokenService.generateToken(userDetailsService.loadUserByUsername(username));
    }

    @Override
    @Transactional
    public void createAdmin(String username, String password) {
        if (!adminRepository.existsByUsername(username)) {
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(password);
            admin.setActive(true);
            userMapper.mapToDTO(adminRepository.save(admin));
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminDto getAdminByUserName(String username) {
        Admin admin = adminRepository.findByUsername(username);
        if (Objects.nonNull(admin)) {
            return userMapper.mapToDTO(admin);
        }
        return null;
    }

}
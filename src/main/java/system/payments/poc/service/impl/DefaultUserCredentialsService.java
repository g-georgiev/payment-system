package system.payments.poc.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import system.payments.poc.dto.UserDTO;
import system.payments.poc.enums.Role;
import system.payments.poc.mapper.UserMapper;
import system.payments.poc.model.UserCredentials;
import system.payments.poc.model.security.UserSecurity;
import system.payments.poc.repository.UserCredentialsRepository;
import system.payments.poc.service.JwtService;
import system.payments.poc.service.UserCredentialsService;

import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultUserCredentialsService implements UserCredentialsService {

    private UserCredentialsRepository userRepository;

    private AuthenticationManager authenticationManager;

    private UserDetailsService userDetailsService;
    private JwtService jwtTokenService;

    private UserMapper userMapper;

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
    public UserDTO createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new EntityExistsException(username);
        }

        return userMapper.mapToDTO(userRepository.save(UserCredentials.builder()
                .username(username)
                .password(prepareEncryptedPassword(password))
                .role(Role.MERCHANT)
                .isActive(true)
                .build()));
    }

    @Override
    @Transactional
    public void createAdmin(String username, String password) {
        if (!userRepository.existsByUsername(username)) {
            userMapper.mapToDTO(userRepository.save(UserCredentials.builder()
                    .username(username)
                    .password(prepareEncryptedPassword(password))
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build()));
        }
    }

    private String prepareEncryptedPassword(String password) {
        String encryptedPassword = null;
        if (Objects.nonNull(password)) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            encryptedPassword = bCryptPasswordEncoder.encode(password);
        }

        return encryptedPassword;
    }


    @Transactional
    public UserDTO getUserByUserName(String username) {
        return userMapper.mapToDTO(userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(username)));
    }

}
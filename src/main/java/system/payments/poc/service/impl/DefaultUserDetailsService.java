package system.payments.poc.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import system.payments.poc.model.UserCredentials;
import system.payments.poc.model.security.UserSecurity;
import system.payments.poc.repository.UserCredentialsRepository;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

    private UserCredentialsRepository userRepository;

    @Override
    @Transactional
    public UserSecurity loadUserByUsername(final String username) {
        log.info("----- loadUserByUsername() -----");
        UserCredentials userCredentials = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        UserSecurity userSecurity = new UserSecurity(userCredentials);
        log.info("Existing userSecurity: {}, Role: {}", userSecurity.getUsername(), userCredentials.getRole());
        return userSecurity;
    }
}
package system.payments.poc.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import system.payments.poc.model.UserCredentials;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Slf4j
public class UserSecurity implements UserDetails {
    private UserCredentials userCredentials;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.userCredentials.getRole()));
    }

    @Override
    public String getPassword() {
        return userCredentials.getPassword();
    }

    @Override
    public String getUsername() {
        return userCredentials.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        log.info("isAccountNonExpired: {}", this.userCredentials.isActive());
        return this.userCredentials.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        log.info("isAccountNonLocked: {}", this.userCredentials.isActive());
        return this.userCredentials.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        log.info("isCredentialsNonExpired: {}", this.userCredentials.isActive());
        return this.userCredentials.isActive();
    }

    @Override
    public boolean isEnabled() {
        log.info("isEnabled: {}", this.userCredentials.isActive());
        return this.userCredentials.isActive();
    }
}

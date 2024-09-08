package system.payments.poc.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import system.payments.poc.enums.MerchantStatus;
import system.payments.poc.model.Admin;
import system.payments.poc.model.Merchant;
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
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.userCredentials.getRole().toUpperCase()));
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
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
        boolean isActive = false;
        if (this.userCredentials instanceof Admin) {
            isActive = ((Admin) this.userCredentials).isActive();
        } else if (this.userCredentials instanceof Merchant) {
            isActive = ((Merchant) this.userCredentials).getStatus().equals(MerchantStatus.ACTIVE);
        }

        log.info("isEnabled: {}", isActive);
        return isActive;
    }
}

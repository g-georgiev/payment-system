package system.payments.poc.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import system.payments.poc.service.JwtService;

import java.io.IOException;


@AllArgsConstructor
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserDetailsService jwtUserDetailsService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {
        log.info("Do internal REQUEST filter...");
        log.info("---> request url: " + request.getRequestURL());
        log.info("---> requested URI: " + request.getRequestURI());
        log.info("---> method: " + request.getMethod());
        log.info("---> path: " + request.getPathInfo());
        log.info("---> query string: " + request.getQueryString());

        log.info("Look for Bearer auth header");
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            log.info("There is NO header starting with Bearer!");
            return;
        }

        log.info("There IS Bearer auth header. Validating token ...");
        final String token = header.substring(7);
        final String username = jwtService.validateTokenAndGetUsername(token);
        if (username == null) {
            log.error("Validation failed or token expired");
            chain.doFilter(request, response);
            return;
        }

        log.info("Set user details on spring security context");
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);


        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}

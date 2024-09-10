package system.payments.poc.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import system.payments.poc.dto.AuthDTO;
import system.payments.poc.dto.UserDTO;
import system.payments.poc.service.UserCredentialsService;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final UserCredentialsService userCredentialsService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public String createToken(@Valid @RequestBody AuthDTO userDTO) {
        return userCredentialsService.authenticateUser(userDTO.getUsername(), userDTO.getPassword());
    }

    @GetMapping("/admin/{username}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    public UserDTO getUser(@PathVariable String username) {
        return userCredentialsService.getAdminByUserName(username);
    }
}

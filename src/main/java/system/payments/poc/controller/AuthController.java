package system.payments.poc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import system.payments.poc.dto.UserDTO;
import system.payments.poc.service.UserCredentialsService;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final UserCredentialsService userCredentialsService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userCredentialsService.createUser(userDTO.getUsername(), userDTO.getPassword());
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public String createToken(@RequestBody UserDTO userDTO) {
        return userCredentialsService.authenticateUser(userDTO.getUsername(), userDTO.getPassword());
    }

    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUser(@PathVariable String username) {
        return userCredentialsService.getUserByUserName(username);
    }
}

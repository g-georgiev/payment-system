package system.payments.poc.service;

import system.payments.poc.dto.UserDTO;
import system.payments.poc.model.security.UserSecurity;

public interface UserCredentialsService {
    UserSecurity getCurrentUserCredentials();

    String authenticateUser(String username, String password);

    void createAdmin(String username, String password);

    UserDTO getUserByUserName(String username);

    UserDTO createUser(String username, String password);
}

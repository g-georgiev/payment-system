package system.payments.poc.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import system.payments.poc.service.UserCredentialsService;

@Component
@RequiredArgsConstructor
public class UserCredentialsInit implements CommandLineRunner {
    private final UserCredentialsService userCredentialsService;

    @Override
    public void run(String... args) {
        userCredentialsService.createAdmin("admin", "nimda");
    }
}

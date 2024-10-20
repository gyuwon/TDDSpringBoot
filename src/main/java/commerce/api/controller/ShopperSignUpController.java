package commerce.api.controller;

import java.util.UUID;

import commerce.Patterns;
import commerce.Shopper;
import commerce.ShopperRepository;
import commerce.command.CreateShopperCommand;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record ShopperSignUpController(
    Pbkdf2PasswordEncoder passwordEncoder,
    ShopperRepository repository
) {

    @PostMapping("/shopper/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateShopperCommand command) {
        if (isCommandValid(command) == false) {
            return ResponseEntity.badRequest().build();
        }

        UUID id = UUID.randomUUID();
        String hashedPassword = passwordEncoder.encode(command.password());
        var shopper = new Shopper();
        shopper.setId(id);
        shopper.setEmail(command.email());
        shopper.setUsername(command.username());
        shopper.setHashedPassword(hashedPassword);

        try {
            repository.save(shopper);
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    private static boolean isCommandValid(CreateShopperCommand command) {
        return isEmailValid(command.email())
            && isUsernameValid(command.username())
            && isPasswordValid(command.password());
    }

    private static boolean isEmailValid(String email) {
        return email != null && email.matches(Patterns.EMAIL_REGEX);
    }

    private static boolean isUsernameValid(String username) {
        return username != null && username.matches(Patterns.USERNAME_REGEX);
    }

    private static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }
}
